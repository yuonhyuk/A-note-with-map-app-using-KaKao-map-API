package com.example.mapnote.Room

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE

@Dao
interface MarkerDAO {
    @Insert(onConflict = REPLACE)
    fun insert(marker_Info : MarkerEntity)

    @Query("SELECT * FROM  markerInfo")
    fun getAll() : List<MarkerEntity>

    @Query("SELECT * FROM  markerInfo WHERE lat LIKE :lat AND lng LIKE :lng")
    fun search(lat : Double, lng : Double) : Boolean

    @Delete
    fun delete(marker_Info: MarkerEntity)

    @Update
    fun update(marker_Info: MarkerEntity)
}