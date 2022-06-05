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

    @Query("SELECT * FROM markerInfo WHERE place_name LIKE :marker")
    fun getAn(marker: String?) : MarkerEntity

    @Delete
    fun delete(marker_Info: MarkerEntity)

    @Query("UPDATE markerInfo SET place_name = :local_name,memo = :memo_edit, date= :date_edit,time=:time_edit WHERE ((lat MATCH:lat_edit) AND (lng MATCH :lng_edit))")
    fun update(local_name : String, memo_edit : String, date_edit : String, time_edit :String, lat_edit : Double, lng_edit : Double)
}