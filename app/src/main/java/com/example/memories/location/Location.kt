package com.example.memories.location

import android.annotation.SuppressLint
import android.app.Activity
import android.content.IntentSender
import android.os.Looper
import android.widget.Toast
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*

class Location {
    companion object{
        val SETTINGS_DIALOG_REQUEST = 200
        val locationRequest = LocationRequest.create().apply {
            interval = 5000
            fastestInterval = 1000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        fun showUserLoction(activity: Activity,fusedLocationClient:FusedLocationProviderClient,locationCallback:LocationCallback) {

            val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
            val result = LocationServices.getSettingsClient(activity).checkLocationSettings(builder.build())

            result.addOnCompleteListener {
                try {
                    val response = it.getResult(ApiException::class.java)
                    // All location settings are satisfied. The client can initialize location
                    // requests here.
                    getLocationFromClientApi(fusedLocationClient,locationCallback)
                } catch (exception: ApiException) {
                    when (exception.statusCode) {
                        LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                            // Location settings are not satisfied. But could be fixed by showing the
                            // user a dialog.
                            try {
                                // Cast to a resolvable exception.
                                val resolvable = exception as ResolvableApiException
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                resolvable.startResolutionForResult(
                                    activity,
                                    SETTINGS_DIALOG_REQUEST
                                )
                            } catch (e: IntentSender.SendIntentException) {
                                // Ignore the error.
                            } catch (e: ClassCastException) {
                                // Ignore, should be an impossible error.
                            }
                        }
                        LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                            // Location settings are not satisfied. However, we have no way to fix the
                            // settings so we won't show the dialog.
                            Toast.makeText(activity, "Cannot Access your Location", Toast.LENGTH_LONG)
                        }
                    }
                }
            }
        }

        @SuppressLint("MissingPermission")
        fun getLocationFromClientApi(fusedLocationClient:FusedLocationProviderClient,locationCallback:LocationCallback) {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }

    }
}