package com.example.mapnote.Room

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface MarkerDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(markerInfo: MarkerInfo)

    @Query("SELECT * FROM markerInfo")
    suspend fun getAll(): List<MarkerInfo>

    @Query("SELECT * FROM markerInfo WHERE lat=:lat AND lng=:lng")
    suspend fun getId(lat: Double,lng:Double) : MarkerInfo

    @Update
    suspend fun update(markerInfo: MarkerInfo)

    @Delete
    suspend fun delete(markerInfo: MarkerInfo)

    @Query("DELETE FROM markerInfo WHERE mid > 0")
    suspend fun deleteAll()
}