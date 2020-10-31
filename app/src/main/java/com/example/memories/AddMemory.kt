package com.example.memories


import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.Manifest.permission.CAMERA
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.memories.base.BaseActivity
import com.example.memories.permessions.Permissions.Companion.isPermissionGranted
import com.example.memories.permessions.Permissions.Companion.requestPermissionFromUser
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.OnCompleteListener
import kotlinx.android.synthetic.main.activity_add_memory.*


class AddMemory : BaseActivity() {
    val PERMISSIONS_REQUEST_CODE = 1000
    val SETTINGS_DIALOG_REQUEST = 200
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    var permissions_request: Array<String> = arrayOf(ACCESS_FINE_LOCATION, CAMERA)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_memory)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        if (!isPermissionGranted(this, permissions_request)) {
            requestPermissionFromUser(this, permissions_request, PERMISSIONS_REQUEST_CODE)
        } else {
            showUserLoction()
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (grantResults[0] == 0) {
            showUserLoction()
        } else {
            Toast.makeText(this, "User Denied the Location Permissions", Toast.LENGTH_LONG).show()
        }
        if (grantResults[1] == 0) {
            //camera
        } else {
            Toast.makeText(this, "User Denied the Camera Permissions", Toast.LENGTH_LONG).show()
        }
    }

    val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult?) {
            result ?: return
            for (location in result.locations) {
                // Update UI with location data
                // ...
                location_text.setText(
                    "lat= " + location.latitude
                            + " long= " + location.longitude
                            + " acc= " + location.accuracy
                )
            }
        }
    }
    val locationRequest = LocationRequest.create().apply {
        interval = 5000
        fastestInterval = 1000
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    @SuppressLint("MissingPermission")
    fun getLocationFromClientApi() {
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }


    fun showUserLoction() {

        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val result = LocationServices.getSettingsClient(this).checkLocationSettings(builder.build())

        result.addOnCompleteListener {
            try {
                val response = it.getResult(ApiException::class.java)
                // All location settings are satisfied. The client can initialize location
                // requests here.
                getLocationFromClientApi();
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
                                this@AddMemory,
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
                        Toast.makeText(this, "Cannot Access your Location", Toast.LENGTH_LONG)
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val states: LocationSettingsStates = LocationSettingsStates.fromIntent(data);
        when (requestCode) {
            SETTINGS_DIALOG_REQUEST ->
                if (resultCode == RESULT_OK) {
                    // All required changes were successfully made
                    getLocationFromClientApi()
                } else if (resultCode == RESULT_CANCELED) {
                    // The user was asked to change settings, but chose not to
                    Toast.makeText(
                        this,
                        "Cannot Access your Location Please open GPS",
                        Toast.LENGTH_LONG
                    ).show()
                }
        }
    }

}