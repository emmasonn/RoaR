package com.column.roar.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface LodgeDao {

    @Query("SELECT * FROM lodge_table")
    fun getAllLodges(): LiveData<List<Lodge>>

    @Transaction
    @Query("SELECT * FROM lodge_table WHERE id =:lodgeId")
    fun getLodgeAndPhoto(lodgeId: String): LiveData<LodgeAndPhotos>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(lodge: Lodge)
}