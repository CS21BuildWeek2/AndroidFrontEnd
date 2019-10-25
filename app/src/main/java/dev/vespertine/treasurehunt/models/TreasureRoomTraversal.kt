package dev.vespertine.treasurehunt.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "treasure_room")
data class TreasureRoomTraversal(

    @PrimaryKey
    val room_id: Int,
    val name : String,
    val description: String,

    var north: Int? = null,

    var south: Int? = null,

    var east: Int? = null,

    var west: Int? = null
)