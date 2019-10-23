package dev.vespertine.treasurehunt.api

import dev.vespertine.treasurehunt.models.TreasureRoomData
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Header

public interface TreasureRoomApi {

    @GET("api/adv/init/")
    fun initializePlayer(@Header("Authorization") token: String): Single<TreasureRoomData>

}