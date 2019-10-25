package dev.vespertine.treasurehunt.api

import dev.vespertine.treasurehunt.models.Direction
import dev.vespertine.treasurehunt.models.TreasureRoomData
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

public interface TreasureRoomApi {

    @GET("api/adv/init/")
    fun initializePlayer(@Header("Authorization") token: String): Single<TreasureRoomData>

    @POST("api/adv/move/")
    fun movePlayer(
        @Header("Authorization") token: String,
        @Body direction: Direction): Single<TreasureRoomData>

}