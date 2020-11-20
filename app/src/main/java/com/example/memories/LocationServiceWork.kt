package com.example.memories

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters


class LocationServiceWork(appContext: Context, workerParams: WorkerParameters):
    Worker(appContext, workerParams) {
    override fun doWork(): Result {
        val intent = Intent(applicationContext,BackgroundLocationService::class.java)
        applicationContext.startService(intent)
//        Log.e("Repeat","Repeat")
        // Indicate whether the work finished successfully with the Result
        return Result.success()
    }

}
