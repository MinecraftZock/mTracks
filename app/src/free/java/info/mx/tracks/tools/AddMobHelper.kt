package info.mx.tracks.tools

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.provider.Settings
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import info.mx.tracks.BuildConfig
import info.mx.core.MxCoreApplication
import info.mx.tracks.R
import timber.log.Timber

class AddMobHelper(context: Context) : IAddMobHelper {

    init {
        if (!initDone) {
            MobileAds.initialize(context) { Timber.d("init done") }
            initDone = true
        }
    }

    @SuppressLint("HardwareIds")
    override fun onCreateView(activity: Activity) {
        val adView = activity.findViewById<AdView>(R.id.adView)
        adView?.let {
            val androidId = Settings.Secure.getString(activity.contentResolver, Settings.Secure.ANDROID_ID)

            val testDevices: MutableList<String> = ArrayList()

            if (MxCoreApplication.isEmulator) {
                testDevices.add(AdRequest.DEVICE_ID_EMULATOR)
            }
            if (BuildConfig.DEBUG) {
                testDevices.add(androidId)
            }

            val requestConfiguration = RequestConfiguration.Builder().setTestDeviceIds(testDevices).build()
            MobileAds.setRequestConfiguration(requestConfiguration)

            it.adListener = object : AdListener() {
                override fun onAdLoaded() {
                    // Code to be executed when an ad finishes loading.
                }
                override fun onAdOpened() {
                    // Code to be executed when an ad opens an overlay that covers the screen.
                }

                override fun onAdClosed() {
                    // Code to be executed when when the user is about to return to the app after tapping on an ad.
                }
            }

            it.loadAd(AdRequest.Builder().build())
        }
    }

    override fun onPause(activity: Activity) {
        activity.findViewById<AdView>(R.id.adView)?.pause()
    }

    override fun onResume(activity: Activity) {
        activity.findViewById<AdView>(R.id.adView)?.resume()
    }

    override fun onDestroy(activity: Activity) {
        activity.findViewById<AdView>(R.id.adView)?.destroy()
    }

    override fun showInterstitial(activity: Activity) {
        InterstitialAd.load(
                activity,
                activity.getString(R.string.admob_interstitial),
                AdRequest.Builder().build(),
                object : InterstitialAdLoadCallback() {
                    override fun onAdLoaded(interstitialAd: InterstitialAd) {
                        // The mInterstitialAd reference will be null until an ad is loaded.
                        mInterstitialAd = interstitialAd
                        mInterstitialAd?.fullScreenContentCallback =
                                object : FullScreenContentCallback() {
                                    override fun onAdDismissedFullScreenContent() {
                                        super.onAdDismissedFullScreenContent()
                                        mInterstitialAd = null

                                        //// perform your code that you wants to do after ad dismissed or closed
                                    }

                                    override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                                        super.onAdFailedToShowFullScreenContent(adError)
                                        mInterstitialAd = null

                                        /// perform your action here when ad will not load
                                    }

                                    override fun onAdShowedFullScreenContent() {
                                        super.onAdShowedFullScreenContent()
                                        mInterstitialAd = null
                                    }
                                }
                    }

                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        mInterstitialAd = null
                    }
                })
    }

    companion object {
        private var initDone = false
        private var mInterstitialAd: InterstitialAd? = null
    }
}
