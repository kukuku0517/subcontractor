package com.example.android.subcontractor.view

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.android.subcontractor.R
import java.lang.Thread.sleep

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Thread{
            sleep(1000)
            startActivity(Intent(applicationContext,LoginActivity::class.java))
            finish()
        }.start()
    }
}
