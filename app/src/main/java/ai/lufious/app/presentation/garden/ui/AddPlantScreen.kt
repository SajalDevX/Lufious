package ai.lufious.app.presentation.garden.ui

import ai.lufious.app.core.theme.Background
import ai.lufious.app.core.theme.PrimaryColor
import ai.lufious.app.core.utils.BACK_BUTTON_HEIGHT_FRACTION
import ai.lufious.app.core.utils.ShadowButton
import ai.lufious.app.core.utils.UiEffect
import ai.lufious.app.core.utils.hR
import ai.lufious.app.core.utils.rememberResponsiveDimensions
import ai.lufious.app.core.utils.wR
import ai.lufious.app.presentation.garden.viewmodel.AddPlantEvent
import ai.lufious.app.presentation.garden.viewmodel.AddPlantViewModel
import ai.lufious.app.presentation.utils.CommonTextField
import android.widget.Toast
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import kotlinx.coroutines.flow.collectLatest

private val locationOptions = listOf("Living Room", "Balcony", "Bedroom", "Office", "Outdoor")

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun AddPlantScreen(
    navController: NavController,
    viewModel: AddPlantViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val dimensions = rememberResponsiveDimensions()

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
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Add Plant",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = dimensions.wR(8f).dp, vertical = dimensions.hR(8f).dp)
                .testTag("add_plant_screen"),
            contentAlignment = Alignment.BottomCenter
        ) {
            Card(
                modifier = Modifier
                    .height(dimensions.heightFraction(0.78f).dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colors.onBackground)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "New Plant",
                            color = Color(0xFFB0AFFF),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        CommonTextField(
                            value = state.nickname,
                            onValueChange = { viewModel.onEvent(AddPlantEvent.NicknameChanged(it)) },
                            responsive = dimensions,
                            placeholder = "Plant nickname",
                            imeAction = ImeAction.Next,
                            onImeAction = {},
                            modifier = Modifier.testTag("nickname_field")
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        CommonTextField(
                            value = state.species,
                            onValueChange = { viewModel.onEvent(AddPlantEvent.SpeciesChanged(it)) },
                            responsive = dimensions,
                            placeholder = "Species name",
                            imeAction = ImeAction.Next,
                            onImeAction = {},
                            modifier = Modifier.testTag("species_field")
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Location dropdown
                        var expanded by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded }
                        ) {
                            TextField(
                                value = state.locationTag,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Location", color = Color.White.copy(alpha = 0.6f)) },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.White.copy(alpha = 0.1f),
                                    unfocusedContainerColor = Color.White.copy(alpha = 0.1f),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent
                                ),
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth()
                            )
                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                locationOptions.forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text(text = option) },
                                        onClick = {
                                            viewModel.onEvent(AddPlantEvent.LocationChanged(option))
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        CommonTextField(
                            value = state.wateringIntervalDays,
                            onValueChange = {
                                viewModel.onEvent(AddPlantEvent.WateringIntervalChanged(it))
                            },
                            responsive = dimensions,
                            placeholder = "Water every N days",
                            imeAction = ImeAction.Done,
                            onImeAction = { viewModel.onEvent(AddPlantEvent.Submit) }
                        )
                    }

                    ShadowButton(
                        text = "ADD PLANT",
                        onClick = { viewModel.onEvent(AddPlantEvent.Submit) },
                        enabled = state.isSubmitEnabled && !state.isLoading,
                        isLoading = state.isLoading,
                        responsive = dimensions,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    )
                }
            }
        }
    }
}
