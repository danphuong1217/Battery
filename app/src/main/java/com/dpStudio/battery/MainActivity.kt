package com.dpStudio.battery

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.BatteryManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.dpStudio.battery.util.Contrants
import com.dpStudio.battery.util.Utils
import com.github.anastr.speedviewlib.PointerSpeedometer
import com.github.anastr.speedviewlib.SpeedView
import com.github.anastr.speedviewlib.components.Style
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds
import java.util.*


class MainActivity : AppCompatActivity() {
    lateinit var batteryManager: BatteryManager
    lateinit var svChanger : SpeedView
    lateinit var svDungluong : PointerSpeedometer
    lateinit var tvStatus : TextView
    lateinit var tvDungLuong : TextView
    lateinit var tvstatus2 : TextView
    lateinit var interstitialAd: InterstitialAd

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
        initClick()
    }

    private fun initClick() {
        svChanger.setOnClickListener {
            if (interstitialAd.isLoaded) {
                interstitialAd.show()
            }
        }

        svDungluong.setOnClickListener {
            if (interstitialAd.isLoaded) {
                interstitialAd.show()
            }
        }
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
        Utils.initAds(applicationContext)
        Utils.initBannerAds(applicationContext, findViewById(R.id.av_activity_main_banner))
        interstitialAd = Utils.initQuangCaoTrungGian(applicationContext)
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
                    val isCharge = if (currentA > 0) false else true
                    setSpeedChanrgeColor(isCharge)
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

    @RequiresApi(Build.VERSION_CODES.M)
    fun setSpeedChanrgeColor(isCharge : Boolean) {
        if (isCharge) {
            svChanger.makeSections(5, Color.CYAN, Style.BUTT)
            svChanger.sections[0].color = getColor(R.color.slow_charge)
            svChanger.sections[1].color = getColor(R.color.normal_charge)
            svChanger.sections[2].color = getColor(R.color.normal_charge)
            svChanger.sections[3].color = getColor(R.color.fast_charge)
            svChanger.sections[4].color = getColor(R.color.fast_charge)
        } else {
            svChanger.makeSections(5, Color.RED, Style.BUTT)
            svChanger.sections[0].color = getColor(R.color.slow_use)
            svChanger.sections[1].color = getColor(R.color.normal_use)
            svChanger.sections[2].color = getColor(R.color.normal_use)
            svChanger.sections[3].color = getColor(R.color.fast_use)
            svChanger.sections[4].color = getColor(R.color.fast_use)
        }
    }


}