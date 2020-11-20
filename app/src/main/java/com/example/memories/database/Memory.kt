package com.example.memories.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Memories")
data class Memory(
    @PrimaryKey(autoGenerate = true)
    var id:Int? = null,
    @ColumnInfo
    var title:String? = null,
    @ColumnInfo
    var description:String? = null,
    @ColumnInfo
    var date:String? = null,
    @ColumnInfo
    var path:String? = null,
    @ColumnInfo
    var latitude:Double? = null,
    @ColumnInfo
    var longitude:Double? = null
)