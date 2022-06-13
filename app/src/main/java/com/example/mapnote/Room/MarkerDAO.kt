package com.example.mapnote.Room

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface MarkerDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(markerInfo: MarkerInfo)

    @Query("SELECT * FROM markerInfo")
    fun getAll(): List<MarkerInfo>

    @Delete
    fun delete(markerInfo: MarkerInfo)

    @Query("DELETE FROM markerInfo WHERE mid > 0")
    fun deleteAll()
}