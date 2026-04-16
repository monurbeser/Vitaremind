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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vitaremind.app.data.local.entity.DoseLog
import com.vitaremind.app.data.local.entity.DoseStatus
import com.vitaremind.app.data.local.entity.Medicine
import com.vitaremind.app.ui.theme.ChipPending
import com.vitaremind.app.ui.theme.ChipSkipped
import com.vitaremind.app.ui.theme.ChipTaken
import com.vitaremind.app.ui.theme.NunitoFontFamily
import com.vitaremind.app.ui.theme.Purple400
import com.vitaremind.app.ui.theme.Purple50
import com.vitaremind.app.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicineScreen(
    onNavigateToAdd: () -> Unit = {},
    viewModel: MedicineViewModel = hiltViewModel()
) {
    val medicines  by viewModel.medicines.collectAsStateWithLifecycle()
    val todayDoses by viewModel.todayDoses.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "My Medicines",
                        fontFamily = NunitoFontFamily,
                        fontWeight = FontWeight.ExtraBold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor    = Purple400,
                    titleContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick        = onNavigateToAdd,
                containerColor = Purple400,
                contentColor   = Color.White,
                shape          = CircleShape
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add medicine")
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        if (medicines.isEmpty()) {
            Box(
                modifier         = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Surface(
                        shape    = CircleShape,
                        color    = Purple50,
                        modifier = Modifier.size(88.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Filled.Medication,
                                null,
                                tint     = Purple400.copy(alpha = 0.4f),
                                modifier = Modifier.size(44.dp)
                            )
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    Text(
                        "No medicines yet",
                        style      = MaterialTheme.typography.titleMedium,
                        color      = TextSecondary,
                        fontWeight = FontWeight.Bold,
                        fontFamily = NunitoFontFamily
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Tap + to add your first medication",
                        color      = TextSecondary.copy(alpha = 0.7f),
                        style      = MaterialTheme.typography.bodySmall,
                        fontFamily = NunitoFontFamily,
                        textAlign  = TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                modifier            = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item { Spacer(Modifier.height(8.dp)) }
                items(medicines, key = { it.id }) { medicine ->
                    val dosesToday = todayDoses.filter { it.medicineId == medicine.id }
                    MedicineCard(
                        modifier   = Modifier.animateItem(),
                        medicine   = medicine,
                        dosesToday = dosesToday,
                        onDelete   = { viewModel.deleteMedicine(medicine) }
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
            title = { Text("Delete ${medicine.name}?", fontFamily = NunitoFontFamily) },
            text  = { Text("This will remove the medicine and all its reminders.", fontFamily = NunitoFontFamily) },
            confirmButton = {
                TextButton(onClick = { onDelete(); showDeleteDialog = false }) {
                    Text("Delete", color = Color.Red, fontFamily = NunitoFontFamily)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel", fontFamily = NunitoFontFamily)
                }
            }
        )
    }

    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) {
                showDeleteDialog = true
                false
            } else false
        }
    )

    SwipeToDismissBox(
        state                      = dismissState,
        enableDismissFromStartToEnd = false,
        backgroundContent          = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFFFFEBEB))
                    .padding(end = 20.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(Icons.Filled.Delete, "Delete", tint = Color(0xFFE53935))
            }
        }
    ) {
        Card(
            modifier  = modifier.fillMaxWidth(),
            shape     = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(0.dp),
            colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Row(
                modifier          = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Prominent left color bar
                Box(
                    modifier = Modifier
                        .width(8.dp)
                        .height(80.dp)
                        .clip(RoundedCornerShape(topStart = 20.dp, bottomStart = 20.dp))
                        .background(Color(medicine.color))
                )

                // Medicine icon circle
                Spacer(Modifier.width(12.dp))
                Surface(
                    shape    = CircleShape,
                    color    = Color(medicine.color).copy(alpha = 0.12f),
                    modifier = Modifier.size(44.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Filled.Medication,
                            contentDescription = null,
                            tint     = Color(medicine.color),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                Spacer(Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f).padding(vertical = 16.dp)) {
                    Text(
                        medicine.name,
                        style      = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = NunitoFontFamily
                    )
                    Text(
                        medicine.dosage,
                        style      = MaterialTheme.typography.bodySmall,
                        color      = TextSecondary,
                        fontFamily = NunitoFontFamily
                    )
                }

                // Status chip
                val status = when {
                    dosesToday.any { it.status == DoseStatus.TAKEN }   -> DoseStatus.TAKEN
                    dosesToday.any { it.status == DoseStatus.SKIPPED } -> DoseStatus.SKIPPED
                    dosesToday.isNotEmpty()                            -> DoseStatus.PENDING
                    else                                               -> null
                }
                if (status != null) {
                    val (chipColor, label) = when (status) {
                        DoseStatus.TAKEN   -> ChipTaken   to "Taken"
                        DoseStatus.SKIPPED -> ChipSkipped to "Skipped"
                        else               -> ChipPending to "Pending"
                    }
                    SuggestionChip(
                        onClick  = {},
                        label    = {
                            Text(
                                label,
                                color      = chipColor,
                                fontFamily = NunitoFontFamily,
                                fontWeight = FontWeight.SemiBold
                            )
                        },
                        modifier = Modifier.padding(end = 12.dp),
                        colors   = SuggestionChipDefaults.suggestionChipColors(
                            containerColor = chipColor.copy(alpha = 0.12f)
                        ),
                        border   = SuggestionChipDefaults.suggestionChipBorder(
                            enabled     = true,
                            borderColor = chipColor.copy(alpha = 0.3f)
                        )
                    )
                }
            }
        }
    }
}
