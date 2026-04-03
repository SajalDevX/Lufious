package ai.lufious.app.presentation.shop.ui

import ai.lufious.app.core.theme.Background
import ai.lufious.app.core.theme.PrimaryColor
import ai.lufious.app.core.utils.BACK_BUTTON_HEIGHT_FRACTION
import ai.lufious.app.core.utils.ShadowButton
import ai.lufious.app.core.utils.UiEffect
import ai.lufious.app.core.utils.hR
import ai.lufious.app.core.utils.heightFraction
import ai.lufious.app.core.utils.rememberResponsiveDimensions
import ai.lufious.app.core.utils.wR
import ai.lufious.app.presentation.shop.data.models.ListingCategory
import ai.lufious.app.presentation.shop.viewmodel.CreateListingEvent
import ai.lufious.app.presentation.shop.viewmodel.CreateListingViewModel
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
import androidx.compose.material3.ExposedDropdownMenu
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

@Composable
fun CreateListingScreen(
    navController: NavController,
    viewModel: CreateListingViewModel = hiltViewModel()
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
                    text = "Create Listing",
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
                .padding(innerPadding)
                .padding(horizontal = dimensions.wR(8f).dp, vertical = dimensions.hR(8f).dp)
                .testTag("create_listing_screen"),
            contentAlignment = Alignment.BottomCenter
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(0.78f.heightFraction(dimensions).dp),
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
                            text = "Sell something for another plant lover",
                            color = Color(0xFFB0AFFF),
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        CommonTextField(
                            value = state.title,
                            onValueChange = { viewModel.onEvent(CreateListingEvent.TitleChanged(it)) },
                            responsive = dimensions,
                            placeholder = "Listing title",
                            imeAction = ImeAction.Next,
                            onImeAction = {}
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        CommonTextField(
                            value = state.description,
                            onValueChange = {
                                viewModel.onEvent(CreateListingEvent.DescriptionChanged(it))
                            },
                            responsive = dimensions,
                            placeholder = "Description",
                            imeAction = ImeAction.Next,
                            onImeAction = {},
                            singleLine = false
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        CommonTextField(
                            value = state.price,
                            onValueChange = { viewModel.onEvent(CreateListingEvent.PriceChanged(it)) },
                            responsive = dimensions,
                            placeholder = "Price in USD",
                            imeAction = ImeAction.Next,
                            onImeAction = {}
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        var expanded by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded }
                        ) {
                            TextField(
                                value = state.category,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Category", color = Color.White.copy(alpha = 0.6f)) },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                                },
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
                                ListingCategory.listingOptions.forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text(option) },
                                        onClick = {
                                            viewModel.onEvent(CreateListingEvent.CategoryChanged(option))
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    ShadowButton(
                        text = "CREATE LISTING",
                        onClick = { viewModel.onEvent(CreateListingEvent.Submit) },
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
