package com.vitaremind.app.ui.medicine

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vitaremind.app.data.local.entity.DoseLog
import com.vitaremind.app.data.local.entity.DoseStatus
import com.vitaremind.app.data.local.entity.Medicine
import androidx.compose.ui.text.style.TextAlign
import com.vitaremind.app.ui.theme.Purple400
import com.vitaremind.app.ui.theme.Teal500

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicineScreen(
    onNavigateToAdd: () -> Unit = {},
    viewModel: MedicineViewModel = hiltViewModel()
) {
    val medicines by viewModel.medicines.collectAsStateWithLifecycle()
    val todayDoses by viewModel.todayDoses.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Medicines") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Purple400,
                    titleContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAdd,
                containerColor = Purple400,
                contentColor = Color.White
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add medicine")
            }
        }
    ) { innerPadding ->
        if (medicines.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Filled.Medication, null, tint = Color.LightGray, modifier = Modifier.size(64.dp))
                    Spacer(Modifier.height(12.dp))
                    Text(
                        "No medicines yet",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.Gray,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Medium
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Tap + to add your first medication",
                        color = Color.LightGray,
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(innerPadding).padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item { Spacer(Modifier.height(8.dp)) }
                items(medicines, key = { it.id }) { medicine ->
                    val dosesToday = todayDoses.filter { it.medicineId == medicine.id }
                    MedicineCard(
                        modifier = Modifier.animateItem(),
                        medicine = medicine,
                        dosesToday = dosesToday,
                        onDelete = { viewModel.deleteMedicine(medicine) }
                    )
                }
                item { Spacer(Modifier.height(88.dp)) }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MedicineCard(
    medicine: Medicine,
    modifier: Modifier = Modifier,
    dosesToday: List<DoseLog>,
    onDelete: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete ${medicine.name}?") },
            text = { Text("This will remove the medicine and all its reminders.") },
            confirmButton = {
                TextButton(onClick = { onDelete(); showDeleteDialog = false }) { Text("Delete", color = Color.Red) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel") }
            }
        )
    }

    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) {
                showDeleteDialog = true
                false // don't auto-dismiss; let dialog handle it
            } else false
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = false,
        backgroundContent = {
            Box(
                modifier = Modifier.fillMaxSize().background(Color(0xFFFFEBEB)).padding(end = 20.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(Icons.Filled.Delete, "Delete", tint = Color.Red)
            }
        }
    ) {
        Card(
            modifier = modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(2.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Colored left border
                Box(
                    modifier = Modifier
                        .width(6.dp)
                        .height(72.dp)
                        .background(Color(medicine.color))
                )
                Column(
                    modifier = Modifier.weight(1f).padding(horizontal = 14.dp, vertical = 12.dp)
                ) {
                    Text(medicine.name, style = MaterialTheme.typography.titleMedium)
                    Text(medicine.dosage, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
                // Status chip
                val status = when {
                    dosesToday.any { it.status == DoseStatus.TAKEN } -> DoseStatus.TAKEN
                    dosesToday.any { it.status == DoseStatus.SKIPPED } -> DoseStatus.SKIPPED
                    dosesToday.isNotEmpty() -> DoseStatus.PENDING
                    else -> null
                }
                if (status != null) {
                    val (chipColor, label) = when (status) {
                        DoseStatus.TAKEN   -> Color(0xFF4CAF50) to "Taken"
                        DoseStatus.SKIPPED -> Color(0xFFF44336) to "Skipped"
                        else               -> Color(0xFFFFA726) to "Pending"
                    }
                    SuggestionChip(
                        onClick = {},
                        label = { Text(label, color = chipColor) },
                        modifier = Modifier.padding(end = 12.dp),
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            containerColor = chipColor.copy(alpha = 0.12f)
                        ),
                        border = SuggestionChipDefaults.suggestionChipBorder(
                            enabled = true,
                            borderColor = chipColor.copy(alpha = 0.4f)
                        )
                    )
                }
            }
        }
    }
}
