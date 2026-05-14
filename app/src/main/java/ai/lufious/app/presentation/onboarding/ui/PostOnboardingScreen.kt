package ai.lufious.app.presentation.onboarding.ui

import ai.lufious.app.core.theme.Background
import ai.lufious.app.core.theme.LimeAccent
import ai.lufious.app.core.theme.PrimaryColor
import ai.lufious.app.core.utils.MAIN_GRAPH
import ai.lufious.app.core.utils.UiEffect
import ai.lufious.app.presentation.catalog.PlantSpecies
import ai.lufious.app.presentation.catalog.matches
import ai.lufious.app.presentation.catalog.plantCatalog
import ai.lufious.app.presentation.onboarding.viewmodel.PostOnboardingEvent
import ai.lufious.app.presentation.onboarding.viewmodel.PostOnboardingState
import ai.lufious.app.presentation.onboarding.viewmodel.PostOnboardingViewModel
import ai.lufious.app.presentation.onboarding.viewmodel.TOTAL_ONBOARDING_STEPS
import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.android.gms.location.LocationServices

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PostOnboardingScreen(
    navController: NavController,
    viewModel: PostOnboardingViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    val locationPermLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
            android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            try {
                LocationServices.getFusedLocationProviderClient(context)
                    .lastLocation
                    .addOnSuccessListener { loc ->
                        if (loc != null) {
                            viewModel.onEvent(
                                PostOnboardingEvent.SaveLocation(loc.latitude, loc.longitude)
                            )
                        }
                        viewModel.onEvent(PostOnboardingEvent.NextStep)
                    }
                    .addOnFailureListener {
                        viewModel.onEvent(PostOnboardingEvent.NextStep)
                    }
            } catch (_: SecurityException) {
                viewModel.onEvent(PostOnboardingEvent.NextStep)
            }
        } else {
            viewModel.onEvent(PostOnboardingEvent.NextStep)
        }
    }

    val notifPermLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        viewModel.onEvent(PostOnboardingEvent.NextStep)
    }

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is UiEffect.Navigate -> navController.navigate(MAIN_GRAPH) {
                    popUpTo(0) { inclusive = true }
                }
                else -> Unit
            }
        }
    }

    val pagerState = rememberPagerState(
        initialPage = state.currentStep,
        pageCount = { TOTAL_ONBOARDING_STEPS }
    )

    LaunchedEffect(state.currentStep) {
        if (pagerState.currentPage != state.currentStep) {
            pagerState.animateScrollToPage(state.currentStep)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF1E3D1E), Background)))
            .safeDrawingPadding()
            .padding(horizontal = 24.dp, vertical = 24.dp)
    ) {
        ProgressBar(step = state.currentStep, total = TOTAL_ONBOARDING_STEPS)
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Step ${state.currentStep + 1} of $TOTAL_ONBOARDING_STEPS",
            color = Color.White.copy(alpha = 0.45f),
            fontSize = 11.sp,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(20.dp))

        HorizontalPager(
            state = pagerState,
            userScrollEnabled = false,
            modifier = Modifier.weight(1f)
        ) { page ->
            when (page) {
                0 -> WelcomeStep()
                1 -> ChoiceStep(
                    emoji = "🌿",
                    title = "How experienced are you?",
                    subtitle = "We'll tailor suggestions to your level.",
                    options = listOf(
                        "beginner" to "Brand new to plants",
                        "intermediate" to "Killed a few, learning",
                        "expert" to "I run a jungle"
                    ),
                    selected = state.gardenerLevel,
                    onPick = { viewModel.onEvent(PostOnboardingEvent.SetLevel(it)) }
                )
                2 -> InterestsStep(
                    selected = state.interestCategories,
                    onToggle = { viewModel.onEvent(PostOnboardingEvent.ToggleInterest(it)) }
                )
                3 -> ChoiceStep(
                    emoji = "🎯",
                    title = "Your primary goal?",
                    subtitle = "What draws you to plant care?",
                    options = listOf(
                        "decor" to "Decorate my space",
                        "wellness" to "Wellness and calm",
                        "food" to "Grow my own food",
                        "hobby" to "Just a fun hobby"
                    ),
                    selected = state.gardenerGoal,
                    onPick = { viewModel.onEvent(PostOnboardingEvent.SetGoal(it)) }
                )
                4 -> ChoiceStep(
                    emoji = "🌍",
                    title = "Your climate?",
                    subtitle = "Helps us recommend plants that thrive nearby.",
                    options = listOf(
                        "tropical" to "Tropical / warm + humid",
                        "temperate" to "Temperate / four seasons",
                        "arid" to "Arid / dry",
                        "cold" to "Cold / long winters"
                    ),
                    selected = state.climateZone,
                    onPick = { viewModel.onEvent(PostOnboardingEvent.SetClimate(it)) }
                )
                5 -> ChoiceStep(
                    emoji = "🏠",
                    title = "Where will plants live?",
                    subtitle = "Apartment, house, balcony — all good.",
                    options = listOf(
                        "apartment" to "Apartment (indoor focus)",
                        "house" to "House with yard",
                        "balcony" to "Balcony / small outdoor"
                    ),
                    selected = state.livingSpace,
                    onPick = { viewModel.onEvent(PostOnboardingEvent.SetSpace(it)) }
                )
                6 -> SimpleStep(
                    emoji = "📍",
                    title = "Allow location",
                    subtitle = "We use your location for weather-adjusted care tips."
                )
                7 -> SimpleStep(
                    emoji = "🔔",
                    title = "Stay on track",
                    subtitle = "Daily reminders so you never miss a watering."
                )
                8 -> PickPlantsStep(
                    state = state,
                    onToggle = { viewModel.onEvent(PostOnboardingEvent.ToggleSpecies(it)) }
                )
                9 -> CompleteStep()
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        FooterButtons(
            state = state,
            onPrimary = {
                when (state.currentStep) {
                    6 -> locationPermLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                    7 -> {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            notifPermLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        } else {
                            viewModel.onEvent(PostOnboardingEvent.NextStep)
                        }
                    }
                    9 -> viewModel.onEvent(PostOnboardingEvent.Complete)
                    else -> viewModel.onEvent(PostOnboardingEvent.NextStep)
                }
            },
            onSkip = {
                if (state.currentStep < TOTAL_ONBOARDING_STEPS - 1) {
                    viewModel.onEvent(PostOnboardingEvent.NextStep)
                } else {
                    viewModel.onEvent(PostOnboardingEvent.Complete)
                }
            }
        )
    }
}

