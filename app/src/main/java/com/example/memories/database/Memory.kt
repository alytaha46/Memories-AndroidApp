package com.example.memories.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Memories")
data class Memory(
    @PrimaryKey(autoGenerate = true)
    val id:Int? = null,
    @ColumnInfo
    val title:String? = null,
    @ColumnInfo
    val description:String? = null,
    @ColumnInfo
    val date:String? = null
)