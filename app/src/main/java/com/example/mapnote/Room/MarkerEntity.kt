package com.example.mapnote.Room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "markerInfo")
data class MarkerEntity(
    @PrimaryKey(autoGenerate = true) var id: Long?,
    var place_name: String,
    var memo: String,
    var time: String,
    var lat: Double?,
    var lng: Double?
)