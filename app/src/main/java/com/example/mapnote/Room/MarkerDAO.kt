package com.example.mapnote.Room

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface MarkerDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(markerInfo: MarkerInfo)

    @Query("SELECT * FROM markerInfo")
    fun getAll(): List<MarkerInfo>

    @Query("SELECT * FROM markerInfo WHERE lat=:lat AND lng=:lng")
    fun getId(lat: Double,lng:Double) : MarkerInfo

    /*@Query("UPDATE markerinfo SET content = :name WHERE mid = :id")
    fun name_update(id: Long, name : String)

    @Query("UPDATE markerinfo SET content = :memo WHERE mid = :id")
    fun content_update(id: Long, memo : String)

    @Query("UPDATE markerinfo SET date = :date WHERE mid = :id")
    fun date_update(id: Long, date : String)

    @Query("UPDATE markerinfo SET time = :time WHERE mid = :id")
    fun time_update(id: Long, time : String)*/

    @Delete
    fun delete(markerInfo: MarkerInfo)
}