package com.vitaremind.app.ui.water.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vitaremind.app.ui.theme.NunitoFontFamily
import com.vitaremind.app.ui.theme.Teal100
import com.vitaremind.app.ui.theme.Teal500
import com.vitaremind.app.ui.theme.TextSecondary

@Composable
fun WaterCircularProgress(
    consumed: Int,
    goal: Int,
    modifier: Modifier = Modifier,
    size: Dp = 220.dp,
    strokeWidth: Dp = 18.dp
) {
    val progress = if (goal > 0) (consumed.toFloat() / goal).coerceIn(0f, 1f) else 0f
    val animatedProgress by animateFloatAsState(
        targetValue   = progress,
        animationSpec = tween(durationMillis = 900),
        label         = "water_progress"
    )
    val percent = (animatedProgress * 100).toInt()

    Box(
        contentAlignment = Alignment.Center,
        modifier         = modifier.size(size)
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val strokePx = strokeWidth.toPx()
            val topLeft  = Offset(strokePx / 2f, strokePx / 2f)
            val arcSize  = Size(this.size.width - strokePx, this.size.height - strokePx)

            // Background track
            drawArc(
                color      = Teal100,
                startAngle = 135f,
                sweepAngle = 270f,
                useCenter  = false,
                topLeft    = topLeft,
                size       = arcSize,
                style      = Stroke(width = strokePx, cap = StrokeCap.Round)
            )

            // Progress arc — gradient feel via single color with glow at end
            if (animatedProgress > 0f) {
                drawArc(
                    brush      = Brush.sweepGradient(
                        colorStops = arrayOf(
                            0.0f to Teal500.copy(alpha = 0.7f),
                            1.0f to Teal500
                        )
                    ),
                    startAngle = 135f,
                    sweepAngle = 270f * animatedProgress,
                    useCenter  = false,
                    topLeft    = topLeft,
                    size       = arcSize,
                    style      = Stroke(width = strokePx, cap = StrokeCap.Round)
                )
            }
        }

        // Center content
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text       = "$consumed",
                fontSize   = 36.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = NunitoFontFamily,
                color      = Teal500
            )
            Text(
                text       = "ml",
                fontSize   = 14.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = NunitoFontFamily,
                color      = Teal500
            )
            Text(
                text       = "of $goal ml",
                fontSize   = 12.sp,
                fontFamily = NunitoFontFamily,
                color      = TextSecondary
            )
        }

        // % badge — top-right of the circle
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = (-8).dp, y = 16.dp)
                .background(Teal500, shape = RoundedCornerShape(12.dp))
                .padding(horizontal = 8.dp, vertical = 3.dp)
        ) {
            Text(
                text       = "$percent%",
                fontSize   = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = NunitoFontFamily,
                color      = Color.White
            )
        }
    }
}
