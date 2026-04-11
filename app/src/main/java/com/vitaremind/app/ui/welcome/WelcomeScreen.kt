package com.vitaremind.app.ui.welcome

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vitaremind.app.R
import com.vitaremind.app.ui.theme.Purple400
import com.vitaremind.app.ui.theme.Teal500
import kotlinx.coroutines.delay

// ── Google Font: Nunito (rounded, friendly, stylish) ──────────────────────────
private val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage  = "com.google.android.gms",
    certificates     = R.array.com_google_android_gms_fonts_certs
)

private val NunitoFont = GoogleFont("Nunito")

val NunitoFontFamily = FontFamily(
    Font(googleFont = NunitoFont, fontProvider = provider, weight = FontWeight.Normal),
    Font(googleFont = NunitoFont, fontProvider = provider, weight = FontWeight.SemiBold),
    Font(googleFont = NunitoFont, fontProvider = provider, weight = FontWeight.Bold),
    Font(googleFont = NunitoFont, fontProvider = provider, weight = FontWeight.ExtraBold),
)

// ── Onboarding page data ───────────────────────────────────────────────────────
private data class OnboardingPage(
    val backgroundColor: Color,
    val icon: ImageVector,
    val secondIcon: ImageVector? = null,
    val title: String,
    val subtitle: String,
    val showGetStarted: Boolean = false,
    val showBranding: Boolean = false   // VitaRemind logo + name on first page
)

private val pages = listOf(
    OnboardingPage(
        backgroundColor = Teal500,
        icon = Icons.Filled.WaterDrop,
        title = "Stay Hydrated",
        subtitle = "Track your daily water intake effortlessly",
        showBranding = true
    ),
    OnboardingPage(
        backgroundColor = Purple400,
        icon = Icons.Filled.Medication,
        title = "Never Miss a Dose",
        subtitle = "Smart reminders for all your medications"
    ),
    OnboardingPage(
        backgroundColor = Color.White,
        icon = Icons.Filled.WaterDrop,
        secondIcon = Icons.Filled.Medication,
        title = "Ready to Start",
        subtitle = "Set your goals and let VitaRemind do the rest",
        showGetStarted = true
    )
)

// ── Main composable ────────────────────────────────────────────────────────────
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WelcomeScreen(onNavigateToHome: () -> Unit) {
    val pagerState = rememberPagerState(pageCount = { pages.size })

    // Auto-advance every 1.5 s; pauses on last page
    LaunchedEffect(pagerState.currentPage) {
        if (pagerState.currentPage < pages.size - 1) {
            delay(1500L)
            pagerState.animateScrollToPage(
                page = pagerState.currentPage + 1,
                animationSpec = tween(durationMillis = 600)
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { pageIndex ->
            PageContent(page = pages[pageIndex], onGetStarted = onNavigateToHome)
        }

        // Skip button — pages 0 and 1 only
        if (pagerState.currentPage < 2) {
            TextButton(
                onClick = onNavigateToHome,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 52.dp, end = 16.dp)
            ) {
                Text(
                    text = "Skip",
                    color = Color.White,
                    fontFamily = NunitoFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
            }
        }

        // Page indicator dots
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 52.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(pages.size) { index ->
                val isSelected = pagerState.currentPage == index
                val dotWidth by animateDpAsState(
                    targetValue = if (isSelected) 28.dp else 8.dp,
                    animationSpec = tween(300),
                    label = "dot_w_$index"
                )
                val dotColor by animateColorAsState(
                    targetValue = if (isSelected) Color.White else Color.White.copy(alpha = 0.35f),
                    animationSpec = tween(300),
                    label = "dot_c_$index"
                )
                Box(
                    modifier = Modifier
                        .height(8.dp)
                        .width(dotWidth)
                        .clip(CircleShape)
                        .background(dotColor)
                )
            }
        }
    }
}

// ── Single page ────────────────────────────────────────────────────────────────
@Composable
private fun PageContent(page: OnboardingPage, onGetStarted: () -> Unit) {
    val isLight = page.backgroundColor == Color.White
    val contentColor = if (isLight) Color(0xFF1A1A1A) else Color.White

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(page.backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 36.dp)
        ) {

            // ── Branding block (page 1 only) ──────────────────────────────────
            if (page.showBranding) {
                // Logo image
                Image(
                    painter = painterResource(id = R.drawable.ic_vitaremind_logo),
                    contentDescription = "VitaRemind Logo",
                    modifier = Modifier.size(120.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // App name — "Vita" normal + "Remind" bold for visual split
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Vita",
                        fontFamily = NunitoFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 42.sp,
                        color = Color.White,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Remind",
                        fontFamily = NunitoFontFamily,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 42.sp,
                        color = Color.White.copy(alpha = 0.85f),
                        letterSpacing = 1.sp
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Your health companion",
                    fontFamily = NunitoFontFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.7f),
                    letterSpacing = 2.sp
                )

                Spacer(modifier = Modifier.height(48.dp))
            } else {
                // ── Regular page icon ─────────────────────────────────────────
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = page.icon,
                        contentDescription = null,
                        tint = if (isLight) Teal500 else Color.White,
                        modifier = Modifier.size(88.dp)
                    )
                    page.secondIcon?.let { second ->
                        Icon(
                            imageVector = second,
                            contentDescription = null,
                            tint = Purple400,
                            modifier = Modifier.size(88.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(48.dp))
            }

            // ── Title ─────────────────────────────────────────────────────────
            Text(
                text = page.title,
                fontFamily = NunitoFontFamily,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 30.sp,
                color = contentColor,
                textAlign = TextAlign.Center,
                lineHeight = 36.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ── Subtitle ──────────────────────────────────────────────────────
            Text(
                text = page.subtitle,
                fontFamily = NunitoFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 17.sp,
                color = contentColor.copy(alpha = 0.75f),
                textAlign = TextAlign.Center,
                lineHeight = 24.sp
            )

            // ── Get Started button (last page) ────────────────────────────────
            if (page.showGetStarted) {
                Spacer(modifier = Modifier.height(56.dp))
                Button(
                    onClick = onGetStarted,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Teal500,
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(58.dp)
                ) {
                    Text(
                        text = "Get Started",
                        fontFamily = NunitoFontFamily,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }
    }
}
