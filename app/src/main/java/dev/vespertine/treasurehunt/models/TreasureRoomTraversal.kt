package dev.vespertine.treasurehunt.models

data class TreasureRoomTraversal(
    val name : String,
    val northID: Int,
    val southID: Int,
    val eastID: Int,
    val westID: Int
)