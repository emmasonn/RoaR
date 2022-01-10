package com.column.roar.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface FavModelDao {

    @Query("SELECT * From fav_table ")
    fun getFavString(): LiveData<List<FavModel>>

    @Query("SELECT * From fav_table ")
    suspend fun getFavOnce(): List<FavModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(favId: FavModel)

    @Delete
    suspend fun delete(favId: FavModel)
}