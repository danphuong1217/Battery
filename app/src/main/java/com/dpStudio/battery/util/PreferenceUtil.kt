package com.dpStudio.battery.util

import android.content.Context
import android.content.SharedPreferences

class PreferenceUtil {
    companion object {
        private val NAME_PREFERENCE = "APP_MONITOR";
        lateinit var sharedPreferences : SharedPreferences
        lateinit var preferenceUtil: PreferenceUtil

        private fun PreferenceUtil(context: Context){
            sharedPreferences = context.getSharedPreferences(NAME_PREFERENCE, Context.MODE_PRIVATE)

        }

        fun getMaxChanrge() {
            sharedPreferences.getInt("max_mAh_Charge", 3000)
        }

        fun setMaxChanrge(max : Int) {
            sharedPreferences.edit().putInt("max_mAh_Charge", max).commit()
        }

        fun getMaxCapity() {
            sharedPreferences.getInt("max_mAh_Capity", 1000)
        }

        fun setMaxCapity(max : Int) {
            sharedPreferences.edit().putInt("max_mAh_Capity", max).commit()
        }
    }
}