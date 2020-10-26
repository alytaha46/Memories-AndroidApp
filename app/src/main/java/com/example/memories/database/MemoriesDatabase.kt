package com.example.memories.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = arrayOf(Memory::class), version = 1, exportSchema = false)
abstract class MemoriesDatabase : RoomDatabase() {

    abstract fun memoriesDao(): MemoriesDao

    companion object
    {
        private val DATABASE_NAME = "memories-database"

        fun getInstance(context: Context): MemoriesDatabase {
            val memoriesDatabase = Room.databaseBuilder(
                context, MemoriesDatabase::class.java,
                DATABASE_NAME
            ).allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build()
            return memoriesDatabase
        }
    }

}