package dev.vespertine.treasurehunt.db

import androidx.room.*
import io.reactivex.Completable

@Dao
interface BaseDao<T> {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(type: T)

    @Delete
    fun delete(type: T): Completable

    @Update
    fun update(type: T): Completable



}