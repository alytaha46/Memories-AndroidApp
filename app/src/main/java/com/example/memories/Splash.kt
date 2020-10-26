package com.example.memories

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler

class Splash : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        Handler().postDelayed(
            {
                moveToHomeActivity()
            }, 2500
        )
    }

    private fun moveToHomeActivity() {
        startActivity(Intent(this@Splash,Home::class.java))
        finish()
    }
}