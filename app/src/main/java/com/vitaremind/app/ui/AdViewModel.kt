package com.vitaremind.app.ui

import androidx.lifecycle.ViewModel
import com.vitaremind.app.util.AdManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Thin ViewModel that exposes the AdManager singleton to Composables.
 * Pre-loads interstitial and rewarded ads when first created.
 */
@HiltViewModel
class AdViewModel @Inject constructor(
    val adManager: AdManager
) : ViewModel() {

    init {
        adManager.loadInterstitial()
        adManager.loadRewarded()
    }
}
