package com.dpStudio.battery.util

import android.content.Context
import android.util.Log
import android.view.View
import com.dpStudio.battery.R
import com.google.android.gms.ads.*

object Utils{

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

    /**
     * Init Ads
     */
    fun initAds(context: Context) {
        MobileAds.initialize(context) {}
    }

    /**
     * Quang cao banner
     */
    fun initBannerAds(context: Context, adView: AdView) {
        adView.visibility = View.GONE
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
        adView.adListener = object: AdListener() {
            override fun onAdLoaded() {
                super.onAdLoaded()
                adView.visibility = View.VISIBLE
            }
        }
    }

    /**
     * quang cao trung gian
     */

    fun initQuangCaoTrungGian(context: Context) : InterstitialAd {
        val interitalAds = InterstitialAd(context)
        interitalAds.adUnitId = context.getString(R.string.id_quang_cao_trung_gian)
        interitalAds.loadAd(AdRequest.Builder().build())
        return interitalAds
    }
}