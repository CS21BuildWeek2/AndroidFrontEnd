package dev.vespertine.treasurehunt.di.module

import android.app.Application
import dagger.Module
import dagger.Provides
import dev.vespertine.treasurehunt.db.TreasureHuntDatabase
import dev.vespertine.treasurehunt.db.TreasureRoomTraversalDao
import javax.inject.Singleton

@Module
class AppModule {

    @Provides
    @Singleton
    fun provideTreasureHuntDatabase(application: Application): TreasureHuntDatabase
        = TreasureHuntDatabase.getDatabase(application.applicationContext)

    @Provides
    @Singleton
    fun provideTreasureDao(treasureHuntDatabase: TreasureHuntDatabase): TreasureRoomTraversalDao
        = treasureHuntDatabase.treasureDAO()


}