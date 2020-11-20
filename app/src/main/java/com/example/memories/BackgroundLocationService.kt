package com.example.memories

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Constraints
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.example.memories.database.MemoriesDatabase
import com.example.memories.location.Location.Companion.locationRequest
import com.google.android.gms.location.*
import java.util.concurrent.TimeUnit


class BackgroundLocationService : Service() {
    val CHANNEL_ID = "memories_id"
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    override fun onCreate() {
        createNotificationChannel()
        val pendingIntent: PendingIntent =
            Intent(this, Home::class.java).let { notificationIntent ->
                PendingIntent.getActivity(this, 0, notificationIntent, 0)
            }
        val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Location")
            .setContentText("Location")
            .setContentIntent(pendingIntent)
            .build()
        startForeground(2, notification)
    }

    //Location Callback
    val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult?) {
            result ?: return
            for (location in result.locations) {
                // Update UI with location data
                // ...
                //userLocation = location
                AddMemory.lat = location.latitude
                AddMemory.long = location.longitude
                checkNearMemory()
                // search about work manager class  periodic (time period)
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        startLocationUpdates()

        Log.e("here","here")
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback, Looper.myLooper()
        )
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    fun checkNearMemory() {
        val list = MemoriesDatabase.getInstance(applicationContext).memoriesDao().selectAll()
        for (memory in list) {
            if (memory.latitude == null || AddMemory.lat == null || memory.longitude == null || AddMemory.long == null) break
            val difference_lat: Double? = Math.abs(memory.latitude!! - AddMemory.lat!!)
            val difference_Long: Double? = Math.abs(memory.longitude!! - AddMemory.long!!)
            Log.e("diffrence", "" + difference_Long + "   " + difference_lat)
            val difference: Double? = difference_Long!! + difference_lat!!
            Log.e("diffrence", "" + difference)
            if (difference_Long!! + difference_lat!! < 0.003) {
                //Toast.makeText(this, "yessss", Toast.LENGTH_SHORT).show()
                Log.e("in range", "yes")
                showNotification("You are in range of "+memory.title, "Do you Remember ?")
                stopForeground(true)
                stopSelf()
            }

        }
    }

    fun showNotification(textTitle: String, textContent: String) {
        val intent = Intent(this, Home::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        var builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            //.setSmallIcon(R.drawable.notification_icon)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(textTitle)
            .setContentText(textContent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setStyle(NotificationCompat.BigTextStyle().bigText(textTitle))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setSound(null)
            .setContentIntent(PendingIntent.getActivities(this, 2, arrayOf(intent), 0))
            .setAutoCancel(true)
        with(NotificationManagerCompat.from(applicationContext)) {
            // notificationId is a unique int for each notification that you must define
            notify(1, builder.build())
        }
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}