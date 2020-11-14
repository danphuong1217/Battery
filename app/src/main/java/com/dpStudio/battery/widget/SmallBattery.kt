package com.dpStudio.battery.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import com.dpStudio.battery.R
import com.dpStudio.battery.util.Utils


/**
 * Implementation of App Widget functionality.
 */
class SmallBattery : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
        Log.d("dannvb", "Update")
    }

    override fun onEnabled(context: Context) {

    }

    override fun onDisabled(context: Context) {

    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
        val views = RemoteViews(context?.packageName, R.layout.small_battery)
        val cuA = intent?.getIntExtra("EXTRA_CURRENT_A",1)
        views.setTextViewText(R.id.tv_small_widget_status, Utils.buildSubtitle(context!!, cuA!!/1000))
        views.setTextViewText(R.id.tv_small_widget_currentA, (Math.abs(cuA)/1000).toString())
        val componentName = ComponentName(context!!, SmallBattery::class.java)
        AppWidgetManager.getInstance(context).updateAppWidget(componentName, views);
    }
}

internal fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
    val views = RemoteViews(context.packageName, R.layout.small_battery)
//    val textCurrent = views.setTextViewText()
    appWidgetManager.updateAppWidget(appWidgetId, views)
}