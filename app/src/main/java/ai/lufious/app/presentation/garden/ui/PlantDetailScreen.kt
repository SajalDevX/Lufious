package ai.lufious.app.presentation.garden.ui

import ai.lufious.app.core.theme.Background
import ai.lufious.app.core.theme.CriticalRed
import ai.lufious.app.core.theme.HealthyGreen
import ai.lufious.app.core.theme.PrimaryColor
import ai.lufious.app.core.theme.WarningOrange
import ai.lufious.app.core.utils.BACK_BUTTON_HEIGHT_FRACTION
import ai.lufious.app.core.utils.UiEffect
import ai.lufious.app.core.utils.hR
import ai.lufious.app.core.utils.rememberResponsiveDimensions
import ai.lufious.app.core.utils.wR
import ai.lufious.app.presentation.garden.data.models.CareLogModel
import ai.lufious.app.presentation.garden.viewmodel.PlantDetailEvent
import ai.lufious.app.presentation.garden.viewmodel.PlantDetailViewModel
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import ai.lufious.app.core.theme.TextPrimary
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import kotlinx.coroutines.flow.collectLatest
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val logTypes = listOf("watered", "fertilized", "repotted", "note")

@Composable
fun PlantDetailScreen(
    navController: NavController,
    viewModel: PlantDetailViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val dimensions = rememberResponsiveDimensions()

    LaunchedEffect(Unit) {
        viewModel.effects.collectLatest { effect ->
            when (effect) {
                is UiEffect.ShowError ->
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                else -> {}
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Background,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = dimensions.heightFraction(BACK_BUTTON_HEIGHT_FRACTION).dp)
                    .padding(horizontal = dimensions.wR(8f).dp, vertical = dimensions.hR(8f).dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = state.plant?.nickname ?: "Plant Detail",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("plant_detail_screen")
        ) {
        when {
            state.isLoading && state.plant == null -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = PrimaryColor)
            }

            else -> LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                state.plant?.let { plant ->
                    item {
                        // Plant info card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colors.onBackground
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                val healthColor = when (plant.healthStatus) {
                                    "warning" -> WarningOrange
                                    "critical" -> CriticalRed
                                    else -> HealthyGreen
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(48.dp)
                                            .background(
                                                color = HealthyGreen.copy(alpha = 0.15f),
                                                shape = RoundedCornerShape(12.dp)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = plant.nickname.firstOrNull()
                                                ?.uppercaseChar()?.toString() ?: "🌱",
                                            fontSize = 22.sp,
                                            color = TextPrimary
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = plant.nickname,
                                            color = TextPrimary,
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = plant.species,
                                            color = TextPrimary.copy(alpha = 0.6f),
                                            fontSize = 13.sp
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    InfoChip(label = "📍 ${plant.locationTag}")
                                    InfoChip(label = "💧 Every ${plant.wateringIntervalDays}d")
                                    InfoChip(label = healthColor.toStatusLabel())
                                }
                            }
                        }
                    }

                    item {
                        // Log care action button
                        Button(
                            onClick = { viewModel.onEvent(PlantDetailEvent.ShowLogDialog) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor)
                        ) {
                            Text(
                                text = "LOG CARE ACTION",
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    if (state.careLogs.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No care actions logged yet",
                                    color = TextPrimary.copy(alpha = 0.4f),
                                    fontSize = 13.sp
                                )
                            }
                        }
                    } else {
                        item {
                            Text(
                                text = "Care Log",
                                color = TextPrimary.copy(alpha = 0.7f),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        items(state.careLogs, key = { it.id }) { log ->
                            CareLogItem(log = log)
                        }
                    }
                }
            }
        }
        }
    }

    // Care log dialog
    if (state.showLogDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.onEvent(PlantDetailEvent.DismissLogDialog) },
            title = {
                Text(
                    text = "Log Care Action",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        text = "Type",
                        color = TextPrimary.copy(alpha = 0.7f),
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        logTypes.forEach { type ->
                            val selected = state.selectedLogType == type
                            Box(
                                modifier = Modifier
                                    .background(
                                        color = if (selected) PrimaryColor
                                        else TextPrimary.copy(alpha = 0.15f),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .clickable {
                                        viewModel.onEvent(PlantDetailEvent.LogTypeSelected(type))
                                    }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = type.replaceFirstChar { it.uppercase() },
                                    color = TextPrimary,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = state.logNote,
                        onValueChange = { viewModel.onEvent(PlantDetailEvent.NoteChanged(it)) },
                        placeholder = {
                            Text(
                                "Note (optional)",
                                color = TextPrimary.copy(alpha = 0.4f)
                            )
                        },
                        colors = TextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedContainerColor = TextPrimary.copy(alpha = 0.1f),
                            unfocusedContainerColor = TextPrimary.copy(alpha = 0.1f),
                            focusedIndicatorColor = PrimaryColor,
                            unfocusedIndicatorColor = TextPrimary.copy(alpha = 0.3f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.onEvent(PlantDetailEvent.SubmitLog) },
                    enabled = !state.isLoading
                ) {
                    Text("LOG", color = PrimaryColor, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { viewModel.onEvent(PlantDetailEvent.DismissLogDialog) }
                ) {
                    Text("CANCEL", color = TextPrimary.copy(alpha = 0.6f))
                }
            },
            containerColor = Color(0xFF1E2020)
        )
    }
}

@Composable
private fun InfoChip(label: String) {
    Box(
        modifier = Modifier
            .background(
                color = TextPrimary.copy(alpha = 0.12f),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(text = label, color = TextPrimary.copy(alpha = 0.8f), fontSize = 11.sp)
    }
}

@Composable
private fun CareLogItem(log: CareLogModel) {
    val dateStr = SimpleDateFormat("MMM d, h:mm a", Locale.getDefault())
        .format(Date(log.timestamp))
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = TextPrimary.copy(alpha = 0.06f),
                shape = RoundedCornerShape(10.dp)
            )
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = when (log.type) {
                "watered" -> Icons.Default.WaterDrop
                else -> Icons.Default.LocalFlorist
            },
            contentDescription = null,
            tint = PrimaryColor,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(
                text = log.type.replaceFirstChar { it.uppercase() },
                color = TextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
            if (log.note.isNotBlank()) {
                Text(
                    text = log.note,
                    color = TextPrimary.copy(alpha = 0.6f),
                    fontSize = 11.sp
                )
            }
            Text(
                text = dateStr,
                color = TextPrimary.copy(alpha = 0.4f),
                fontSize = 10.sp
            )
        }
    }
}

private fun Color.toStatusLabel(): String = when (this) {
    WarningOrange -> "⚠ Warning"
    CriticalRed -> "🔴 Critical"
    else -> "✅ Healthy"
}
