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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vitaremind.app.data.local.entity.WaterLog
import com.vitaremind.app.ui.theme.Teal100
import com.vitaremind.app.ui.theme.Teal500
import com.vitaremind.app.ui.water.components.AddWaterDialog
import com.vitaremind.app.ui.water.components.WaterCircularProgress
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val quickAddAmounts = listOf(150, 200, 250, 500)
private val timeFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WaterScreen(
    viewModel: WaterViewModel = hiltViewModel()
) {
    val todayLogs by viewModel.todayLogs.collectAsStateWithLifecycle()
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
                title = { Text("Water Tracker") },
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
                contentColor   = Color.White
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add custom amount")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier        = Modifier.fillMaxSize().padding(innerPadding),
            contentPadding  = PaddingValues(bottom = 88.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            // Circular progress
            item {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    WaterCircularProgress(consumed = totalToday, goal = dailyGoal)
                }
            }

            // Quick-add chips
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    quickAddAmounts.forEach { amount ->
                        FilterChip(
                            selected = false,
                            onClick  = { viewModel.addWater(amount) },
                            label    = { Text("+${amount}ml") },
                            colors   = FilterChipDefaults.filterChipColors(
                                containerColor = MaterialTheme.colorScheme.surface,
                                labelColor     = Teal500
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled  = true,
                                selected = false,
                                borderColor = Teal500
                            )
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Section header
            item {
                Text(
                    text     = "Today's Log",
                    style    = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            // Empty state
            if (todayLogs.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Filled.WaterDrop,
                            contentDescription = null,
                            tint     = Teal100,
                            modifier = Modifier.size(72.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            "No entries yet",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.Gray,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Tap + to log your first glass of water",
                            color = Color.LightGray,
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            } else {
                items(items = todayLogs, key = { it.id }) { log ->
                    WaterLogItem(log = log, onDelete = { viewModel.deleteLog(log.id) })
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WaterLogItem(log: WaterLog, onDelete: () -> Unit) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) { onDelete(); true }
            else false
        }
    )

    val bgColor by animateColorAsState(
        targetValue = if (dismissState.dismissDirection == SwipeToDismissBoxValue.EndToStart)
            Color(0xFFFFEBEB) else MaterialTheme.colorScheme.surface,
        label = "swipe_bg"
    )

    SwipeToDismissBox(
        state        = dismissState,
        enableDismissFromStartToEnd = false,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(bgColor)
                    .padding(end = 20.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(Icons.Filled.Delete, contentDescription = "Delete", tint = Color.Red)
            }
        }
    ) {
        ListItem(
            headlineContent   = { Text("${log.amountMl} ml", style = MaterialTheme.typography.bodyLarge) },
            supportingContent = {
                Text(
                    timeFormatter.format(Date(log.timestamp)),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            },
            leadingContent = {
                Icon(Icons.Filled.WaterDrop, contentDescription = null, tint = Teal500)
            }
        )
    }
}
