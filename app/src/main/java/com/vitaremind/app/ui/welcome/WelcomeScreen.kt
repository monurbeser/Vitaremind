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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vitaremind.app.R
import com.vitaremind.app.ui.theme.NunitoFontFamily
import com.vitaremind.app.ui.theme.Purple400
import com.vitaremind.app.ui.theme.Purple50
import com.vitaremind.app.ui.theme.Teal500
import com.vitaremind.app.ui.theme.Teal50
import kotlinx.coroutines.launch

// ── Onboarding page data ───────────────────────────────────────────────────────
private data class OnboardingPage(
    val gradientStart:   Color,
    val gradientEnd:     Color,
    val icon:            ImageVector,
    val secondIcon:      ImageVector? = null,
    val iconTint:        Color,
    val secondIconTint:  Color = Color.White,
    val iconBg:          Color,
    val title:           String,
    val subtitle:        String,
    val showBranding:    Boolean = false,
    val isLight:         Boolean = false
)

private val pages = listOf(
    OnboardingPage(
        gradientStart = Teal500,
        gradientEnd   = Color(0xFF007A65),
        icon          = Icons.Filled.WaterDrop,
        iconTint      = Teal500,
        iconBg        = Color.White,
        title         = "Stay Hydrated",
        subtitle      = "Track your daily water intake and reach your hydration goals effortlessly",
        showBranding  = true
    ),
    OnboardingPage(
        gradientStart = Purple400,
        gradientEnd   = Color(0xFF4A3EC7),
        icon          = Icons.Filled.Medication,
        iconTint      = Purple400,
        iconBg        = Color.White,
        title         = "Never Miss a Dose",
        subtitle      = "Smart reminders for all your medications — always on time"
    ),
    OnboardingPage(
        gradientStart  = Color(0xFF2D2A6E),
        gradientEnd    = Color(0xFF1D9E75),
        icon           = Icons.Filled.WaterDrop,
        secondIcon     = Icons.Filled.Medication,
        iconTint       = Color.White,
        secondIconTint = Color.White,
        iconBg         = Color.White,
        title          = "Ready to Start!",
        subtitle       = "Set your goals and let VitaRemind take care of the rest",
        isLight        = false
    )
)

