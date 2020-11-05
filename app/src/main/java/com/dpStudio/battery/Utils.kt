package com.dpStudio.battery

import android.content.Context
import android.util.Log

object Utils{

    /**
     * subtext notification
     */
    fun buildSubtitle(context: Context, curentA : Int): String = if (curentA > 0) {
        context.getString(R.string.dang_dung)
    } else if(curentA < -2000) {
        Log.d("dannvb", curentA.toString());
        context.getString(R.string.sac_nhanh)
    } else if (curentA < -500) {
        context.getString(R.string.sac_thuong)
    } else {
        context.getString(R.string.sac_cham)
    }
}