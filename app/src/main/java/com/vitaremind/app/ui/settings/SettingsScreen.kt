package com.vitaremind.app.ui.settings

import android.app.TimePickerDialog
import android.os.Build
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vitaremind.app.ui.theme.Teal500

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: SettingsViewModel = hiltViewModel()) {
    val dailyGoal           by viewModel.dailyGoalMl.collectAsStateWithLifecycle()
    val intervalH           by viewModel.waterReminderIntervalH.collectAsStateWithLifecycle()
    val startHour           by viewModel.waterReminderStartHour.collectAsStateWithLifecycle()
    val endHour             by viewModel.waterReminderEndHour.collectAsStateWithLifecycle()
    val snoozeMinutes       by viewModel.medicineSnoozeMinutes.collectAsStateWithLifecycle()
    val soundEnabled        by viewModel.medicineSoundEnabled.collectAsStateWithLifecycle()
    val themePreference     by viewModel.themePreference.collectAsStateWithLifecycle()

    var showResetDialog     by remember { mutableStateOf(false) }
    val context             = LocalContext.current

    // ── Reset confirmation dialog ──────────────────────────────────────────────
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Reset All Data?") },
            text  = { Text("This will delete all water logs, medicines, and preferences. This cannot be undone.") },
            confirmButton = {
                TextButton(onClick = { viewModel.resetAllData(); showResetDialog = false }) {
                    Text("Reset", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) { Text("Cancel") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor    = Teal500,
                    titleContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {

            // ──────────────────────────────────────────────────────────────────
            // A) WATER SETTINGS
            // ──────────────────────────────────────────────────────────────────
            SectionHeader("Water Settings")

            // Daily goal slider
            ListItem(
                headlineContent   = { Text("Daily Goal") },
                supportingContent = {
                    Column {
                        Text(
                            "${dailyGoal} ml",
                            style      = MaterialTheme.typography.bodySmall,
                            color      = Teal500,
                            fontWeight = FontWeight.Bold
                        )
                        Slider(
                            value         = dailyGoal.toFloat(),
                            onValueChange = { viewModel.setDailyGoal(it.toInt()) },
                            valueRange    = 500f..4000f,
                            steps         = 13, // (4000-500)/250 - 1 = 13
                            colors        = SliderDefaults.colors(
                                thumbColor       = Teal500,
                                activeTrackColor = Teal500
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Text(
                            "500 ml                              4000 ml",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray
                        )
                    }
                }
            )

            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

            // Reminder interval dropdown
            val intervalOptions = listOf(0 to "Off", 1 to "1 hour", 2 to "2 hours", 3 to "3 hours", 4 to "4 hours")
            DropdownListItem(
                label    = "Reminder Interval",
                selected = intervalOptions.find { it.first == intervalH }?.second ?: "2 hours",
                options  = intervalOptions.map { it.second },
                onSelect = { label ->
                    val hours = intervalOptions.find { it.second == label }?.first ?: 2
                    viewModel.setWaterReminderInterval(hours)
                }
            )

            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

            // Active hours — start time
            ListItem(
                headlineContent   = { Text("Reminder Start Time") },
                supportingContent = { Text("Currently: %02d:00".format(startHour), color = Color.Gray) },
                trailingContent   = {
                    TextButton(onClick = {
                        TimePickerDialog(context, { _, h, _ ->
                            viewModel.setWaterReminderStartHour(h)
                        }, startHour, 0, true).show()
                    }) { Text("%02d:00".format(startHour), color = Teal500) }
                }
            )

            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

            // Active hours — end time
            ListItem(
                headlineContent   = { Text("Reminder End Time") },
                supportingContent = { Text("Currently: %02d:00".format(endHour), color = Color.Gray) },
                trailingContent   = {
                    TextButton(onClick = {
                        TimePickerDialog(context, { _, h, _ ->
                            viewModel.setWaterReminderEndHour(h)
                        }, endHour, 0, true).show()
                    }) { Text("%02d:00".format(endHour), color = Teal500) }
                }
            )

            // ──────────────────────────────────────────────────────────────────
            // B) MEDICINE SETTINGS
            // ──────────────────────────────────────────────────────────────────
            SectionHeader("Medicine Settings")

            // Snooze duration
            val snoozeOptions = listOf(5, 10, 15, 30)
            DropdownListItem(
                label    = "Default Snooze Duration",
                selected = "$snoozeMinutes min",
                options  = snoozeOptions.map { "$it min" },
                onSelect = { label ->
                    val mins = label.removeSuffix(" min").toIntOrNull() ?: 10
                    viewModel.setMedicineSnoozeMinutes(mins)
                }
            )

            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

            // Notification sound toggle
            ListItem(
                headlineContent   = { Text("Notification Sound") },
                supportingContent = { Text(if (soundEnabled) "Enabled" else "Disabled", color = Color.Gray) },
                trailingContent   = {
                    Switch(
                        checked         = soundEnabled,
                        onCheckedChange = { viewModel.setMedicineSoundEnabled(it) },
                        colors          = SwitchDefaults.colors(
                            checkedThumbColor = Teal500,
                            checkedTrackColor = Teal500.copy(alpha = 0.5f)
                        )
                    )
                }
            )

            // Exact alarm permission banner (Android 12+ only)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val alarmManager = LocalContext.current
                    .getSystemService(android.app.AlarmManager::class.java)
                val canSchedule  = alarmManager?.canScheduleExactAlarms() ?: true
                if (!canSchedule) {
                    val ctx = LocalContext.current
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                    ListItem(
                        headlineContent   = {
                            Text("Exact Alarm Permission", color = Color(0xFFBA7517))
                        },
                        supportingContent = {
                            Text(
                                "Medicine reminders need this to fire on time.",
                                color = Color.Gray
                            )
                        },
                        trailingContent   = {
                            TextButton(onClick = {
                                ctx.startActivity(
                                    android.content.Intent(
                                        android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
                                    ).apply {
                                        data = android.net.Uri.parse("package:${ctx.packageName}")
                                    }
                                )
                            }) { Text("Allow", color = Color(0xFFBA7517)) }
                        }
                    )
                }
            }

            // ──────────────────────────────────────────────────────────────────
            // C) GENERAL
            // ──────────────────────────────────────────────────────────────────
            SectionHeader("General")

            // Theme selector
            DropdownListItem(
                label    = "App Theme",
                selected = themePreference.replaceFirstChar { it.uppercase() },
                options  = listOf("System", "Light", "Dark"),
                onSelect = { label -> viewModel.setThemePreference(label.lowercase()) }
            )

            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

            // Reset all data
            ListItem(
                headlineContent   = { Text("Reset All Data", color = Color.Red) },
                supportingContent = { Text("Delete all logs, medicines and preferences", color = Color.Gray) },
                trailingContent   = {
                    TextButton(onClick = { showResetDialog = true }) {
                        Text("Reset", color = Color.Red)
                    }
                }
            )

            Spacer(Modifier.height(32.dp))

            // App version
            Text(
                text     = "VitaRemind v1.0.0",
                style    = MaterialTheme.typography.labelSmall,
                color    = Color.Gray,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text       = title,
        style      = MaterialTheme.typography.labelLarge,
        color      = Teal500,
        fontWeight = FontWeight.Bold,
        modifier   = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DropdownListItem(
    label: String,
    selected: String,
    options: List<String>,
    onSelect: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ListItem(
        headlineContent   = { Text(label) },
        trailingContent   = {
            ExposedDropdownMenuBox(
                expanded         = expanded,
                onExpandedChange = { expanded = it }
            ) {
                OutlinedTextField(
                    value         = selected,
                    onValueChange = {},
                    readOnly      = true,
                    trailingIcon  = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier      = Modifier.menuAnchor(androidx.compose.material3.MenuAnchorType.PrimaryNotEditable),
                    textStyle     = MaterialTheme.typography.bodySmall
                )
                ExposedDropdownMenu(
                    expanded         = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    options.forEach { option ->
                        DropdownMenuItem(
                            text    = { Text(option) },
                            onClick = { onSelect(option); expanded = false }
                        )
                    }
                }
            }
        }
    )
}
