package com.vitaremind.app.ui.home

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vitaremind.app.data.local.entity.DoseLog
import com.vitaremind.app.data.local.entity.Medicine
import com.vitaremind.app.ui.AdViewModel
import com.vitaremind.app.ui.components.BannerAdView
import com.vitaremind.app.ui.theme.NunitoFontFamily
import com.vitaremind.app.ui.theme.Purple400
import com.vitaremind.app.ui.theme.Purple50
import com.vitaremind.app.ui.theme.Teal100
import com.vitaremind.app.ui.theme.Teal50
import com.vitaremind.app.ui.theme.Teal500
import com.vitaremind.app.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToWater: () -> Unit = {},
    onNavigateToMedicine: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel(),
    adViewModel: AdViewModel = hiltViewModel()
) {
    val totalToday by viewModel.totalToday.collectAsStateWithLifecycle()
    val dailyGoal  by viewModel.dailyGoal.collectAsStateWithLifecycle()
    val weeklyData by viewModel.weeklyWaterData.collectAsStateWithLifecycle()
    val streak     by viewModel.currentStreak.collectAsStateWithLifecycle()
    val nextDoses  by viewModel.nextDoses.collectAsStateWithLifecycle()
    val medicines  by viewModel.medicines.collectAsStateWithLifecycle()

    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val greeting = when {
        hour < 12 -> "Good morning"
        hour < 17 -> "Good afternoon"
        else      -> "Good evening"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "VitaRemind",
                        style      = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = NunitoFontFamily
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor    = Teal500,
                    titleContentColor = Color.White
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        LazyColumn(
            modifier            = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding      = PaddingValues(bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Greeting
            item {
                Row(
                    modifier          = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "$greeting! \uD83D\uDC4B",
                            style      = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = NunitoFontFamily
                        )
                        Text(
                            "Here's your health summary",
                            style      = MaterialTheme.typography.bodySmall,
                            color      = TextSecondary,
                            fontFamily = NunitoFontFamily
                        )
                    }
                    if (streak > 0) {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = Color(0xFFFFF3E0)
                        ) {
                            Text(
                                "\uD83D\uDD25 $streak day streak",
                                style      = MaterialTheme.typography.labelMedium,
                                color      = Color(0xFFE65100),
                                fontWeight = FontWeight.Bold,
                                fontFamily = NunitoFontFamily,
                                modifier   = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }

            // Water summary card
            item {
                WaterSummaryCard(
                    consumed          = totalToday,
                    goal              = dailyGoal,
                    onNavigateToWater = onNavigateToWater,
                    modifier          = Modifier.padding(horizontal = 20.dp)
                )
            }

            // Today's Medicines card
            item {
                TodayMedicinesCard(
                    doses            = nextDoses,
                    medicines        = medicines,
                    onNavigateToMeds = onNavigateToMedicine,
                    modifier         = Modifier.padding(horizontal = 20.dp)
                )
            }

            // Weekly water chart
            if (weeklyData.isNotEmpty()) {
                item {
                    WeeklyWaterChart(
                        data     = weeklyData,
                        goal     = dailyGoal,
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                }
            }

            // Banner ad
            item {
                BannerAdView(
                    adManager = adViewModel.adManager,
                    modifier  = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

// ── Water Summary Card ─────────────────────────────────────────────────────────
@Composable
private fun WaterSummaryCard(
    consumed: Int,
    goal: Int,
    onNavigateToWater: () -> Unit,
    modifier: Modifier = Modifier
) {
    val progress   = if (goal > 0) (consumed.toFloat() / goal).coerceIn(0f, 1f) else 0f
    val isComplete = consumed >= goal

    var started by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { started = true }
    val animatedProgress by animateFloatAsState(
        targetValue   = if (started) progress else 0f,
        animationSpec = tween(900),
        label         = "waterProgress"
    )

    Card(
        modifier  = modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(0.dp),
        colors    = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(Teal500, Color(0xFF007A65))
                    ),
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(20.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Circular arc progress
                Box(
                    modifier         = Modifier.size(80.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val stroke = Stroke(9.dp.toPx(), cap = StrokeCap.Round)
                        val inset  = stroke.width / 2
                        val arcSize = Size(size.width - stroke.width, size.height - stroke.width)

                        drawArc(
                            color      = Color.White.copy(alpha = 0.25f),
                            startAngle = 135f,
                            sweepAngle = 270f,
                            useCenter  = false,
                            style      = stroke,
                            topLeft    = Offset(inset, inset),
                            size       = arcSize
                        )
                        drawArc(
                            color      = Color.White,
                            startAngle = 135f,
                            sweepAngle = 270f * animatedProgress,
                            useCenter  = false,
                            style      = stroke,
                            topLeft    = Offset(inset, inset),
                            size       = arcSize
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Filled.WaterDrop,
                            contentDescription = null,
                            tint     = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                        Text(
                            "${(progress * 100).toInt()}%",
                            style      = MaterialTheme.typography.labelSmall,
                            color      = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontFamily = NunitoFontFamily,
                            fontSize   = 10.sp
                        )
                    }
                }

                Spacer(Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Water Today",
                        style      = MaterialTheme.typography.labelMedium,
                        color      = Color.White.copy(alpha = 0.8f),
                        fontFamily = NunitoFontFamily
                    )
                    Text(
                        "$consumed ml",
                        style      = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color      = Color.White,
                        fontFamily = NunitoFontFamily
                    )
                    Text(
                        "of $goal ml goal",
                        style      = MaterialTheme.typography.bodySmall,
                        color      = Color.White.copy(alpha = 0.75f),
                        fontFamily = NunitoFontFamily
                    )
                    if (isComplete) {
                        Spacer(Modifier.height(2.dp))
                        Text(
                            "Goal reached! \uD83C\uDF89",
                            style      = MaterialTheme.typography.labelSmall,
                            color      = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontFamily = NunitoFontFamily
                        )
                    }
                }

                Button(
                    onClick = onNavigateToWater,
                    colors  = ButtonDefaults.buttonColors(
                        containerColor = Color.White.copy(alpha = 0.22f),
                        contentColor   = Color.White
                    ),
                    shape            = RoundedCornerShape(14.dp),
                    contentPadding   = PaddingValues(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Text(
                        "+ Add",
                        fontWeight = FontWeight.Bold,
                        fontFamily = NunitoFontFamily
                    )
                }
            }
        }
    }
}

// ── Today's Medicines Card ─────────────────────────────────────────────────────
@Composable
private fun TodayMedicinesCard(
    doses: List<DoseLog>,
    medicines: List<Medicine>,
    onNavigateToMeds: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier  = modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(0.dp),
        colors    = CardDefaults.cardColors(containerColor = Purple50)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Purple400)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Today's Medicines",
                        style      = MaterialTheme.typography.titleMedium,
                        color      = Purple400,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = NunitoFontFamily
                    )
                }
                TextButton(onClick = onNavigateToMeds) {
                    Text(
                        "View All",
                        color      = Purple400.copy(alpha = 0.8f),
                        fontFamily = NunitoFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        style      = MaterialTheme.typography.labelLarge
                    )
                }
            }

            Spacer(Modifier.height(4.dp))

            if (doses.isEmpty()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier          = Modifier.padding(vertical = 8.dp)
                ) {
                    Icon(
                        Icons.Filled.CheckCircle,
                        contentDescription = null,
                        tint     = Purple400.copy(alpha = 0.6f),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "All done for today! \u2728",
                        color      = TextSecondary,
                        style      = MaterialTheme.typography.bodyMedium,
                        fontFamily = NunitoFontFamily
                    )
                }
            } else {
                doses.forEach { dose ->
                    val medicine = medicines.find { it.id == dose.medicineId }
                    if (medicine != null) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(4.dp)
                                    .height(36.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(Color(medicine.color))
                            )
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    medicine.name,
                                    color      = MaterialTheme.colorScheme.onSurface,
                                    style      = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = NunitoFontFamily
                                )
                                Text(
                                    "${medicine.dosage}  \u00B7  ${timeFormat.format(Date(dose.scheduledTime))}",
                                    color      = TextSecondary,
                                    style      = MaterialTheme.typography.labelSmall,
                                    fontFamily = NunitoFontFamily
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ── Weekly Water Bar Chart ─────────────────────────────────────────────────────
@Composable
private fun WeeklyWaterChart(
    data: List<Pair<String, Int>>,
    goal: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier  = modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(0.dp),
        colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                "Weekly Water Intake",
                style      = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = NunitoFontFamily
            )
            Text(
                "Last 7 days",
                style      = MaterialTheme.typography.bodySmall,
                color      = TextSecondary,
                fontFamily = NunitoFontFamily
            )
            Spacer(Modifier.height(20.dp))

            val maxVal     = maxOf(data.maxOfOrNull { it.second } ?: 0, goal, 1)
            val todayIndex = data.size - 1

            Row(
                modifier              = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment     = Alignment.Bottom
            ) {
                data.forEachIndexed { index, (label, value) ->
                    val ratio   = value.toFloat() / maxVal
                    val isToday = index == todayIndex
                    val metGoal = value >= goal

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom,
                        modifier            = Modifier
                            .weight(1f)
                            .fillMaxSize()
                    ) {
                        if (value > 0) {
                            val displayText = if (value >= 1000) {
                                "${"%.1f".format(value / 1000f)}L"
                            } else {
                                "${value}ml"
                            }
                            Text(
                                displayText,
                                style      = MaterialTheme.typography.labelSmall,
                                color      = if (isToday) Teal500 else TextSecondary,
                                fontSize   = 9.sp,
                                fontFamily = NunitoFontFamily
                            )
                        }
                        Spacer(Modifier.height(2.dp))
                        Box(
                            modifier = Modifier
                                .weight(maxOf(ratio, 0.05f))
                                .fillMaxWidth(0.55f)
                                .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                                .background(
                                    when {
                                        metGoal -> Teal500
                                        isToday -> Teal500.copy(alpha = 0.65f)
                                        else    -> Teal100
                                    }
                                )
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            label,
                            style      = MaterialTheme.typography.labelSmall,
                            color      = if (isToday) Teal500 else TextSecondary,
                            fontWeight = if (isToday) FontWeight.ExtraBold else FontWeight.Normal,
                            fontSize   = 10.sp,
                            textAlign  = TextAlign.Center,
                            fontFamily = NunitoFontFamily
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Teal500)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    "Goal met",
                    style      = MaterialTheme.typography.labelSmall,
                    color      = TextSecondary,
                    fontFamily = NunitoFontFamily
                )
                Spacer(Modifier.width(16.dp))
                Box(
                    Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Teal100)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    "Below goal",
                    style      = MaterialTheme.typography.labelSmall,
                    color      = TextSecondary,
                    fontFamily = NunitoFontFamily
                )
            }
        }
    }
}
