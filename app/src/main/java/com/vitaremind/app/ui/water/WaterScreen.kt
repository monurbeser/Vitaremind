package com.vitaremind.app.ui.water

import androidx.compose.animation.animateColorAsState
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vitaremind.app.data.local.entity.WaterLog
import com.vitaremind.app.ui.theme.NunitoFontFamily
import com.vitaremind.app.ui.theme.Teal100
import com.vitaremind.app.ui.theme.Teal50
import com.vitaremind.app.ui.theme.Teal500
import com.vitaremind.app.ui.theme.TextSecondary
import com.vitaremind.app.ui.water.components.AddWaterDialog
import com.vitaremind.app.ui.water.components.WaterCircularProgress
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val quickAddAmounts = listOf(150, 200, 250, 500)
private val timeFormatter   = SimpleDateFormat("HH:mm", Locale.getDefault())

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WaterScreen(
    viewModel: WaterViewModel = hiltViewModel()
) {
    val todayLogs  by viewModel.todayLogs.collectAsStateWithLifecycle()
    val totalToday by viewModel.totalToday.collectAsStateWithLifecycle()
    val dailyGoal  by viewModel.dailyGoal.collectAsStateWithLifecycle()
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        AddWaterDialog(
            onConfirm = { amount ->
                viewModel.addWater(amount)
                showDialog = false
            },
            onDismiss = { showDialog = false }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Water Tracker",
                        fontFamily = NunitoFontFamily,
                        fontWeight = FontWeight.ExtraBold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor    = Teal500,
                    titleContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick        = { showDialog = true },
                containerColor = Teal500,
                contentColor   = Color.White,
                shape          = CircleShape
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add custom amount")
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        LazyColumn(
            modifier            = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding      = PaddingValues(bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            // Circular progress
            item {
                Column(
                    modifier            = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    WaterCircularProgress(consumed = totalToday, goal = dailyGoal)
                }
            }

            // Quick-add chips
            item {
                Row(
                    modifier              = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    quickAddAmounts.forEach { amount ->
                        QuickAddChip(
                            amount    = amount,
                            onClick   = { viewModel.addWater(amount) },
                            modifier  = Modifier.weight(1f)
                        )
                    }
                }
                Spacer(Modifier.height(20.dp))
            }

            // Section header
            item {
                Row(
                    modifier          = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(4.dp, 20.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(Teal500)
                    )
                    Text(
                        text       = "  Today's Log",
                        style      = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = NunitoFontFamily,
                        color      = MaterialTheme.colorScheme.onBackground
                    )
                }
                Spacer(Modifier.height(8.dp))
            }

            // Empty state
            if (todayLogs.isEmpty()) {
                item {
                    Column(
                        modifier            = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Teal50,
                            modifier = Modifier.size(80.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Filled.WaterDrop,
                                    contentDescription = null,
                                    tint     = Teal100,
                                    modifier = Modifier.size(40.dp)
                                )
                            }
                        }
                        Spacer(Modifier.height(16.dp))
                        Text(
                            "No entries yet",
                            style      = MaterialTheme.typography.titleMedium,
                            color      = TextSecondary,
                            fontWeight = FontWeight.Bold,
                            fontFamily = NunitoFontFamily
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "Tap + or a quick-add chip to log water",
                            color      = TextSecondary.copy(alpha = 0.7f),
                            style      = MaterialTheme.typography.bodySmall,
                            fontFamily = NunitoFontFamily,
                            textAlign  = TextAlign.Center
                        )
                    }
                }
            } else {
                items(items = todayLogs, key = { it.id }) { log ->
                    WaterLogItem(
                        log      = log,
                        onDelete = { viewModel.deleteLog(log.id) },
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

// ── Quick-add chip ─────────────────────────────────────────────────────────────
@Composable
private fun QuickAddChip(
    amount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TextButton(
        onClick   = onClick,
        shape     = RoundedCornerShape(14.dp),
        modifier  = modifier
            .background(Teal50, RoundedCornerShape(14.dp))
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                Icons.Filled.WaterDrop,
                contentDescription = null,
                tint     = Teal500,
                modifier = Modifier.size(18.dp)
            )
            Text(
                "+${amount}ml",
                style      = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                fontFamily = NunitoFontFamily,
                color      = Teal500
            )
        }
    }
}

// ── Water log item ─────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WaterLogItem(
    log: WaterLog,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) { onDelete(); true }
            else false
        }
    )

    val bgColor by animateColorAsState(
        targetValue = if (dismissState.dismissDirection == SwipeToDismissBoxValue.EndToStart)
            Color(0xFFFFEBEB) else Color.Transparent,
        label = "swipe_bg"
    )

    SwipeToDismissBox(
        state                      = dismissState,
        enableDismissFromStartToEnd = false,
        modifier                   = modifier,
        backgroundContent          = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(16.dp))
                    .background(bgColor),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    Icons.Filled.Delete,
                    contentDescription = "Delete",
                    tint     = Color(0xFFE53935),
                    modifier = Modifier.padding(end = 20.dp)
                )
            }
        }
    ) {
        Card(
            shape     = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(0.dp),
            colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier  = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier          = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = Teal50,
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Filled.WaterDrop,
                            contentDescription = null,
                            tint     = Teal500,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Spacer(Modifier.padding(horizontal = 8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "${log.amountMl} ml",
                        style      = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        fontFamily = NunitoFontFamily
                    )
                    Text(
                        timeFormatter.format(Date(log.timestamp)),
                        style      = MaterialTheme.typography.bodySmall,
                        color      = TextSecondary,
                        fontFamily = NunitoFontFamily
                    )
                }
            }
        }
    }
}
