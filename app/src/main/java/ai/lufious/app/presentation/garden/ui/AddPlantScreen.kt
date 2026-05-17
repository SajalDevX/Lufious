package ai.lufious.app.presentation.garden.ui

import ai.lufious.app.core.theme.TextPrimary
import ai.lufious.app.core.utils.UiEffect
import ai.lufious.app.presentation.catalog.PlantSpecies
import ai.lufious.app.presentation.catalog.plantCatalog
import ai.lufious.app.presentation.garden.viewmodel.AddPlantEvent
import ai.lufious.app.presentation.garden.viewmodel.AddPlantViewModel
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import kotlinx.coroutines.flow.collectLatest

private val PageBg = Color(0xFFF8FBF8)
private val CardWhite = Color.White
private val Brand = Color(0xFF1A5C35)
private val BrandSoft = Color(0xFFE8F5E8)
private val ChipIdle = Color(0xFFEEF2EE)
private val Hint = Color(0xFF7A8C82)

private val locationOptions = listOf("Living Room", "Bedroom", "Balcony", "Office", "Outdoor")
private val waterPresets = listOf(3, 5, 7, 10, 14)

private fun PlantSpecies.suggestedDays(): Int = when (water) {
    PlantSpecies.Water.Frequent -> 3
    PlantSpecies.Water.Weekly -> 7
    PlantSpecies.Water.Sparse -> 14
}

@Composable
fun AddPlantScreen(
    navController: NavController,
    viewModel: AddPlantViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    var selectedSpeciesId by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        viewModel.effects.collectLatest { effect ->
            when (effect) {
                is UiEffect.Navigate -> navController.popBackStack()
                is UiEffect.ShowError ->
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                else -> {}
            }
        }
    }

    val selectedSpecies = remember(selectedSpeciesId) {
        plantCatalog.firstOrNull { it.id == selectedSpeciesId }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PageBg)
            .testTag("add_plant_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .padding(bottom = 96.dp)
        ) {
            // Top bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(CardWhite)
                        .clickable { navController.popBackStack() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(Modifier.width(12.dp))
                Text(
                    text = "Add a plant",
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Spacer(Modifier.height(20.dp))

            // Hero — selected species emoji or generic
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(BrandSoft),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = selectedSpecies?.emoji ?: "🌱",
                        fontSize = 64.sp
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // Section 1: species
            SectionLabel("Pick a species")
            Spacer(Modifier.height(8.dp))
            SpeciesPicker(
                selectedId = selectedSpeciesId,
                onSelect = { species ->
                    selectedSpeciesId = species.id
                    viewModel.onEvent(AddPlantEvent.SpeciesChanged(species.name))
                    viewModel.onEvent(
                        AddPlantEvent.WateringIntervalChanged(species.suggestedDays().toString())
                    )
                }
            )

            Spacer(Modifier.height(20.dp))

            // Section 2: nickname
            SectionLabel("Give it a nickname")
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = state.nickname,
                onValueChange = { viewModel.onEvent(AddPlantEvent.NicknameChanged(it)) },
                placeholder = { Text("e.g. Monty the Monstera", color = Hint) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("nickname_field"),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Brand,
                    unfocusedBorderColor = Color(0xFFDCE5D5),
                    focusedContainerColor = CardWhite,
                    unfocusedContainerColor = CardWhite,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                )
            )

            Spacer(Modifier.height(20.dp))

            // Section 3: location
            SectionLabel("Where will it live?")
            Spacer(Modifier.height(8.dp))
            LocationRow(
                selected = state.locationTag,
                onSelect = { viewModel.onEvent(AddPlantEvent.LocationChanged(it)) }
            )

            Spacer(Modifier.height(20.dp))

            // Section 4: watering
            SectionLabel("Water every")
            Spacer(Modifier.height(8.dp))
            WateringPicker(
                value = state.wateringIntervalDays,
                onSelect = { viewModel.onEvent(AddPlantEvent.WateringIntervalChanged(it)) }
            )
        }

        // Sticky CTA
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(PageBg)
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            val enabled = state.isSubmitEnabled && !state.isLoading
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(if (enabled) Brand else Brand.copy(alpha = 0.4f))
                    .clickable(enabled = enabled) {
                        viewModel.onEvent(AddPlantEvent.Submit)
                    },
                contentAlignment = Alignment.Center
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(22.dp)
                    )
                } else {
                    Text(
                        text = "Add to garden",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        color = TextPrimary,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold
    )
}

@Composable
private fun SpeciesPicker(
    selectedId: String?,
    onSelect: (PlantSpecies) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(end = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(plantCatalog, key = { it.id }) { species ->
            val isSelected = species.id == selectedId
            Column(
                modifier = Modifier
                    .width(96.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(CardWhite)
                    .border(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) Brand else Color(0xFFDCE5D5),
                        shape = RoundedCornerShape(14.dp)
                    )
                    .clickable { onSelect(species) }
                    .padding(vertical = 12.dp, horizontal = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(species.emoji, fontSize = 32.sp)
                Spacer(Modifier.height(6.dp))
                Text(
                    text = species.name,
                    color = TextPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun LocationRow(selected: String, onSelect: (String) -> Unit) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(end = 8.dp)
    ) {
        items(locationOptions) { option ->
            val isSelected = option == selected
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (isSelected) Brand else ChipIdle)
                    .clickable { onSelect(option) }
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Text(
                    text = option,
                    color = if (isSelected) Color.White else TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun WateringPicker(value: String, onSelect: (String) -> Unit) {
    val current = value.toIntOrNull()
    Column {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            waterPresets.forEach { days ->
                val isSelected = current == days
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isSelected) Brand else ChipIdle)
                        .clickable { onSelect(days.toString()) }
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = "${days}d",
                        color = if (isSelected) Color.White else TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }
        Spacer(Modifier.height(10.dp))
        OutlinedTextField(
            value = value,
            onValueChange = { onSelect(it.filter { c -> c.isDigit() }.take(3)) },
            placeholder = { Text("Custom days", color = Hint) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Brand,
                unfocusedBorderColor = Color(0xFFDCE5D5),
                focusedContainerColor = CardWhite,
                unfocusedContainerColor = CardWhite,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            )
        )
    }
}
