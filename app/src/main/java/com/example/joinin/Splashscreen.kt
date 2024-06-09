package com.example.joinin

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class Splashscreen : AppCompatActivity() {
    private val SPLASH_TIME_OUT:Long = 3000
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splashscreen)
        android.os.Handler().postDelayed({
            startActivity(Intent(this,Getstarted::class.java))
            finish()
        }, SPLASH_TIME_OUT)
    }
}