// ── Main composable ────────────────────────────────────────────────────────────
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WelcomeScreen(onNavigateToHome: () -> Unit) {
    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope      = rememberCoroutineScope()
    val isLastPage = pagerState.currentPage == pages.size - 1

    // ── Background gradient interpolates smoothly between pages ───────────────
    val bgStart by remember {
        derivedStateOf {
            val current = pagerState.currentPage
            val offset  = pagerState.currentPageOffsetFraction
            val current0 = pages[current].gradientStart
            when {
                offset < 0f && current < pages.size - 1 ->
                    lerp(current0, pages[current + 1].gradientStart, (-offset).coerceIn(0f, 1f))
                offset > 0f && current > 0 ->
                    lerp(current0, pages[current - 1].gradientStart, offset.coerceIn(0f, 1f))
                else -> current0
            }
        }
    }
    val bgEnd by remember {
        derivedStateOf {
            val current = pagerState.currentPage
            val offset  = pagerState.currentPageOffsetFraction
            val current1 = pages[current].gradientEnd
            when {
                offset < 0f && current < pages.size - 1 ->
                    lerp(current1, pages[current + 1].gradientEnd, (-offset).coerceIn(0f, 1f))
                offset > 0f && current > 0 ->
                    lerp(current1, pages[current - 1].gradientEnd, offset.coerceIn(0f, 1f))
                else -> current1
            }
        }
    }

    val isLastLight = pages[pagerState.currentPage].isLight
    val skipColor   = if (isLastLight) Teal500 else Color.White.copy(alpha = 0.85f)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(bgStart, bgEnd)))
    ) {
        // ── Decorative circles ────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .size(260.dp)
                .align(Alignment.TopEnd)
                .padding(top = 0.dp, end = 0.dp)
                .background(
                    brush  = Brush.radialGradient(listOf(Color.White.copy(alpha = 0.08f), Color.Transparent)),
                    shape  = CircleShape
                )
        )
        Box(
            modifier = Modifier
                .size(180.dp)
                .align(Alignment.BottomStart)
                .background(
                    brush  = Brush.radialGradient(listOf(Color.White.copy(alpha = 0.06f), Color.Transparent)),
                    shape  = CircleShape
                )
        )

        // ── Pager ─────────────────────────────────────────────────────────────
        HorizontalPager(
            state    = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { pageIndex ->
            PageContent(page = pages[pageIndex])
        }

        // ── Skip button ───────────────────────────────────────────────────────
        if (!isLastPage) {
            TextButton(
                onClick  = onNavigateToHome,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 48.dp, end = 16.dp)
            ) {
                Text(
                    text       = "Skip",
                    color      = skipColor,
                    fontFamily = NunitoFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize   = 15.sp
                )
            }
        }

        // ── Bottom: dots + button ─────────────────────────────────────────────
        Column(
            modifier            = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 28.dp, vertical = 44.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment     = Alignment.CenterVertically
            ) {
                repeat(pages.size) { index ->
                    val isSelected = pagerState.currentPage == index
                    val dotWidth by animateDpAsState(
                        targetValue   = if (isSelected) 28.dp else 8.dp,
                        animationSpec = tween(300),
                        label         = "dot_w_$index"
                    )
                    val dotColor by animateColorAsState(
                        targetValue   = if (isSelected) {
                            if (isLastLight) Teal500 else Color.White
                        } else {
                            if (isLastLight) Teal500.copy(alpha = 0.3f) else Color.White.copy(alpha = 0.35f)
                        },
                        animationSpec = tween(300),
                        label         = "dot_c_$index"
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

            val isLastPageCurrent = pagerState.currentPage == pages.size - 1
            Button(
                onClick = {
                    if (isLastPageCurrent) {
                        onNavigateToHome()
                    } else {
                        scope.launch {
                            pagerState.animateScrollToPage(
                                page          = pagerState.currentPage + 1,
                                animationSpec = tween(400)
                            )
                        }
                    }
                },
                shape  = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isLastPageCurrent) Teal500 else Color.White.copy(alpha = 0.22f),
                    contentColor   = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(
                    text          = if (isLastPageCurrent) "Get Started" else "Next",
                    fontFamily    = NunitoFontFamily,
                    fontWeight    = FontWeight.ExtraBold,
                    fontSize      = 17.sp,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}

// ── Single page content ────────────────────────────────────────────────────────
@Composable
private fun PageContent(page: OnboardingPage) {
    val contentColor = if (page.isLight) Color(0xFF1A1A2E) else Color.White

    Box(
        modifier         = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier            = Modifier
                .fillMaxWidth()
                .padding(horizontal = 36.dp)
                .padding(bottom = 160.dp)
        ) {
            // ── Branding page (page 1) ────────────────────────────────────────
            if (page.showBranding) {
                // Logo in white circle
                Surface(
                    shape = CircleShape,
                    color = Color.White,
                    shadowElevation = 8.dp,
                    modifier = Modifier.size(120.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Image(
                            painter            = painterResource(id = R.drawable.ic_vitaremind_logo),
                            contentDescription = "VitaRemind Logo",
                            modifier           = Modifier.size(80.dp)
                        )
                    }
                }
                Spacer(Modifier.height(28.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text          = "Vita",
                        fontFamily    = NunitoFontFamily,
                        fontWeight    = FontWeight.Bold,
                        fontSize      = 40.sp,
                        color         = Color.White,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text          = "Remind",
                        fontFamily    = NunitoFontFamily,
                        fontWeight    = FontWeight.ExtraBold,
                        fontSize      = 40.sp,
                        color         = Color.White,
                        letterSpacing = 0.5.sp
                    )
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    text          = "Your health companion",
                    fontFamily    = NunitoFontFamily,
                    fontWeight    = FontWeight.Normal,
                    fontSize      = 14.sp,
                    color         = Color.White.copy(alpha = 0.72f),
                    letterSpacing = 2.sp
                )
                Spacer(Modifier.height(48.dp))

            } else if (page.secondIcon != null) {
                // ── Last page — two icons side by side in colored circles ─────
                Row(
                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Surface(
                        shape           = CircleShape,
                        color           = Teal50,
                        shadowElevation = 4.dp,
                        modifier        = Modifier.size(96.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector        = page.icon,
                                contentDescription = null,
                                tint               = page.iconTint,
                                modifier           = Modifier.size(52.dp)
                            )
                        }
                    }
                    Surface(
                        shape           = CircleShape,
                        color           = Purple50,
                        shadowElevation = 4.dp,
                        modifier        = Modifier.size(96.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector        = page.secondIcon,
                                contentDescription = null,
                                tint               = page.secondIconTint,
                                modifier           = Modifier.size(52.dp)
                            )
                        }
                    }
                }
                Spacer(Modifier.height(48.dp))

            } else {
                // ── Icon in white circle ──────────────────────────────────────
                Surface(
                    shape           = CircleShape,
                    color           = Color.White.copy(alpha = 0.20f),
                    shadowElevation = 0.dp,
                    modifier        = Modifier.size(132.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Surface(
                            shape           = CircleShape,
                            color           = Color.White,
                            shadowElevation = 6.dp,
                            modifier        = Modifier.size(96.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector        = page.icon,
                                    contentDescription = null,
                                    tint               = page.iconTint,
                                    modifier           = Modifier.size(52.dp)
                                )
                            }
                        }
                    }
                }
                Spacer(Modifier.height(48.dp))
            }

            // ── Title ─────────────────────────────────────────────────────────
            Text(
                text       = page.title,
                fontFamily = NunitoFontFamily,
                fontWeight = FontWeight.ExtraBold,
                fontSize   = 28.sp,
                color      = contentColor,
                textAlign  = TextAlign.Center,
                lineHeight = 36.sp
            )
            Spacer(Modifier.height(14.dp))

            // ── Subtitle ──────────────────────────────────────────────────────
            Text(
                text       = page.subtitle,
                fontFamily = NunitoFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize   = 16.sp,
                color      = contentColor.copy(alpha = 0.72f),
                textAlign  = TextAlign.Center,
                lineHeight = 24.sp
            )
        }
    }
}
