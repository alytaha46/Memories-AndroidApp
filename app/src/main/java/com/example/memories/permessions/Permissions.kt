package com.example.memories.permessions

import android.Manifest
import android.Manifest.permission.*
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.memories.base.BaseActivity


class Permissions : BaseActivity() {
    companion object {
        var flag = false;

        fun isPermissionGranted(context: Context, Permission: Array<String>): Boolean {
            for (permission in Permission) {
                if (ActivityCompat.checkSelfPermission(
                        context,
                        permission
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return false
                }
            }
            return true
        }

        fun requestPermissionFromUser(activity: Activity, permissions: Array<String>, requestCode: Int) {
            for (permission in permissions) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                    flag = true
                    break
                }
            }
            if (flag) {
                showMessage(message = "Application wants to these permissions to be able to save it in your memory",
                    posActionName = "OK",
                    context = activity,
                    posAction = { dialog, _ ->
                        ActivityCompat.requestPermissions(
                            activity,
                            permissions,
                            requestCode
                        )
                        dialog.dismiss()
                    },
                    negActionName = "Cancel",
                    negAction = { dialog, _ -> dialog.dismiss() })
            } else {
                ActivityCompat.requestPermissions(
                    activity,
                    permissions,
                    requestCode
                )
            }
        }


    }
}