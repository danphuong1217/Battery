package com.dpStudio.battery

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initAll()
    }

    fun initView() {

    }

    fun initAll() {
        Contrants.createNotificationChannel(applicationContext)
        startService(Intent(applicationContext, ServiceBatterymonitor::class.java))
    }
}