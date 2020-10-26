package com.example.memories.database

import androidx.room.*

@Dao
interface MemoriesDao {
    @Insert
    fun addMemory(memory: Memory)

    @Delete
    fun deleteMemory(memory: Memory)

    @Update
    fun updateMemory(memory: Memory)

    @Query("delete from memories where id = :id")
    fun deleteMemoryById(id:Int)

    @Query("select * from memories")
    fun selectAll():List<Memory>
}