package com.example.mapnote.Room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [MarkerInfo::class], version = 8)
abstract class MarkerDataBase : RoomDatabase(){
    abstract fun markerDAO() : MarkerDAO

    companion object{
        var INSTANCE : MarkerDataBase? = null

        fun getInstance(context: Context) : MarkerDataBase?{
            if(INSTANCE == null){
                synchronized(MarkerDataBase::class){
                    INSTANCE = Room.databaseBuilder(context.applicationContext,MarkerDataBase::class.java,"marker_info")
                        .fallbackToDestructiveMigration()
                        .build()
                }
            }

            return INSTANCE
        }
    }
}