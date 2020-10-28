package com.example.memories

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.memories.base.BaseActivity

class AddMemory : BaseActivity() {
    val LOCATION_PERMISSION_REQUEST_CODE=1000
    val CAMERA_PERMISSION_REQUEST_CODE=2000
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_memory)
        if (isPermissionGranted()){
            showUserLoction()
        }else{
            requestPermissionFromUser()
        }
    }
    fun requestPermissionFromUser(){
        if( ActivityCompat.shouldShowRequestPermissionRationale(this,android.Manifest.permission.ACCESS_FINE_LOCATION)){
            showMessage(message = "Application wants to access your location to be able to save it in your note"
                ,posActionName ="OK" ,posAction = { dialog, _ ->
                    ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),LOCATION_PERMISSION_REQUEST_CODE)
                    dialog.dismiss()}
                ,negActionName ="Cancel" ,negAction = { dialog, _ ->dialog.dismiss()  })
        }else{ ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),LOCATION_PERMISSION_REQUEST_CODE)
        }
    }
    fun showUserLoction(){
        Toast.makeText(this,"User Location", Toast.LENGTH_LONG).show()
    }

    fun isPermissionGranted():Boolean{
        return ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED &&
                return ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode==LOCATION_PERMISSION_REQUEST_CODE){
            showUserLoction()
        }else if(requestCode==CAMERA_PERMISSION_REQUEST_CODE){
            showUserLoction()
        }else {
            Toast.makeText(this,"User Denied the Permissions", Toast.LENGTH_LONG).show()
        }

    }
}