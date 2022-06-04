package com.example.mapnote.Room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query

@Dao
interface MarkerDAO {
    @Insert(onConflict = REPLACE)
    fun insert(marker_Info : MarkerEntity)

    @Query("SELECT * FROM  markerInfo")
    fun getAll() : List<MarkerEntity>

    @Delete
    fun delete(marker_Info: MarkerEntity)
}