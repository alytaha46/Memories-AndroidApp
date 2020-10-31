package com.example.memories.permessions

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.memories.base.BaseActivity


class Permissions:BaseActivity() {
    companion object {
        fun isPermissionGranted(context: Context, Permission: String): Boolean {
            return ContextCompat.checkSelfPermission(
                context,
                Permission
            ) == PackageManager.PERMISSION_GRANTED
        }

        fun requestPermissionFromUser(activity: Activity, permission: String, requestCode: Int) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission))
            {
                showMessage(message = "Application wants to access your location to be able to save it in your memory",
                    posActionName = "OK",
                    context = activity,
                    posAction = { dialog, _ ->
                        ActivityCompat.requestPermissions(
                            activity,
                            arrayOf(permission),
                            requestCode,

                        )
                        dialog.dismiss()
                    },
                    negActionName = "Cancel",
                    negAction = { dialog, _ -> dialog.dismiss() })
            } else {
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(permission),
                    requestCode
                )
            }
        }


    }
}