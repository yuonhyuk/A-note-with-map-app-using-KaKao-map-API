package com.example.mapnote.Room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "markerInfo")
data class MarkerInfo(
    @PrimaryKey(autoGenerate = true) val mid: Long?,
    @ColumnInfo(name = "place") var name: String?,
    @ColumnInfo(name = "content") var content: String?,
    @ColumnInfo(name = "date") var date: String?,
    @ColumnInfo(name = "time") var time: String?,
    @ColumnInfo(name = "lat") var lat: Double?,
    @ColumnInfo(name = "lng") var lng: Double?
)