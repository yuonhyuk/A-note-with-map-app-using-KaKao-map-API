package com.example.mapnote.Room

import android.content.Context
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = arrayOf(Entity::class), version = 1)
abstract class DataBase : RoomDatabase(){
    abstract fun DAO() : DAO

    companion object{
        var INSTANCE : DataBase? = null

        fun getInstance(context: Context) : DataBase?{
            if(INSTANCE == null){
                synchronized(DataBase::class){
                    INSTANCE = Room.databaseBuilder(context.applicationContext,DataBase::class.java,"marker_info")
                        .fallbackToDestructiveMigration()
                        .build()
                }
            }

            return INSTANCE
        }
    }
}