package com.dpStudio.battery

import android.app.Notification
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.Icon
import android.os.BatteryManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.dpStudio.battery.util.Contrants
import java.util.*


class ServiceBatterymonitor : Service() {
    lateinit var notificationManager: NotificationManagerCompat
    lateinit var batteryManager: BatteryManager
    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        notificationManager = NotificationManagerCompat.from(this)
        batteryManager = getSystemService(BATTERY_SERVICE) as BatteryManager
        var builder = NotificationCompat.Builder(this, Contrants.CHANNEL_ID_BATTERY_MONITOR)
                .setSmallIcon(R.drawable.max)
                .setContentText("")
        startForeground(1122, builder.build())
        timerMeasureBattery(applicationContext)
        return super.onStartCommand(intent, flags, startId)
    }

    /**
     * Do pin lien tuc
     */
    fun timerMeasureBattery(context: Context) {
        Timer().schedule(object : TimerTask() {
            override fun run() {
                val currentA = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW)
                val dungluong = (batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER) / 1000)
                var content = buildContent(context,currentA, dungluong)
                var subtext = buildSubtitle(context, currentA / 1000)
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    updateNotification(context, content, currentA, subtext)
                };
                sendIntentWidget(currentA);
            }
        }, 3000, 3000)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun updateNotification(context: Context, content: String, currentA: Int, subtext: String) {
        var builder = Notification.Builder(this, Contrants.CHANNEL_ID_BATTERY_MONITOR)
                .setSmallIcon(Icon.createWithBitmap(textAsBitmap(context, currentA)))
                .setSubText(subtext)
                .setContentText(content)
        notificationManager.notify(1122, builder.build())
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun textAsBitmap(context: Context, text: Int): Bitmap? {
        var color: Int = 0;
        if (text > 0) {
            color = R.color.use_battery_notification
        } else {
            color = R.color.charng_battery_notification
        }
        var icon = (Math.abs(text) / 1000).toString()
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.setTextSize(45f)
        paint.setColor(getColor(color))
        val baseline: Float = -paint.ascent()
        val width = (paint.measureText(icon) + 0.5f).toInt()
        val height = (baseline + paint.descent() + 0.5f).toInt()
        val image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(image)
        canvas.drawText(icon, 0f, baseline, paint)
        return image
    }

    fun buildContent(context: Context, currentA: Int, currentCap :Int) : String{
        var content = "";
        if (currentA < 0) {
            content = context.getString(R.string.Dang_sac)
        } else {
            content = context.getString(R.string.Dang_dung)
        }

        content += " " + Math.abs(currentA / 1000).toString() +" "+ context.getString(R.string.unit) + "   "
        content += " " + context.getString(R.string.Dung_luong) +" "+  currentCap + context.getString(R.string.unit)
        return content
    }

    /**
     * subtext notification
     */
    fun buildSubtitle(context: Context, curentA : Int): String = if (curentA > 0) {
            context.getString(R.string.dang_dung)
        } else if(curentA < -2000) {
            context.getString(R.string.sac_nhanh)
        } else if (curentA < -500) {
            context.getString(R.string.sac_thuong)
        } else {
            context.getString(R.string.sac_cham)
        }

    fun sendIntentWidget(currentA: Int) {
        val intent = Intent();
        intent.setAction("android.appwidget.action.APPWIDGET_UPDATE")
        intent.putExtra("EXTRA_CURRENT_A",currentA)
        sendBroadcast(intent)
    }
}