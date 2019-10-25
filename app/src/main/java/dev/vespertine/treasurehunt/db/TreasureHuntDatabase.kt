package dev.vespertine.treasurehunt.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import dev.vespertine.treasurehunt.models.TreasureRoomData
import dev.vespertine.treasurehunt.models.TreasureRoomTraversal

const val DATABASE_SCHEMA_VERSION = 2
const val DB_NAME = "local-db"

@Database(
    entities = [TreasureRoomTraversal::class],
    version = DATABASE_SCHEMA_VERSION,
    exportSchema = false)
abstract class TreasureHuntDatabase: RoomDatabase(){

    abstract fun treasureDAO() : TreasureRoomTraversalDao

    companion object {
        @Volatile
        private var INSTANCE: TreasureHuntDatabase? = null

        fun getDatabase(context: Context): TreasureHuntDatabase {
            if(INSTANCE == null) {
                INSTANCE = createDatabase(context)
            }

            return INSTANCE!!
        }

        private fun createDatabase(context: Context): TreasureHuntDatabase {
            return Room.databaseBuilder(context, TreasureHuntDatabase::class.java, DB_NAME)
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}