package com.vitaremind.app.util

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.vitaremind.app.BuildConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private const val TAG = "AdManager"

        private const val TEST_BANNER_ID       = "ca-app-pub-3940256099942544/6300978111"
        private const val TEST_INTERSTITIAL_ID = "ca-app-pub-3940256099942544/1033173712"
        private const val TEST_REWARDED_ID     = "ca-app-pub-3940256099942544/5224354917"

        private const val PROD_BANNER_ID       = "ca-app-pub-1407872603866175/6040598696"
        private const val PROD_INTERSTITIAL_ID = "ca-app-pub-1407872603866175/3278703983"
        private const val PROD_REWARDED_ID     = "ca-app-pub-1407872603866175/9652540643"
    }

    val isTestMode: Boolean get() = BuildConfig.DEBUG

    val bannerAdUnitId: String
        get() = if (isTestMode) TEST_BANNER_ID else PROD_BANNER_ID

    val interstitialAdUnitId: String
        get() = if (isTestMode) TEST_INTERSTITIAL_ID else PROD_INTERSTITIAL_ID

    val rewardedAdUnitId: String
        get() = if (isTestMode) TEST_REWARDED_ID else PROD_REWARDED_ID

    // ── State ──────────────────────────────────────────────────────────────────
    private var interstitialAd:       InterstitialAd? = null
    private var rewardedAd:           RewardedAd?     = null
    private var isInterstitialLoading = false
    private var isRewardedLoading     = false

    val isInterstitialReady: Boolean get() = interstitialAd != null
    val isRewardedReady:     Boolean get() = rewardedAd     != null

    // ── Pre-warm on construction ───────────────────────────────────────────────
    // Singleton oluşturulduğu anda her iki ad tipi için yükleme başlar.
    // Böylece ilk showInterstitial() çağrısı null'a düşmez.
    init {
        loadInterstitial()
        loadRewarded()
    }

    // ── Interstitial ───────────────────────────────────────────────────────────

    fun loadInterstitial() {
        // Guard: zaten yüklüyse veya yükleniyorsa duplicate request gönderme.
        if (interstitialAd != null || isInterstitialLoading) return
        isInterstitialLoading = true
        Log.d(TAG, "loadInterstitial — request gönderiliyor")

        InterstitialAd.load(
            context,
            interstitialAdUnitId,
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    Log.d(TAG, "loadInterstitial — onAdLoaded ✓")
                    interstitialAd    = ad
                    isInterstitialLoading = false
                }
                override fun onAdFailedToLoad(error: LoadAdError) {
                    Log.e(TAG, "loadInterstitial — onAdFailedToLoad [${error.code}] ${error.message}")
                    interstitialAd    = null
                    isInterstitialLoading = false
                    // Retry yapmıyoruz — bir sonraki showInterstitial() tetikleyecek.
                }
            }
        )
    }

    /**
     * Reklamı gösterir.
     *
     * @return true  → reklam gösterildi; onDismissed dismiss sonrası çağrılacak.
     *         false → reklam henüz hazır değil; yükleme başlatıldı; onDismissed ÇAĞRILMAZ.
     *                 Caller bu durumu "reklam atlandı" olarak işleyebilir veya
     *                 isInterstitialReady flag'ini kontrol edebilir.
     *
     * Neden null durumunda onDismissed çağrılmıyor?
     * Çünkü caller onDismissed'ı "reklam gösterildi ve kapandı" sinyali olarak kullanır.
     * Null durumunda onDismissed'ı hemen çağırmak, hiç gösterilmemiş bir reklamın
     * "gösterilip kapandığını" simüle eder — bu monetization kaybıdır.
     */
    fun showInterstitial(activity: Activity, onDismissed: () -> Unit = {}): Boolean {
        val ad = interstitialAd
        if (ad == null) {
            Log.w(TAG, "showInterstitial — ad null, yükleme başlatıldı (fırsat kaçtı)")
            if (!isInterstitialLoading) loadInterstitial()
            return false
        }

        // Referansı hemen temizle — aynı ad object'in çift gösterilmesini engeller.
        // show() öncesi null set etmek güvenli: local val `ad` hâlâ geçerli.
        interstitialAd = null

        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdShowedFullScreenContent() {
                Log.d(TAG, "showInterstitial — onAdShowedFullScreenContent ✓ (impression)")
            }
            override fun onAdImpression() {
                Log.d(TAG, "showInterstitial — onAdImpression ✓ (SDK impression confirmed)")
            }
            override fun onAdFailedToShowFullScreenContent(error: AdError) {
                // show() çağrıldı ama ekrana çıkmadı.
                // Bu durumu eski kod hiç handle etmiyordu: interstitialAd temizlenmiyordu,
                // preload tetiklenmiyordu, onDismissed asla çağrılmıyordu → caller donuyordu.
                Log.e(TAG, "showInterstitial — onAdFailedToShowFullScreenContent [${error.code}] ${error.message}")
                loadInterstitial()
                onDismissed()   // caller'ı bloklamamak için dismiss sinyali gönder
            }
            override fun onAdDismissedFullScreenContent() {
                Log.d(TAG, "showInterstitial — onAdDismissedFullScreenContent")
                loadInterstitial()  // bir sonraki gösterim için preload başlat
                onDismissed()
            }
        }

        ad.show(activity)
        return true
    }

    // ── Rewarded ───────────────────────────────────────────────────────────────

    fun loadRewarded() {
        if (rewardedAd != null || isRewardedLoading) return
        isRewardedLoading = true
        Log.d(TAG, "loadRewarded — request gönderiliyor")

        RewardedAd.load(
            context,
            rewardedAdUnitId,
            AdRequest.Builder().build(),
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) {
                    Log.d(TAG, "loadRewarded — onAdLoaded ✓")
                    rewardedAd    = ad
                    isRewardedLoading = false
                }
                override fun onAdFailedToLoad(error: LoadAdError) {
                    Log.e(TAG, "loadRewarded — onAdFailedToLoad [${error.code}] ${error.message}")
                    rewardedAd    = null
                    isRewardedLoading = false
                }
            }
        )
    }

    /**
     * @return true  → reklam gösterildi.
     *         false → ad hazır değil; yükleme başlatıldı; onDismissed/onRewarded çağrılmaz.
     */
    fun showRewarded(
        activity:    Activity,
        onRewarded:  () -> Unit,
        onDismissed: () -> Unit = {}
    ): Boolean {
        val ad = rewardedAd
        if (ad == null) {
            Log.w(TAG, "showRewarded — ad null, yükleme başlatıldı (fırsat kaçtı)")
            if (!isRewardedLoading) loadRewarded()
            return false
        }

        rewardedAd = null

        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdShowedFullScreenContent() {
                Log.d(TAG, "showRewarded — onAdShowedFullScreenContent ✓ (impression)")
            }
            override fun onAdImpression() {
                Log.d(TAG, "showRewarded — onAdImpression ✓")
            }
            override fun onAdFailedToShowFullScreenContent(error: AdError) {
                Log.e(TAG, "showRewarded — onAdFailedToShowFullScreenContent [${error.code}] ${error.message}")
                loadRewarded()
                onDismissed()
            }
            override fun onAdDismissedFullScreenContent() {
                Log.d(TAG, "showRewarded — onAdDismissedFullScreenContent")
                loadRewarded()
                onDismissed()
            }
        }

        ad.show(activity) { _ -> onRewarded() }
        return true
    }
}
