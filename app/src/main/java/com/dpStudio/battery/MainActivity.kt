package com.dpStudio.battery

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.anastr.speedviewlib.PointerSpeedometer
import com.github.anastr.speedviewlib.SpeedView
import java.util.*


class MainActivity : AppCompatActivity() {
    lateinit var batteryManager: BatteryManager
    lateinit var svChanger : SpeedView
    lateinit var svDungluong : PointerSpeedometer
    lateinit var tvStatus : TextView
    lateinit var tvDungLuong : TextView
    lateinit var tvstatus2 : TextView

    companion object {
        lateinit var tvtemp: TextView
        lateinit var tvhealth: TextView
        lateinit var tvMaxCap: TextView
        lateinit var tvtech: TextView
    }

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
        tvstatus2 = findViewById(R.id.tv_activity_main_status)
        tvtemp = findViewById(R.id.tv_activity_main_temp)
        tvhealth = findViewById(R.id.tv_activity_main_health)
        tvMaxCap = findViewById(R.id.tv_activity_main_max_capitacy)
        tvtech = findViewById(R.id.tv_activity_main_tech)
    }

    fun initAll() {
        initView()
        batteryManager = getSystemService(Service.BATTERY_SERVICE) as BatteryManager
        Contrants.createNotificationChannel(applicationContext)
        startService(Intent(applicationContext, ServiceBatterymonitor::class.java))
        measure()
        resignBroadCast()
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
        tvstatus2.text = Utils.buildSubtitle(context, currentA)
        tvMaxCap.text = getBatteryCapacity(context).toInt().toString() + " " +
                getString(R.string.unit)
    }

    fun resignBroadCast() {
        val itentFilter = IntentFilter();
        itentFilter.addAction(Intent.ACTION_BATTERY_CHANGED)
        this.registerReceiver(MeasureBattery(), itentFilter)
    }

    fun getBatteryCapacity(context: Context?): Double {
        val mPowerProfile: Any
        var batteryCapacity = 0.0
        val POWER_PROFILE_CLASS = "com.android.internal.os.PowerProfile"
        try {
            mPowerProfile = Class.forName(POWER_PROFILE_CLASS)
                    .getConstructor(Context::class.java)
                    .newInstance(context)
            batteryCapacity = Class
                    .forName(POWER_PROFILE_CLASS)
                    .getMethod("getBatteryCapacity")
                    .invoke(mPowerProfile) as Double
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return batteryCapacity
    }

    class MeasureBattery : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
          tvtemp.text = "${intent?.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0)?.div(10)} °C"
            tvhealth.text = getHealthBattery(context!!, intent?.getIntExtra(BatteryManager.EXTRA_HEALTH, 0)!!)
//            tvMaxCap = "${intent?.getIntExtra(BatteryManager.EX, 0)} mAh"
            tvtech.text = "${intent?.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY)}"
        }

        fun getHealthBattery(context: Context, health : Int) : String {
            when(health) {
                2 -> return context.getString(R.string.status_batery_good)
                3 -> return context.getString(R.string.status_batery_hot)
                5 -> return context.getString(R.string.status_batery_voltage)
                4 -> return context.getString(R.string.status_batery_dead)
                7 -> return context.getString(R.string.status_batery_cold)
                else -> return context.getString(R.string.status_batery_hot)
            }
        }
    }
}