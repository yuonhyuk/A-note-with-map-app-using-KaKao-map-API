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

    @Query("SELECT * FROM markerInfo WHERE lat=:lat AND lng=:lng")
    fun search(lat: Double?, lng: Double?) : MarkerInfo

    @Update
    fun update(markerInfo: MarkerInfo)
    /*@Query("UPDATE markerinfo SET content = :name WHERE mid = :id")
    fun name_update(id: Int?, name : String)

    @Query("UPDATE markerinfo SET content = :memo WHERE mid = :id")
    fun content_update(id: Int?, memo : String)

    @Query("UPDATE markerinfo SET date = :date WHERE mid = :id")
    fun date_update(id: Int?, date : String)

    @Query("UPDATE markerinfo SET time = :time WHERE mid = :id")
    fun time_update(id: Int?, time : String)*/

    @Delete
    fun delete(markerInfo: MarkerInfo)
}