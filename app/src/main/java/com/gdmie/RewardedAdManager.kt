package com.gdmie

import android.app.Activity
import android.content.Context
import android.widget.Toast
import com.gdmie.game.GDMIEGameProgress
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import java.util.UUID

object RewardedAdManager {

    private const val TEST_REWARDED_AD_ID =
        "ca-app-pub-9089838217001612/2598684032"

    private var rewardedAd: RewardedAd? = null
    private var loading = false

    fun preload(context: Context) {
        if (rewardedAd != null || loading) return

        loading = true

        RewardedAd.load(
            context.applicationContext,
            TEST_REWARDED_AD_ID,
            AdRequest.Builder().build(),
            object : RewardedAdLoadCallback() {

                override fun onAdFailedToLoad(error: LoadAdError) {
                    rewardedAd = null
                    loading = false
                }

                override fun onAdLoaded(ad: RewardedAd) {
                    rewardedAd = ad
                    loading = false
                }
            }
        )
    }

    fun show(
        activity: Activity,
        onRewarded: (Int) -> Unit = {}
    ) {
        val ad = rewardedAd

        if (ad == null) {
            preload(activity)

            Toast.makeText(
                activity,
                "Reward video is loading. Try again in a moment.",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        rewardedAd = null

        val rewardId = UUID.randomUUID().toString()

        ad.fullScreenContentCallback =
            object : FullScreenContentCallback() {

                override fun onAdDismissedFullScreenContent() {
                    preload(activity)
                }

                override fun onAdFailedToShowFullScreenContent(
                    adError: AdError
                ) {
                    preload(activity)
                }
            }

        ad.show(activity) { _: RewardItem ->

            val awarded = GDMIEGameProgress.addRewardedAdXpOnce(
                activity,
                rewardId,
                10
            )

            if (awarded > 0) {
                onRewarded(awarded)
            }
        }
    }
}
