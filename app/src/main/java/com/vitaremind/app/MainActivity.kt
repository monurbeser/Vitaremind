package com.vitaremind.app

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vitaremind.app.data.datastore.UserPreferencesDataStore
import com.vitaremind.app.ui.theme.VitaRemindTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var userPrefs: UserPreferencesDataStore

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        splashScreen.setOnExitAnimationListener { splashScreenView ->
            val scaleX = ObjectAnimator.ofFloat(
                splashScreenView.iconView, View.SCALE_X, 0.8f, 1f
            )
            val scaleY = ObjectAnimator.ofFloat(
                splashScreenView.iconView, View.SCALE_Y, 0.8f, 1f
            )
            AnimatorSet().apply {
                interpolator = OvershootInterpolator()
                duration = 500L
                playTogether(scaleX, scaleY)
                doOnEnd { splashScreenView.remove() }
                start()
            }
        }

        setContent {
            val themePreference by userPrefs.themePreference
                .collectAsStateWithLifecycle(initialValue = "system")

            val darkTheme = when (themePreference) {
                "dark"  -> true
                "light" -> false
                else    -> null // null = follow system
            }

            VitaRemindTheme(darkTheme = darkTheme) {
                VitaRemindNavHost()
            }
        }
    }
}
