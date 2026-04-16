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
import com.vitaremind.app.ui.theme.Purple400
import com.vitaremind.app.ui.theme.Teal100
import com.vitaremind.app.ui.theme.Teal500
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
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor    = Teal500,
                    titleContentColor = Color.White
                )
            )
        }
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
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "$greeting, stay healthy! \uD83D\uDC4B",
                            style      = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            "Here's your health summary",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
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
                    modifier          = Modifier.padding(horizontal = 16.dp)
                )
            }

            // Today's Medicines card
            item {
                TodayMedicinesCard(
                    doses            = nextDoses,
                    medicines        = medicines,
                    onNavigateToMeds = onNavigateToMedicine,
                    modifier         = Modifier.padding(horizontal = 16.dp)
                )
            }

            // Weekly water chart
            if (weeklyData.isNotEmpty()) {
                item {
                    WeeklyWaterChart(
                        data     = weeklyData,
                        goal     = dailyGoal,
                        modifier = Modifier.padding(horizontal = 16.dp)
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

// Water Summary Card
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
        animationSpec = tween(800),
        label         = "waterProgress"
    )

    Card(
        modifier  = modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(3.dp),
        colors    = CardDefaults.cardColors(containerColor = Teal500)
    ) {
        Row(
            modifier          = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Circular progress indicator
            Box(
                modifier         = Modifier.size(72.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val stroke = Stroke(8.dp.toPx(), cap = StrokeCap.Round)
                    val inset  = stroke.width / 2
                    val arcSize = Size(size.width - stroke.width, size.height - stroke.width)

                    drawArc(
                        color      = Color.White.copy(alpha = 0.3f),
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
                Icon(
                    Icons.Filled.WaterDrop,
                    contentDescription = null,
                    tint     = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Water Today",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White.copy(alpha = 0.8f)
                )
                Text(
                    "$consumed ml",
                    style      = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color      = Color.White
                )
                Text(
                    "of $goal ml goal",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.8f)
                )
                if (isComplete) {
                    Text(
                        "Goal reached! \uD83C\uDF89",
                        style      = MaterialTheme.typography.labelSmall,
                        color      = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Button(
                onClick = onNavigateToWater,
                colors  = ButtonDefaults.buttonColors(
                    containerColor = Color.White.copy(alpha = 0.25f),
                    contentColor   = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("+ Add", fontWeight = FontWeight.Bold)
            }
        }
    }
}

// Today's Medicines Card
@Composable
private fun TodayMedicinesCard(
    doses: List<DoseLog>,
    medicines: List<Medicine>,
    onNavigateToMeds: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier  = modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(3.dp),
        colors    = CardDefaults.cardColors(containerColor = Purple400)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Text(
                    "Today's Medicines",
                    style      = MaterialTheme.typography.titleMedium,
                    color      = Color.White,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = onNavigateToMeds) {
                    Text("View All", color = Color.White.copy(alpha = 0.8f))
                }
            }

            if (doses.isEmpty()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier          = Modifier.padding(vertical = 8.dp)
                ) {
                    Icon(
                        Icons.Filled.CheckCircle,
                        contentDescription = null,
                        tint     = Color.White.copy(alpha = 0.7f),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "All done for today! \u2728",
                        color = Color.White.copy(alpha = 0.9f),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            } else {
                doses.forEach { dose ->
                    val medicine = medicines.find { it.id == dose.medicineId }
                    if (medicine != null) {
                        Row(
                            modifier          = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(Color(medicine.color))
                            )
                            Spacer(Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    medicine.name,
                                    color      = Color.White,
                                    style      = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    "${medicine.dosage}  \u00B7  ${timeFormat.format(Date(dose.scheduledTime))}",
                                    color = Color.White.copy(alpha = 0.7f),
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// Weekly Water Bar Chart
@Composable
private fun WeeklyWaterChart(
    data: List<Pair<String, Int>>,
    goal: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier  = modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Weekly Water Intake",
                style      = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                "Last 7 days",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
            Spacer(Modifier.height(16.dp))

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
                                style    = MaterialTheme.typography.labelSmall,
                                color    = if (isToday) Teal500 else Color.Gray,
                                fontSize = 9.sp
                            )
                        }
                        Spacer(Modifier.height(2.dp))
                        Box(
                            modifier = Modifier
                                .weight(maxOf(ratio, 0.05f))
                                .fillMaxWidth(0.6f)
                                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                .background(
                                    when {
                                        metGoal -> Teal500
                                        isToday -> Teal500.copy(alpha = 0.7f)
                                        else    -> Teal100
                                    }
                                )
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            label,
                            style      = MaterialTheme.typography.labelSmall,
                            color      = if (isToday) Teal500 else Color.Gray,
                            fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                            fontSize   = 10.sp,
                            textAlign  = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Teal500)
                )
                Spacer(Modifier.width(4.dp))
                Text("Goal met", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                Spacer(Modifier.width(12.dp))
                Box(
                    Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Teal100)
                )
                Spacer(Modifier.width(4.dp))
                Text("Below goal", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            }
        }
    }
}
