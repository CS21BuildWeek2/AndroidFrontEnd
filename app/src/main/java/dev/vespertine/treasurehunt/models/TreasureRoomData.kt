package dev.vespertine.treasurehunt.models

import kotlinx.serialization.Serializable

@Serializable
data class TreasureRoomData(
    val cooldown: Double,
    val coordinates: String,
    val description: String,
    val errors: List<Any>,
    val exits: List<String>,
    val items: List<String>,
    val messages: List<String>?,
    val players: List<String>?,
    val room_id: Int,
    val title: String
)

