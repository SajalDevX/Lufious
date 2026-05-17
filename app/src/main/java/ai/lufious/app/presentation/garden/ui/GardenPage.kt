package ai.lufious.app.presentation.garden.ui

import ai.lufious.app.core.theme.TextPrimary
import ai.lufious.app.core.utils.Screen
import ai.lufious.app.presentation.garden.viewmodel.GardenViewModel
import coil.compose.AsyncImage
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

// ── Design tokens ───────────────────────────────────────────────
private val GardenBg          = Color(0xFFF8FBF8)
private val CardWhite         = Color.White
private val SearchBg          = Color(0xFFF2F4F2)
private val ChipSelectedBg    = Color(0xFF22C55E)
private val ChipSelectedText  = Color.White
private val ChipIdleBg        = Color(0xFFEEF0EE)
private val ChipIdleText      = Color(0xFF444444)
private val FeaturedBadgeBg   = Color(0xFFFFF8E1)
private val FeaturedBadgeText = Color(0xFF856404)
private val HealthyBg         = Color(0xFFE8F5E8)
private val HealthyText       = Color(0xFF1A7A3C)
private val NeedsWaterBg      = Color(0xFFFFF3E0)
private val NeedsWaterText    = Color(0xFFEA580C)
private val WaterGreen        = Color(0xFF22C55E)
private val AddPlantGreen     = Color(0xFF1A5C35)

// ── Demo data ───────────────────────────────────────────────────
private data class GardenDemoPlant(
    val name: String,
    val scientific: String,
    val photoUrl: String,
    val category: String,
    val waterInDays: Int,
    val isHealthy: Boolean,
    val tags: List<String> = emptyList(),
    val isFeatured: Boolean = false
)

private val demoPlants = listOf(
    GardenDemoPlant(
        name = "Monstera", scientific = "Deliciosa",
        photoUrl = "https://images.unsplash.com/photo-1614594975525-e45190c55d0b?w=600",
        category = "Indoor", waterInDays = 2, isHealthy = true,
        tags = listOf("Indoor Plant", "Low Light"), isFeatured = true
    ),
    GardenDemoPlant(
        name = "Snake Plant", scientific = "Sansevieria",
        photoUrl = "https://images.unsplash.com/photo-1593482892290-f54927ae1bb6?w=400",
        category = "Indoor", waterInDays = 0, isHealthy = false
    ),
    GardenDemoPlant(
        name = "Pothos", scientific = "Epipremnum Aureum",
        photoUrl = "https://images.unsplash.com/photo-1572688484438-313a6e50c333?w=400",
        category = "Indoor", waterInDays = 3, isHealthy = true
    ),
    GardenDemoPlant(
        name = "Echeveria", scientific = "Elegans",
        photoUrl = "https://images.unsplash.com/photo-1459411552884-841db9b3cc2a?w=400",
        category = "Succulents", waterInDays = 3, isHealthy = true
    ),
    GardenDemoPlant(
        name = "Boston Fern", scientific = "Nephrolepis Exaltata",
        photoUrl = "https://images.unsplash.com/photo-1520302519878-3592b18a0b6e?w=400",
        category = "Outdoor", waterInDays = 1, isHealthy = true
    ),
    GardenDemoPlant(
        name = "Areca Palm", scientific = "Dypsis Lutescens",
        photoUrl = "https://images.unsplash.com/photo-1518998053901-5348d3961a04?w=400",
        category = "Indoor", waterInDays = 4, isHealthy = true
    )
)

private val gardenCategories = listOf("All Plants", "Indoor", "Outdoor", "Succulents")

private val categoryEmojis = mapOf(
    "All Plants" to "🌿",
    "Indoor"     to "🏠",
    "Outdoor"    to "🌿",
    "Succulents" to "🌵"
)

