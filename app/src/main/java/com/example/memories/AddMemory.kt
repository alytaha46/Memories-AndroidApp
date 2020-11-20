package com.example.memories


import android.Manifest.permission.*
import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentSender
import android.graphics.Bitmap
import android.location.Location
import android.os.Bundle
import android.os.Environment
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import com.example.memories.base.BaseActivity
import com.example.memories.database.MemoriesDatabase
import com.example.memories.database.Memory
import com.example.memories.location.Location.Companion.SETTINGS_DIALOG_REQUEST
import com.example.memories.location.Location.Companion.getLocationFromClientApi
import com.example.memories.location.Location.Companion.showUserLoction
import com.example.memories.permessions.Permissions.Companion.isPermissionGranted
import com.example.memories.permessions.Permissions.Companion.requestPermissionFromUser
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.activity_add_memory.*
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*


class AddMemory : BaseActivity(), OnMapReadyCallback {
    val PERMISSIONS_REQUEST_CODE = 1000
    val CAMERA_REQUEST = 1888
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    var permissions_request: Array<String> =
        arrayOf(ACCESS_FINE_LOCATION, CAMERA, WRITE_EXTERNAL_STORAGE)

    lateinit var img: ImageView
    lateinit var add_img_button: Button
    var title: String? = null
    var description: String? = null
    lateinit var save_button: Button
    var bitmap: Bitmap? = null

    companion object {
        var lat: Double = 0.0
        var long: Double = 0.0
    }

    var file: File? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_memory)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        img = saved_img
        add_img_button = camera_btn
        if (!isPermissionGranted(this, permissions_request)) {
            requestPermissionFromUser(this, permissions_request, PERMISSIONS_REQUEST_CODE)
        } else {
            showUserLoction(this, fusedLocationClient, locationCallback)
            add_img_button.setOnClickListener {
                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(cameraIntent, CAMERA_REQUEST)
            }
        }
        add_img_button.setOnClickListener {
            if (!isPermissionGranted(this, permissions_request)) {
                requestPermissionFromUser(this, permissions_request, PERMISSIONS_REQUEST_CODE)
            } else {
                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(cameraIntent, CAMERA_REQUEST)
            }
        }
        val map = supportFragmentManager.findFragmentById(R.id.mapView) as SupportMapFragment
        map.getMapAsync(this)
        save_button = save
        save_button.setOnClickListener {
            title = title_text.text.toString()
            description = description_text.text.toString()
            if (title == "") {
                Toast.makeText(this, "you must add title", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (description == "") {
                Toast.makeText(this, "you must add description", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (bitmap == null) {
                Toast.makeText(this, "you must add image", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            file = saveImage(bitmap)
            val memory = Memory(
                title = title,
                description = description,
                date = Date().toString(),
                path = file?.path.toString(),
                latitude = lat,
                longitude = long
            )
            //Toast.makeText(this, "" + memory.latitude + " " + memory.longitude, Toast.LENGTH_SHORT).show()
            MemoriesDatabase.getInstance(applicationContext)
                .memoriesDao().addMemory(
                    memory
                )
            finish()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (grantResults[0] == 0) {
            showUserLoction(this, fusedLocationClient, locationCallback)
        } else {
            Toast.makeText(this, "User Denied the Location Permissions", Toast.LENGTH_LONG).show()
        }
        if (grantResults[1] == 0) {
            //camera
            add_img_button.setOnClickListener {
                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(cameraIntent, CAMERA_REQUEST)
            }
        } else {
            Toast.makeText(this, "User Denied the Camera Permissions", Toast.LENGTH_LONG).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode === CAMERA_REQUEST && resultCode === RESULT_OK) {
            img.setImageBitmap(data?.extras?.get("data") as Bitmap)
            bitmap = data?.extras?.get("data") as Bitmap
        }
        when (requestCode) {
            SETTINGS_DIALOG_REQUEST ->
                if (resultCode == RESULT_OK) {
                    // All required changes were successfully made
                    getLocationFromClientApi(fusedLocationClient, locationCallback)
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

    val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult?) {
            result ?: return
            for (location in result.locations) {
                // Update UI with location data
                // ...
                userLocation = location
                lat = location.latitude
                long = location.longitude
                changeUserLocationOnMap()
            }
        }
    }


    var googleMap: GoogleMap? = null
    var userLocation: Location? = null
    override fun onMapReady(map: GoogleMap?) {
        this.googleMap = map
        googleMap?.setMinZoomPreference(5.0f)
        changeUserLocationOnMap()
    }

    @SuppressLint("MissingPermission")
    fun changeUserLocationOnMap() {
        if (googleMap == null) return
        if (userLocation == null) return
//        val markerOptions =
//            MarkerOptions().position(LatLng(userLocation!!.latitude, userLocation!!.longitude))
//        val marker = googleMap?.addMarker(markerOptions)

        googleMap?.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(
                    userLocation!!.latitude,
                    userLocation!!.longitude
                ), 18.0f
            )
        )
        googleMap?.isMyLocationEnabled = true
    }

    private fun saveImage(finalBitmap: Bitmap?): File? {
        val root: String = Environment.getExternalStorageDirectory().toString()
        val myDir = File("$root/Memories")
        myDir.mkdirs()
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val fname = "Shutta_$timeStamp.jpg"
        val file = File(myDir, fname)
        if (file.exists()) file.delete()
        try {
            //Log.e("name",""+myDir+fname)
            val out = FileOutputStream(file)
            finalBitmap?.compress(Bitmap.CompressFormat.JPEG, 100, out)
            out.flush()
            out.close()
            return file
        } catch (e: Exception) {
            Log.e("ms", "" + e)
            return null
        }
    }

}