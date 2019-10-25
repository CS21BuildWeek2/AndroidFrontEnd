package dev.vespertine.treasurehunt.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import dev.vespertine.treasurehunt.R
import dev.vespertine.treasurehunt.api.TreasureRoomApi
import dev.vespertine.treasurehunt.db.TreasureHuntDatabase
import dev.vespertine.treasurehunt.db.TreasureRoomTraversalDao
import dev.vespertine.treasurehunt.models.Direction
import dev.vespertine.treasurehunt.models.TreasureRoomData
import dev.vespertine.treasurehunt.models.TreasureRoomTraversal
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    private lateinit var traversalDisposable: CompositeDisposable

    //private var roomCount = 0
    private var timeWait : Double = 0.0
    private var moves : MutableList<String> = mutableListOf()
    private var rewindPath: MutableList<String> = mutableListOf()
    private var isRewind : Boolean = false

    lateinit var direction: Direction

    val roomCount: MutableLiveData<Int>
            by lazy { MutableLiveData<Int>() }

    val currentRoom : MutableLiveData<TreasureRoomData>
            by lazy { MutableLiveData<TreasureRoomData>() }

    val prevRoom : MutableLiveData<TreasureRoomData>
            by lazy { MutableLiveData<TreasureRoomData>() }

    lateinit var treasureRoomApi: TreasureRoomApi


    lateinit var treasureHuntDatabase: TreasureHuntDatabase


    lateinit var dao: TreasureRoomTraversalDao


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        treasureHuntDatabase = TreasureHuntDatabase.getDatabase(this)

        dao = treasureHuntDatabase.treasureDAO()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://lambda-treasure-hunt.herokuapp.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()

        treasureRoomApi = retrofit.create(TreasureRoomApi::class.java)





        compositeDisposable.add(
            treasureRoomApi.initializePlayer("Token 5fea1d71395c06916414b3ced62e3992b8b022ea")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess {
                    tv_room_name.text = it.title
                    tv_room_desc.text = it.description
                    dao.getRoomCount().subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                            {num -> roomCount.postValue(num)},
                            {}
                        )


                    currentRoom.postValue(it)

                }
                .subscribe({},{})
        )


        roomCount.observe(this, Observer<Int>{
            tv_room_count.text = it.toString()
        })

        currentRoom.observe(this, Observer<TreasureRoomData> {
            timeWait = it.cooldown
            moves.clear()


            tv_room_name.text = it.title + " - ID: " + it.room_id
            tv_room_desc.text = it.description
            val roomTrav = dao.getMTtreasureRoomID(it.room_id)




            if (roomTrav == null) {
                val rtTraversal = TreasureRoomTraversal(it.room_id, it.title, it.description)
                it.exits.forEach{
                    when(it) {
                        "n" ->{
                            rtTraversal.north = 666
                            moves.add("n")
                        }
                        "s"->{
                            rtTraversal.south = 666
                            moves.add("s")
                        }
                        "e"->{
                            rtTraversal.east = 666
                            moves.add("e")
                        }
                        "w"->{
                            rtTraversal.west = 666
                            moves.add("w")

                        }
                    }
                }

                if(moves.isEmpty()){
                    moves.addAll(it.exits)
                }



                dao.insert(rtTraversal)
                isRewind = false
            } else{
                tv_north_room_id.text = roomTrav.north.toString()
                tv_east_room_id.text = roomTrav.east.toString()
                tv_south_room_id.text = roomTrav.south.toString()
                tv_west_room_id.text = roomTrav.west.toString()

                if(roomTrav.north == 666) {
                    moves.add("n")
                }

                if(roomTrav.south == 666) {
                    moves.add("s")
                }

                if(roomTrav.east == 666) {
                    moves.add("e")
                }

                if(roomTrav.west == 666) {
                    moves.add("w")
                }

                if(moves.isEmpty()){
                    moves.addAll(it.exits)
                }


            }

            dao.getRoomCount().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {num -> roomCount.postValue(num)},
                    {})






        })

        prevRoom.observe(this, Observer<TreasureRoomData>{

        })

        button2.setOnClickListener {
            traversalDisposable.clear()
            traversalDisposable.dispose()
            button2.text = "Start"
        }

        button.setOnClickListener {
            button2.text = "Pause"
            traversalDisposable = CompositeDisposable()

            traversalDisposable.add(
                Observable.interval(15, TimeUnit.SECONDS)
                    .flatMap{it->
                        val randomDirect = moves.random()
                        direction = Direction(randomDirect)
                        when (randomDirect) {
                            "n"->{rewindPath.add("s")}
                            "s"->{rewindPath.add("n")}
                            "e"->{rewindPath.add("e")}
                            "w"->{rewindPath.add("w")}
                        }

                        treasureRoomApi.movePlayer(
                            "Token 5fea1d71395c06916414b3ced62e3992b8b022ea"
                            , direction).toObservable()

                    }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        {

                            //Check DB to see if next room is in DB
                            //Check to see if current room is in DB
                            var daoCheckNext = dao.getMTtreasureRoomID(it.room_id)

                            if (daoCheckNext == null) {
                                //Create new room traversal object to next room from sub
                                val id = it.room_id
                                val name = it.title
                                val desc = it.description
                                daoCheckNext = TreasureRoomTraversal(
                                    room_id = id,
                                    name = name,
                                    description = desc)

                                it.exits.forEach{
                                    when(it) {
                                        "n" ->{
                                            daoCheckNext.north = 666
                                        }
                                        "s"->{
                                            daoCheckNext.south = 666

                                        }
                                        "e"->{
                                            daoCheckNext.east = 666

                                        }
                                        "w"->{
                                            daoCheckNext.west = 666

                                        }
                                    }
                                }
                            }


                            //Directions

                            val currentRoomID = currentRoom.value?.room_id
                            when (direction.direction) {
                                "n"->{daoCheckNext.south = currentRoomID }
                                "s"->{daoCheckNext.north = currentRoomID}
                                "e"->{daoCheckNext.west = currentRoomID}
                                "w"->{daoCheckNext.east = currentRoomID}

                            }

                            dao.insert(daoCheckNext)

                            currentRoom.postValue(it)},
                        { Log.e("Traverse: ", "onError - " + it.message)},
                        {})
            )
        }

        tv_west.setOnClickListener {
            val west = Direction("w")
            treasureRoomApi.movePlayer("Token 5fea1d71395c06916414b3ced62e3992b8b022ea", west)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess {
                    currentRoom.postValue(it)
                }
                .subscribe()
                .dispose()
        }

        tv_east.setOnClickListener {
            val east = Direction("e")
            compositeDisposable.add(treasureRoomApi.movePlayer("Token 5fea1d71395c06916414b3ced62e3992b8b022ea", east)
                .toObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {

                        //Check DB to see if next room is in DB
                        //Check to see if current room is in DB
                        var daoCheckNext = dao.getMTtreasureRoomID(it.room_id)

                        if (daoCheckNext == null) {
                            //Create new room traversal object to next room from sub
                            val id = it.room_id
                            val name = it.title
                            val desc = it.description
                            daoCheckNext = TreasureRoomTraversal(
                                room_id = id,
                                name = name,
                                description = desc)

                            it.exits.forEach{
                                when(it) {
                                    "n" ->{
                                        daoCheckNext.north = 666
                                    }
                                    "s"->{
                                        daoCheckNext.south = 666

                                    }
                                    "e"->{
                                        daoCheckNext.east = 666

                                    }
                                    "w"->{
                                        daoCheckNext.west = 666

                                    }
                                }
                            }
                        }


                        //Directions

                        val currentRoomID = currentRoom.value?.room_id
                        when (direction.direction) {
                            "n"->{daoCheckNext.south = currentRoomID }
                            "s"->{daoCheckNext.north = currentRoomID}
                            "e"->{daoCheckNext.west = currentRoomID}
                            "w"->{daoCheckNext.east = currentRoomID}

                        }

                        dao.insert(daoCheckNext)

                        currentRoom.postValue(it)},
                    { Log.e("Traverse: ", "onError - " + it.message)},

                    {})
            )
        }


        tv_north.setOnClickListener {
            val north = Direction("n")
            compositeDisposable.add(treasureRoomApi.movePlayer("Token 5fea1d71395c06916414b3ced62e3992b8b022ea", north)
                .toObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {

                        //Check DB to see if next room is in DB
                        //Check to see if current room is in DB
                        var daoCheckNext = dao.getMTtreasureRoomID(it.room_id)

                        if (daoCheckNext == null) {
                            //Create new room traversal object to next room from sub
                            val id = it.room_id
                            val name = it.title
                            val desc = it.description
                            daoCheckNext = TreasureRoomTraversal(
                                room_id = id,
                                name = name,
                                description = desc)

                            it.exits.forEach{
                                when(it) {
                                    "n" ->{
                                        daoCheckNext.north = 666
                                    }
                                    "s"->{
                                        daoCheckNext.south = 666

                                    }
                                    "e"->{
                                        daoCheckNext.east = 666

                                    }
                                    "w"->{
                                        daoCheckNext.west = 666

                                    }
                                }
                            }
                        }


                        //Directions

                        val currentRoomID = currentRoom.value?.room_id
                        when (direction.direction) {
                            "n"->{daoCheckNext.south = currentRoomID }
                            "s"->{daoCheckNext.north = currentRoomID}
                            "e"->{daoCheckNext.west = currentRoomID}
                            "w"->{daoCheckNext.east = currentRoomID}

                        }

                        dao.insert(daoCheckNext)

                        currentRoom.postValue(it)},
                    { Log.e("Traverse: ", "onError - " + it.message)},

                    {})
            )
        }

        tv_south.setOnClickListener {
            val south = Direction("s")
            compositeDisposable.add(treasureRoomApi.movePlayer("Token 5fea1d71395c06916414b3ced62e3992b8b022ea", south)
                .toObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {

                        //Check DB to see if next room is in DB
                        //Check to see if current room is in DB
                        var daoCheckNext = dao.getMTtreasureRoomID(it.room_id)

                        if (daoCheckNext == null) {
                            //Create new room traversal object to next room from sub
                            val id = it.room_id
                            val name = it.title
                            val desc = it.description
                            daoCheckNext = TreasureRoomTraversal(
                                room_id = id,
                                name = name,
                                description = desc)

                            it.exits.forEach{
                                when(it) {
                                    "n" ->{
                                        daoCheckNext.north = 666
                                    }
                                    "s"->{
                                        daoCheckNext.south = 666

                                    }
                                    "e"->{
                                        daoCheckNext.east = 666

                                    }
                                    "w"->{
                                        daoCheckNext.west = 666

                                    }
                                }
                            }
                        }


                        //Directions

                        val currentRoomID = currentRoom.value?.room_id
                        when (direction.direction) {
                            "n"->{daoCheckNext.south = currentRoomID }
                            "s"->{daoCheckNext.north = currentRoomID}
                            "e"->{daoCheckNext.west = currentRoomID}
                            "w"->{daoCheckNext.east = currentRoomID}

                        }

                        dao.insert(daoCheckNext)

                        currentRoom.postValue(it)},
                    { Log.e("Traverse: " + direction.direction, "onError - " + it.message)},

                    {})
            )
        }
    }





    fun autoTraversal() {

    }



//    fun delayTimer() {
//        val time = timeWait.toFloat()
//
//        delay = object : CountDownTimer((time ))
//    }






    fun autoTraverse() {


    }

    override fun onStart() {
        super.onStart()





    }

    override fun onStop() {
        super.onStop()
        compositeDisposable.clear()
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }
}
