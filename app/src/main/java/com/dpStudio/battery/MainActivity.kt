package com.dpStudio.battery

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.BatteryManager
import android.os.Binder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.renderscript.ScriptGroup
import android.view.View.inflate
import android.widget.TextView
import com.github.anastr.speedviewlib.PointerSpeedometer
import com.github.anastr.speedviewlib.SpeedView
import org.w3c.dom.Text
import java.util.*


class MainActivity : AppCompatActivity() {
    lateinit var batteryManager: BatteryManager
    lateinit var svChanger : SpeedView
    lateinit var svDungluong : PointerSpeedometer
    lateinit var tvStatus : TextView
    lateinit var tvDungLuong : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initAll()
    }

    fun initView() {
        supportActionBar?.elevation = 0f
        svChanger = findViewById(R.id.sv_activity_main_charge)
        svDungluong = findViewById(R.id.sv_activity_main_battery)
        tvStatus = findViewById(R.id.tv_activity_main_status_charge)
        tvDungLuong = findViewById(R.id.tv_activity_main_status_dung_luong)
    }

    fun initAll() {
        initView()
        batteryManager = getSystemService(Service.BATTERY_SERVICE) as BatteryManager
        Contrants.createNotificationChannel(applicationContext)
        startService(Intent(applicationContext, ServiceBatterymonitor::class.java))
        measure()
    }

    fun measure() {
        Timer().schedule(object : TimerTask() {
            override fun run() {
                val currentA = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW) / 1000
                val dungluong = (batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER) / 1000)
                val percent = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY).toString();
                runOnUiThread(Runnable {
                    svChanger.speedTo(Math.abs(currentA) * 1f)
                    svDungluong.speedTo(dungluong * 1f)
                    setStatus(applicationContext, currentA, percent)
                })
            }
        }, 1000, 1000)
    }

    fun setStatus(context: Context, currentA: Int, percent: String) {
        tvStatus.text = Utils.buildSubtitle(context, currentA)
        tvDungLuong.text = percent + " %"
    }
}