// ── Screen ──────────────────────────────────────────────────────
@Composable
fun GardenPage(
    navController: NavController,
    outerNavController: NavController = navController,
    modifier: Modifier = Modifier,
    viewModel: GardenViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All Plants") }

    val hour = remember {
        java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
    }
    val greeting = when (hour) {
        in 5..11 -> "Good Morning"
        in 12..17 -> "Good Afternoon"
        else -> "Good Evening"
    }

    val showDemo = state.plants.isEmpty() && !state.isLoading

    val filteredDemo = remember(selectedCategory) {
        if (selectedCategory == "All Plants") demoPlants
        else demoPlants.filter { it.category.equals(selectedCategory, ignoreCase = true) }
    }

    val featuredPlant = filteredDemo.firstOrNull { it.isFeatured } ?: filteredDemo.firstOrNull()
    val gridPlants    = filteredDemo.filter { !it.isFeatured }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(GardenBg)
            .testTag("garden_screen")
    ) {
        if (showDemo) {
            EmptyGardenIntro(
                greeting = greeting,
                onAddPlant = { outerNavController.navigate(Screen.AddPlant.route) }
            )
            return@Box
        }
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 16.dp, end = 16.dp, top = 0.dp, bottom = 96.dp
            ),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header
            item(span = { GridItemSpan(2) }) {
                GardenHeader(
                    greeting = greeting,
                    plantCount = if (showDemo) 26 else state.plants.size
                )
            }
            // Search
            item(span = { GridItemSpan(2) }) {
                GardenSearchBar(query = searchQuery, onQueryChange = { searchQuery = it })
            }
            // Categories
            item(span = { GridItemSpan(2) }) {
                CategoryChipsRow(
                    categories = gardenCategories,
                    selected = selectedCategory,
                    onSelect = { selectedCategory = it }
                )
            }

            if (state.isLoading) {
                item(span = { GridItemSpan(2) }) {
                    Box(
                        modifier = Modifier.fillMaxWidth().height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = ChipSelectedBg)
                    }
                }
            } else {
                // Featured card
                featuredPlant?.let { plant ->
                    item(span = { GridItemSpan(2) }) {
                        FeaturedPlantCard(plant = plant)
                    }
                }
                // Section label
                item(span = { GridItemSpan(2) }) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "My Plants",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "${gridPlants.size} plants",
                            fontSize = 12.sp,
                            color = TextPrimary.copy(alpha = 0.45f)
                        )
                    }
                }

                if (showDemo) {
                    items(gridPlants) { plant ->
                        DemoPlantGridCard(plant = plant)
                    }
                } else {
                    items(state.plants, key = { it.id }) { plant ->
                        PlantCard(
                            plant = plant,
                            onClick = {
                                navController.navigate(
                                    Screen.PlantDetail.createRoute(plant.id)
                                )
                            }
                        )
                    }
                }
            }
        }

        // FAB – "+ Add Plant" (hidden in empty state; intro has its own CTA)
        if (!showDemo) Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 20.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(AddPlantGreen)
                .clickable { outerNavController.navigate(Screen.AddPlant.route) }
                .padding(horizontal = 20.dp, vertical = 14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = "Add Plant",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

// ── Header ──────────────────────────────────────────────────────
@Composable
private fun GardenHeader(greeting: String, plantCount: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 52.dp, bottom = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Column {
            Text(
                text = "$greeting, Plant Lover 👋",
                fontSize = 13.sp,
                color = TextPrimary.copy(alpha = 0.55f)
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = "My Garden",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Box(
                modifier = Modifier
                    .padding(top = 4.dp)
                    .width(36.dp)
                    .height(3.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(ChipSelectedBg)
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "$plantCount Plants  •  ${(plantCount * 0.23).toInt()} Need Watering",
                fontSize = 12.sp,
                color = TextPrimary.copy(alpha = 0.50f)
            )
        }

        // Mini weather card
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF5FBF5)),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            modifier = Modifier.padding(top = 18.dp)
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("☀️", fontSize = 16.sp)
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "24°C",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
                Text(
                    text = "Sunny",
                    fontSize = 10.sp,
                    color = TextPrimary.copy(alpha = 0.5f)
                )
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.WaterDrop,
                        contentDescription = null,
                        tint = Color(0xFF4FC3F7),
                        modifier = Modifier.size(11.dp)
                    )
                    Spacer(Modifier.width(3.dp))
                    Text(
                        text = "60%",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

// ── Search bar ──────────────────────────────────────────────────
@Composable
private fun GardenSearchBar(query: String, onQueryChange: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(SearchBg)
            .then(
                Modifier.border(
                    width = 1.5.dp,
                    color = ChipSelectedBg,
                    shape = RoundedCornerShape(14.dp)
                )
            )
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = null,
            tint = TextPrimary.copy(alpha = 0.38f),
            modifier = Modifier.size(18.dp)
        )
        Spacer(Modifier.width(10.dp))
        Text(
            text = if (query.isEmpty()) "Search your plants..." else query,
            fontSize = 13.sp,
            color = if (query.isEmpty()) TextPrimary.copy(alpha = 0.35f) else TextPrimary,
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = Icons.Default.Tune,
            contentDescription = "Filter",
            tint = TextPrimary.copy(alpha = 0.45f),
            modifier = Modifier.size(18.dp)
        )
    }
}

// ── Category chips ──────────────────────────────────────────────
@Composable
private fun CategoryChipsRow(
    categories: List<String>,
    selected: String,
    onSelect: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        categories.forEach { category ->
            val isSelected = category == selected
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (isSelected) ChipSelectedBg else ChipIdleBg)
                    .clickable { onSelect(category) }
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = categoryEmojis[category] ?: "🌱", fontSize = 13.sp)
                Spacer(Modifier.width(5.dp))
                Text(
                    text = category,
                    fontSize = 13.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSelected) ChipSelectedText else ChipIdleText
                )
            }
        }
    }
}

