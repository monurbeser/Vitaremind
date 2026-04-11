package com.vitaremind.app.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary              = Teal500,
    onPrimary            = OnPrimary,
    primaryContainer     = Teal100,
    onPrimaryContainer   = Teal700,
    secondary            = Purple400,
    onSecondary          = OnSecondary,
    secondaryContainer   = Purple100,
    onSecondaryContainer = Purple700,
    background           = Background,
    onBackground         = OnBackground,
    surface              = Surface,
    onSurface            = OnSurface,
    error                = ErrorRed,
    onError              = OnError,
)

private val DarkColorScheme = darkColorScheme(
    primary              = Teal200Dark,
    onPrimary            = OnPrimaryDark,
    primaryContainer     = TealContainerDark,
    onPrimaryContainer   = Teal100,
    secondary            = Purple200Dark,
    onSecondary          = OnSecondaryDark,
    secondaryContainer   = PurpleContainerDark,
    onSecondaryContainer = Purple100,
    background           = BackgroundDark,
    onBackground         = OnBackgroundDark,
    surface              = SurfaceDark,
    onSurface            = OnSurfaceDark,
    error                = ErrorRedDark,
    onError              = OnErrorDark,
)

@Composable
fun VitaRemindTheme(
    darkTheme: Boolean? = null,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val useDark = darkTheme ?: isSystemInDarkTheme()
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (useDark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        useDark -> DarkColorScheme
        else    -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = VitaRemindTypography,
        shapes      = VitaRemindShapes,
        content     = content
    )
}
