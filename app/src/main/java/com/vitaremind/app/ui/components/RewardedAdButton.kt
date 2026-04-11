package com.vitaremind.app.ui.components

import android.app.Activity
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.vitaremind.app.ui.theme.Teal500
import com.vitaremind.app.util.AdManager

@Composable
fun RewardedAdButton(
    adManager: AdManager,
    onRewardGranted: () -> Unit,
    modifier: Modifier = Modifier,
    label: String = "View Weekly Report"
) {
    val context = LocalContext.current

    Button(
        onClick = {
            val activity = context as? Activity ?: return@Button
            adManager.showRewarded(
                activity = activity,
                onRewarded = onRewardGranted
            )
        },
        colors = ButtonDefaults.buttonColors(containerColor = Teal500),
        modifier = modifier
    ) {
        Icon(Icons.Filled.Lock, contentDescription = null, tint = Color.White)
        Spacer(Modifier.width(8.dp))
        Text(label, color = Color.White)
    }
}
