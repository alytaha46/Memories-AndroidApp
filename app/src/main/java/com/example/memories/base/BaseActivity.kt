package com.example.memories.base

import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

open class BaseActivity:AppCompatActivity() {
    var dialog:AlertDialog?=null
    fun showMessage(
        title:String?=null,
        message:String?=null,
        posActionName:String?=null,
        posAction:DialogInterface.OnClickListener?=null,
        negActionName:String?=null,
        negAction:DialogInterface.OnClickListener?=null,
        cancelable:Boolean=true
    ){
        dialog=AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(posActionName,posAction)
            .setNegativeButton(negActionName,negAction)
            .setCancelable(cancelable)
            .show()
    }
    fun hideDialog(){
        dialog?.dismiss()
    }
}