// ── Featured card ────────────────────────────────────────────────
@Composable
private fun FeaturedPlantCard(plant: GardenDemoPlant) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth().height(168.dp)) {
            // Image – right half
            AsyncImage(
                model = plant.photoUrl,
                contentDescription = plant.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(160.dp)
                    .fillMaxHeight()
                    .align(Alignment.CenterEnd)
                    .clip(RoundedCornerShape(topEnd = 20.dp, bottomEnd = 20.dp))
            )
            // Text content – left
            Column(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 16.dp, top = 14.dp, end = 168.dp, bottom = 14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(FeaturedBadgeBg)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "⭐ Featured",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = FeaturedBadgeText
                    )
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    text = plant.name,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = plant.scientific,
                    fontSize = 12.sp,
                    color = TextPrimary.copy(alpha = 0.50f)
                )
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.WaterDrop,
                        contentDescription = null,
                        tint = WaterGreen,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = if (plant.waterInDays <= 0) "Needs water now"
                               else "Water in ${plant.waterInDays} days",
                        fontSize = 11.sp,
                        color = WaterGreen,
                        fontWeight = FontWeight.Medium
                    )
                }
                if (plant.tags.isNotEmpty()) {
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                        plant.tags.forEach { tag ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(HealthyBg)
                                    .padding(horizontal = 7.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = tag,
                                    fontSize = 9.sp,
                                    color = HealthyText,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }
            // Heart
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(10.dp)
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.85f))
                    .clickable {},
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.FavoriteBorder,
                    contentDescription = "Favourite",
                    tint = Color(0xFFE57373),
                    modifier = Modifier.size(15.dp)
                )
            }
        }
    }
}

// ── Demo plant grid card ─────────────────────────────────────────
@Composable
private fun DemoPlantGridCard(plant: GardenDemoPlant) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column {
            Box(modifier = Modifier.fillMaxWidth()) {
                AsyncImage(
                    model = plant.photoUrl,
                    contentDescription = plant.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.85f))
                        .clickable {},
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.FavoriteBorder,
                        contentDescription = "Favourite",
                        tint = Color(0xFFE57373),
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
            Column(modifier = Modifier.padding(10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.WaterDrop,
                        contentDescription = null,
                        tint = WaterGreen,
                        modifier = Modifier.size(11.dp)
                    )
                    Spacer(Modifier.width(3.dp))
                    Text(
                        text = if (plant.waterInDays <= 0) "Needs water"
                               else "Water in ${plant.waterInDays} days",
                        fontSize = 10.sp,
                        color = WaterGreen
                    )
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    text = plant.name,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = plant.scientific,
                    fontSize = 10.sp,
                    color = TextPrimary.copy(alpha = 0.45f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (plant.isHealthy) HealthyBg else NeedsWaterBg)
                        .padding(horizontal = 7.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = if (plant.isHealthy) "Healthy" else "Needs Water",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (plant.isHealthy) HealthyText else NeedsWaterText
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyGardenIntro(greeting: String, onAddPlant: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(GardenBg)
            .padding(horizontal = 20.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = greeting,
            color = TextPrimary.copy(alpha = 0.55f),
            fontSize = 13.sp,
            modifier = Modifier.fillMaxWidth(),
            fontWeight = FontWeight.Medium
        )
        Text(
            text = "Your garden",
            color = TextPrimary,
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(28.dp))
        Box(
            modifier = Modifier
                .size(160.dp)
                .clip(CircleShape)
                .background(ChipSelectedBg.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "🪴", fontSize = 88.sp)
        }
        Spacer(Modifier.height(20.dp))
        Text(
            text = "Plant your first one",
            color = TextPrimary,
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = "Track watering, health, and growth — all in one calm place.",
            color = TextPrimary.copy(alpha = 0.6f),
            fontSize = 13.sp,
            modifier = Modifier.fillMaxWidth(),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Spacer(Modifier.height(24.dp))
        IntroStep(emoji = "📸", title = "Scan or add", body = "Use the camera or pick a species manually.")
        Spacer(Modifier.height(10.dp))
        IntroStep(emoji = "💧", title = "Set a schedule", body = "We'll remind you when it's watering day.")
        Spacer(Modifier.height(10.dp))
        IntroStep(emoji = "🌱", title = "Watch it grow", body = "Log progress photos and care notes.")
        Spacer(Modifier.weight(1f))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(AddPlantGreen)
                .clickable { onAddPlant() },
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Add Your First Plant",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun IntroStep(emoji: String, title: String, body: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(CardWhite)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = emoji, fontSize = 26.sp)
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = body,
                color = TextPrimary.copy(alpha = 0.55f),
                fontSize = 12.sp
            )
        }
    }
}
