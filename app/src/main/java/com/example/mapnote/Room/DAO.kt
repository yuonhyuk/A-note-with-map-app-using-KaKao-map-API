package com.example.mapnote.Room

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE

@Dao
interface DAO {
    @Insert(onConflict = REPLACE)
    fun insert(marker_info : Entity)

    @Query("SELECT * FROM  info")
    fun getAll() : List<Entity>

    @Delete
    fun delete(marker_info: Entity)
}