package com.vitaremind.app.ui.medicine

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vitaremind.app.data.local.entity.DoseLog
import com.vitaremind.app.data.local.entity.DoseStatus
import com.vitaremind.app.data.local.entity.Medicine
import com.vitaremind.app.ui.theme.ChipTaken
import com.vitaremind.app.ui.theme.NunitoFontFamily
import com.vitaremind.app.ui.theme.Purple400
import com.vitaremind.app.ui.theme.Purple50
import com.vitaremind.app.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

@Composable
fun TodayDosesCard(
    viewModel: MedicineViewModel = hiltViewModel()
) {
    val doses     by viewModel.todayDoses.collectAsStateWithLifecycle()
    val medicines by viewModel.medicines.collectAsStateWithLifecycle()

    if (doses.isEmpty()) return

    Column(
        modifier            = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier          = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(4.dp, 20.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Purple400)
            )
            Text(
                "  Today's Doses",
                style      = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = NunitoFontFamily,
                color      = MaterialTheme.colorScheme.onBackground,
                modifier   = Modifier.padding(bottom = 2.dp)
            )
        }

        doses.take(5).forEach { dose ->
            val medicine = medicines.find { it.id == dose.medicineId }
            if (medicine != null) {
                DoseItem(
                    dose        = dose,
                    medicine    = medicine,
                    onMarkTaken = { viewModel.markDoseTaken(dose) },
                    onSkip      = { viewModel.skipDose(dose) }
                )
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
    val cardBg = when (dose.status) {
        DoseStatus.TAKEN   -> ChipTaken.copy(alpha = 0.08f)
        DoseStatus.SKIPPED -> Color(0xFFFFF8E1)
        else               -> MaterialTheme.colorScheme.surface
    }

    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(0.dp),
        colors    = CardDefaults.cardColors(containerColor = cardBg)
    ) {
        Row(
            modifier          = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape    = CircleShape,
                color    = Color(medicine.color).copy(alpha = 0.15f),
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Filled.Medication,
                        null,
                        tint     = Color(medicine.color),
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    medicine.name,
                    style      = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    fontFamily = NunitoFontFamily
                )
                Text(
                    "${medicine.dosage}  \u00B7  ${timeFormat.format(Date(dose.scheduledTime))}",
                    style      = MaterialTheme.typography.bodySmall,
                    color      = TextSecondary,
                    fontFamily = NunitoFontFamily
                )
            }
            when (dose.status) {
                DoseStatus.TAKEN -> {
                    Icon(
                        Icons.Filled.CheckCircle,
                        null,
                        tint = ChipTaken,
                        modifier = Modifier.size(24.dp)
                    )
                }
                DoseStatus.PENDING -> {
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        TextButton(
                            onClick          = onSkip,
                            contentPadding   = PaddingValues(horizontal = 8.dp)
                        ) {
                            Text(
                                "Skip",
                                color      = TextSecondary,
                                fontFamily = NunitoFontFamily,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Button(
                            onClick        = onMarkTaken,
                            colors         = ButtonDefaults.buttonColors(containerColor = Purple400),
                            contentPadding = PaddingValues(horizontal = 14.dp),
                            shape          = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                "Take",
                                color      = Color.White,
                                fontFamily = NunitoFontFamily,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                else -> {
                    Text(
                        "Skipped",
                        color      = Color(0xFFFF9800),
                        style      = MaterialTheme.typography.labelSmall,
                        fontFamily = NunitoFontFamily,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
