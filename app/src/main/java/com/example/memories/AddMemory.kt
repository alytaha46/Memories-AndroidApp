package com.example.memories

import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.memories.base.BaseActivity
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*


class AddMemory : BaseActivity() {
    val LOCATION_PERMISSION_REQUEST_CODE = 1000
    val CAMERA_PERMISSION_REQUEST_CODE = 2000
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_memory)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (isPermissionGranted(android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            showUserLoction()
        } else {
            requestPermissionFromUser()
        }
    }

    fun requestPermissionFromUser() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
            showMessage(message = "Application wants to access your location to be able to save it in your note",
                posActionName = "OK",
                posAction = { dialog, _ ->
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                        LOCATION_PERMISSION_REQUEST_CODE
                    )
                    dialog.dismiss()
                },
                negActionName = "Cancel",
                negAction = { dialog, _ -> dialog.dismiss() })
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    val SETTINGS_DIALOG_REQUEST = 200
    fun showUserLoction() {

        val locationRequest = LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
        val result = LocationServices.getSettingsClient(this).checkLocationSettings(builder.build())
        result.addOnCompleteListener {
            try {
                val response = it.getResult(ApiException::class.java)
                // All location settings are satisfied. The client can initialize location
                // requests here.
                getLocationFromClientApi()

            } catch (exception: ApiException) {
                when (exception.getStatusCode()) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED ->
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
                            );
                        } catch (e: IntentSender.SendIntentException) {
                            // Ignore the error.
                        } catch (e: ClassCastException) {
                            // Ignore, should be an impossible error.
                        }

                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE ->
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        Toast.makeText(this, "Cannot Access your Location", Toast.LENGTH_LONG)
                }
            }
        }
    }

    override fun onActivityResult(resultCode: Int, requestCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        val states: LocationSettingsStates = LocationSettingsStates.fromIntent(intent);
        when (requestCode) {
            SETTINGS_DIALOG_REQUEST ->
                if (resultCode == RESULT_OK) {
                    // All required changes were successfully made
                    getLocationFromClientApi()
                } else if (resultCode == RESULT_CANCELED) {
                    // The user was asked to change settings, but chose not to
                    Toast.makeText(this, "Cannot Access your Location", Toast.LENGTH_LONG).show()
                }

        }
    }

    //    val locationCallback=object :LocationCallback(){
//        override fun onLocationAvailability(p0: LocationAvailability?) {
//            super.onLocationAvailability(p0)
//        }
//
//        override fun onLocationResult(p0: LocationResult?) {
//            super.onLocationResult(p0)
//        }
//    }
    @SuppressLint("MissingPermission")
    fun getLocationFromClientApi() {
//        fusedLocationClient.requestLocationUpdates(locationRequest,
//            locationCallback,
//            Looper.getMainLooper())
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
            }
    }


    fun isPermissionGranted(Permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            showUserLoction()
        } else if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            showUserLoction()
        } else {
            Toast.makeText(this, "User Denied the Permissions", Toast.LENGTH_LONG).show()
        }

    }
}