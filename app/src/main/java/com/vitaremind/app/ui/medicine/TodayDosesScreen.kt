package com.vitaremind.app.ui.medicine

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vitaremind.app.data.local.entity.DoseLog
import com.vitaremind.app.data.local.entity.DoseStatus
import com.vitaremind.app.data.local.entity.Medicine
import com.vitaremind.app.ui.theme.Purple400
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

@Composable
fun TodayDosesCard(
    viewModel: MedicineViewModel = hiltViewModel()
) {
    val doses by viewModel.todayDoses.collectAsStateWithLifecycle()
    val medicines by viewModel.medicines.collectAsStateWithLifecycle()

    if (doses.isEmpty()) return

    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
        Text(
            "Today's Doses",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        doses.take(5).forEach { dose ->
            val medicine = medicines.find { it.id == dose.medicineId }
            if (medicine != null) {
                DoseItem(
                    dose = dose,
                    medicine = medicine,
                    onMarkTaken = { viewModel.markDoseTaken(dose) },
                    onSkip = { viewModel.skipDose(dose) }
                )
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun DoseItem(
    dose: DoseLog,
    medicine: Medicine,
    onMarkTaken: () -> Unit,
    onSkip: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(1.dp),
        colors = CardDefaults.cardColors(
            containerColor = when (dose.status) {
                DoseStatus.TAKEN   -> Color(0xFFE8F5E9)
                DoseStatus.SKIPPED -> Color(0xFFFFF3E0)
                else               -> MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Filled.Medication,
                null,
                tint = Color(medicine.color),
                modifier = Modifier.size(28.dp)
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(medicine.name, style = MaterialTheme.typography.bodyMedium)
                Text(
                    "${medicine.dosage}  ·  ${timeFormat.format(Date(dose.scheduledTime))}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
            when (dose.status) {
                DoseStatus.TAKEN -> Icon(Icons.Filled.CheckCircle, null, tint = Color(0xFF4CAF50))
                DoseStatus.PENDING -> {
                    Row {
                        TextButton(onClick = onSkip) { Text("Skip", color = Color.Gray) }
                        Button(
                            onClick = onMarkTaken,
                            colors = ButtonDefaults.buttonColors(containerColor = Purple400),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp)
                        ) {
                            Text("Take", color = Color.White)
                        }
                    }
                }
                else -> Text("Skipped", color = Color(0xFFFF9800), style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
