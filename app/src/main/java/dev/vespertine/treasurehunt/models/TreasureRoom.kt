package dev.vespertine.treasurehunt.models

data class TreasureRoom(
    val cooldown: Double,
    val coordinates: String,
    val description: String,
    val errors: List<Any>,
    val exits: List<String>,
    val items: List<String>,
    val messages: List<Any>,
    val players: List<Any>,
    val room_id: Int,
    val title: String
)