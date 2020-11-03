package com.example.memories

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.memories.database.MemoriesDatabase
import com.example.memories.database.Memory
import kotlinx.android.synthetic.main.activity_home.*

class Home : AppCompatActivity() {
    val adapter = HomeRecycleAdapter(null)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        val list = MemoriesDatabase.getInstance(applicationContext).memoriesDao().selectAll()

//        val arr = listOf<Memory>(
//            Memory(0,"wedding","wedding plan","202020"),
//            Memory(1,"farah","wedding plan","202020"),
//            Memory(2,"3aza","wedding plan","202020"),
//            Memory(3,"prom","wedding plan","202020"),
//            Memory(4,"party","wedding plan","202020"),
//            Memory(5,"party","wedding plan","202020"),
//            Memory(6,"party","wedding plan","202020")
//        )
        adapter.changeData(list)
        memories_recycle.adapter = adapter
    }

    override fun onStart() {
        super.onStart()
        val list = MemoriesDatabase.getInstance(applicationContext).memoriesDao().selectAll()
        adapter.changeData(list)
    }

    fun addMemory(view: View) {
        moveToAddMemoryActivity()
    }

    fun moveToAddMemoryActivity()
    {
        startActivity(Intent(this@Home,AddMemory::class.java))
    }
}
