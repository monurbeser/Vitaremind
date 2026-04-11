package com.vitaremind.app.ui.medicine

import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vitaremind.app.data.local.entity.Medicine
import com.vitaremind.app.ui.theme.Purple400
import com.vitaremind.app.ui.theme.Teal500

private val presetColors = listOf(
    Color(0xFF1D9E75), // Teal
    Color(0xFF7F77DD), // Purple
    Color(0xFF2196F3), // Blue
    Color(0xFFF44336), // Red
    Color(0xFFFF9800), // Orange
    Color(0xFFE91E63), // Pink
    Color(0xFF9C27B0), // Deep Purple
    Color(0xFF607D8B), // Blue Grey
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddMedicineScreen(
    onNavigateBack: () -> Unit,
    viewModel: MedicineViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var name by remember { mutableStateOf("") }
    var dosage by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf(presetColors[0]) }
    var reminderTimes by remember { mutableStateOf<List<String>>(emptyList()) }

    val canSave = name.isNotBlank() && dosage.isNotBlank()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Medicine") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Purple400,
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Name
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Medicine name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Dosage
            OutlinedTextField(
                value = dosage,
                onValueChange = { dosage = it },
                label = { Text("Dosage (e.g. 500mg, 1 tablet)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Color picker
            Text("Color", style = MaterialTheme.typography.labelLarge)
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                presetColors.forEach { color ->
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(color)
                            .then(
                                if (color == selectedColor)
                                    Modifier.border(3.dp, Color.Black, CircleShape)
                                else Modifier
                            )
                            .clickable { selectedColor = color }
                    )
                }
            }

            // Reminder times
            Text("Reminder Times", style = MaterialTheme.typography.labelLarge)

            TextButton(
                onClick = {
                    val cal = java.util.Calendar.getInstance()
                    TimePickerDialog(
                        context,
                        { _, hour, minute ->
                            val time = "%02d:%02d".format(hour, minute)
                            if (time !in reminderTimes) {
                                reminderTimes = reminderTimes + time
                            }
                        },
                        cal.get(java.util.Calendar.HOUR_OF_DAY),
                        cal.get(java.util.Calendar.MINUTE),
                        true
                    ).show()
                }
            ) {
                Icon(Icons.Filled.AccessTime, null)
                Text("  Add reminder time")
            }

            if (reminderTimes.isNotEmpty()) {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    reminderTimes.forEach { time ->
                        InputChip(
                            selected = false,
                            onClick = {},
                            label = { Text(time) },
                            trailingIcon = {
                                IconButton(
                                    onClick = { reminderTimes = reminderTimes - time },
                                    modifier = Modifier.size(18.dp)
                                ) {
                                    Icon(Icons.Filled.Close, "Remove", modifier = Modifier.size(14.dp))
                                }
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = {
                    val timesJson = "[${reminderTimes.joinToString(",") { "\"$it\"" }}]"
                    viewModel.addMedicine(
                        Medicine(
                            name = name.trim(),
                            dosage = dosage.trim(),
                            color = selectedColor.hashCode(),
                            reminderTimes = timesJson
                        )
                    )
                    onNavigateBack()
                },
                enabled = canSave,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Purple400)
            ) {
                Text("Save Medicine", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}