@Composable
private fun ProgressBar(step: Int, total: Int) {
    val target = (step + 1).toFloat() / total
    val progress by animateFloatAsState(
        targetValue = target,
        animationSpec = tween(durationMillis = 320),
        label = "onboarding_progress"
    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(6.dp)
            .clip(RoundedCornerShape(3.dp))
            .background(Color.White.copy(alpha = 0.10f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(progress)
                .background(
                    Brush.horizontalGradient(listOf(PrimaryColor, LimeAccent))
                )
        )
    }
}

@Composable
private fun WelcomeStep() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(200.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(
                            PrimaryColor.copy(alpha = 0.3f),
                            LimeAccent.copy(alpha = 0.05f),
                            Color.Transparent
                        )
                    )
                )
                .border(1.dp, PrimaryColor.copy(alpha = 0.25f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text("🌱", fontSize = 96.sp)
        }
        Spacer(Modifier.height(32.dp))
        Text(
            text = "Welcome to Lufious",
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = "Let's set up your garden in a few quick steps.",
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 15.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun ChoiceStep(
    emoji: String,
    title: String,
    subtitle: String,
    options: List<Pair<String, String>>,
    selected: String?,
    onPick: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        StepHeader(emoji = emoji, title = title, subtitle = subtitle)
        Spacer(Modifier.height(24.dp))
        options.forEach { (id, label) ->
            val active = selected == id
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        if (active) PrimaryColor.copy(alpha = 0.22f)
                        else Color.White.copy(alpha = 0.06f)
                    )
                    .border(
                        width = if (active) 2.dp else 1.dp,
                        color = if (active) PrimaryColor else Color.White.copy(alpha = 0.10f),
                        shape = RoundedCornerShape(14.dp)
                    )
                    .clickable { onPick(id) }
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Text(
                    text = label,
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = if (active) FontWeight.Bold else FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun InterestsStep(
    selected: Set<String>,
    onToggle: (String) -> Unit
) {
    val categories = listOf(
        "indoor" to "🪴 Indoor",
        "outdoor" to "🌳 Outdoor",
        "herb" to "🌿 Herbs",
        "succulent" to "🌵 Succulents",
        "flower" to "🌸 Flowers",
        "vegetable" to "🥬 Vegetables"
    )
    Column(modifier = Modifier.fillMaxSize()) {
        StepHeader(
            emoji = "💚",
            title = "What interests you?",
            subtitle = "Pick any that apply — we'll recommend matching plants."
        )
        Spacer(Modifier.height(20.dp))
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(categories, key = { it.first }) { (id, label) ->
                val active = id in selected
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            if (active) PrimaryColor.copy(alpha = 0.22f)
                            else Color.White.copy(alpha = 0.06f)
                        )
                        .border(
                            if (active) 2.dp else 1.dp,
                            if (active) PrimaryColor else Color.White.copy(alpha = 0.10f),
                            RoundedCornerShape(14.dp)
                        )
                        .clickable { onToggle(id) }
                        .padding(vertical = 18.dp, horizontal = 12.dp)
                ) {
                    Text(
                        text = label,
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = if (active) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun SimpleStep(emoji: String, title: String, subtitle: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(160.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(
                            PrimaryColor.copy(alpha = 0.28f),
                            Color.Transparent
                        )
                    )
                )
                .border(1.dp, PrimaryColor.copy(alpha = 0.2f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(emoji, fontSize = 72.sp)
        }
        Spacer(Modifier.height(28.dp))
        Text(
            text = title,
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = subtitle,
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
        )
    }
}

@Composable
private fun PickPlantsStep(
    state: PostOnboardingState,
    onToggle: (String) -> Unit
) {
    val suggestions = RememberPickPlants(state)
    Column(modifier = Modifier.fillMaxSize()) {
        StepHeader(
            emoji = "🌱",
            title = "Pick a few to start",
            subtitle = "We'll add them to your garden after setup."
        )
        Spacer(Modifier.height(16.dp))
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(suggestions, key = { it.id }) { species ->
                val picked = species.id in state.selectedSpeciesIds
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            if (picked) PrimaryColor.copy(alpha = 0.22f)
                            else Color.White.copy(alpha = 0.06f)
                        )
                        .border(
                            if (picked) 2.dp else 1.dp,
                            if (picked) PrimaryColor else Color.White.copy(alpha = 0.10f),
                            RoundedCornerShape(14.dp)
                        )
                        .clickable { onToggle(species.id) }
                        .padding(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(PrimaryColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(species.emoji, fontSize = 22.sp)
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = species.name,
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                    Text(
                        text = species.difficulty.name,
                        color = Color.White.copy(alpha = 0.55f),
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun RememberPickPlants(state: PostOnboardingState): List<PlantSpecies> {
    return androidx.compose.runtime.remember(state.gardenerLevel, state.interestCategories) {
        val filtered = plantCatalog.filter { it.matches(state.gardenerLevel, state.interestCategories) }
        (if (filtered.size >= 6) filtered else plantCatalog).shuffled().take(8)
    }
}

@Composable
private fun CompleteStep() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(220.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(LimeAccent.copy(alpha = 0.4f), PrimaryColor.copy(alpha = 0.1f), Color.Transparent)
                    )
                )
                .border(1.dp, LimeAccent.copy(alpha = 0.4f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text("🎉", fontSize = 96.sp)
        }
        Spacer(Modifier.height(28.dp))
        Text(
            text = "You're all set!",
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = "Your home is tuned to your preferences.",
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 15.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun StepHeader(emoji: String, title: String, subtitle: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = emoji, fontSize = 44.sp)
        Spacer(Modifier.height(10.dp))
        Text(
            text = title,
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = subtitle,
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 14.sp,
            lineHeight = 20.sp
        )
    }
}

@Composable
private fun FooterButtons(
    state: PostOnboardingState,
    onPrimary: () -> Unit,
    onSkip: () -> Unit
) {
    val (primaryLabel, primaryEnabled) = primaryFor(state)
    Button(
        onClick = onPrimary,
        enabled = primaryEnabled,
        modifier = Modifier.fillMaxWidth().height(54.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = PrimaryColor,
            disabledContainerColor = PrimaryColor.copy(alpha = 0.4f)
        )
    ) {
        Text(
            text = primaryLabel,
            color = Color.White,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 15.sp
        )
    }
    Spacer(Modifier.height(10.dp))
    if (state.currentStep < TOTAL_ONBOARDING_STEPS - 1) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onSkip() }
                .padding(vertical = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Skip for now",
                color = Color.White.copy(alpha = 0.55f),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
        }
    } else {
        Spacer(Modifier.height(10.dp))
    }
}

private fun primaryFor(state: PostOnboardingState): Pair<String, Boolean> {
    return when (state.currentStep) {
        0 -> "Get Started" to true
        1 -> "Continue" to (state.gardenerLevel != null)
        2 -> "Continue" to state.interestCategories.isNotEmpty()
        3 -> "Continue" to (state.gardenerGoal != null)
        4 -> "Continue" to (state.climateZone != null)
        5 -> "Continue" to (state.livingSpace != null)
        6 -> "Allow Location" to true
        7 -> "Enable Notifications" to true
        8 -> "Continue" to true
        9 -> "Open Home" to !state.isCompleting
        else -> "Continue" to true
    }
}
