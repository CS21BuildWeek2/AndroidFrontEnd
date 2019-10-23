package dev.vespertine.treasurehunt.models

data class TreasureRoomTraversal(
    val room_id: Int,
    val name : String,
    val description: String,
    var northID: Int? = -1,
    var southID: Int? = -1,
    var eastID: Int? = -1,
    var westID: Int? = -1
)