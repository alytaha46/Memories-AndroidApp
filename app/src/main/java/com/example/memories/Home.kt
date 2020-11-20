package com.example.memories

import android.Manifest
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.work.Constraints
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.memories.database.MemoriesDatabase
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.activity_home.*
import java.util.concurrent.TimeUnit


class Home : AppCompatActivity() {
    val adapter = HomeRecycleAdapter(null)
    var permissions_request: Array<String> = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_BACKGROUND_LOCATION
    )
    val PERMISSIONS_REQUEST_CODE = 2000
    var userLocation: Location? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
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
        val list = MemoriesDatabase.getInstance(application).memoriesDao().selectAll()
        adapter.changeData(list)
        val work = PeriodicWorkRequest.Builder(LocationServiceWork::class.java, 15, TimeUnit.MINUTES)
            .setConstraints(Constraints.NONE)
            .build()
        WorkManager.getInstance().enqueue(work)
//        val LocationServiceWorkRequest: PeriodicWorkRequest = PeriodicWorkRequestBuilder<LocationServiceWork>(
//            3, TimeUnit.MINUTES, // repeatInterval (the period cycle)
//            2, TimeUnit.MINUTES
//        ) // flexInterval
//            .build()
//        WorkManager
//            .getInstance(this)
//            .enqueue(LocationServiceWorkRequest)
    }

    fun addMemory(view: View) {
        moveToAddMemoryActivity()
    }

    fun moveToAddMemoryActivity() {
        startActivity(Intent(this@Home, AddMemory::class.java))
    }


}
