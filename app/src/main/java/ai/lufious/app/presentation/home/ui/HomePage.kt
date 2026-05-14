package ai.lufious.app.presentation.home.ui

import ai.lufious.app.core.theme.Background
import ai.lufious.app.core.theme.CriticalRed
import ai.lufious.app.core.theme.HealthyGreen
import ai.lufious.app.core.theme.LimeAccent
import ai.lufious.app.core.theme.PrimaryColor
import ai.lufious.app.core.theme.WarningOrange
import ai.lufious.app.core.utils.Screen
import ai.lufious.app.presentation.catalog.DemoListing
import ai.lufious.app.presentation.catalog.PlantSpecies
import ai.lufious.app.presentation.catalog.featuredListings
import ai.lufious.app.presentation.catalog.plantCatalog
import ai.lufious.app.presentation.catalog.tipOfTheDay
import ai.lufious.app.presentation.garden.data.models.PlantModel
import ai.lufious.app.presentation.home.viewmodel.HomeViewModel
import coil.compose.AsyncImage
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import ai.lufious.app.core.theme.TextPrimary
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay

private data class HomeBanner(
    val title: String,
    val subtitle: String,
    val cta: String,
    val icon: ImageVector,
    val gradient: List<Color>,
    val onClick: () -> Unit
)

private data class FeatureCard(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val tint: Color,
    val onClick: () -> Unit
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomePage(
    outerNavController: NavHostController,
    tabNavController: NavHostController? = null,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val greeting = when (java.time.LocalTime.now().hour) {
        in 5..11 -> "Good morning"
        in 12..17 -> "Good afternoon"
        else -> "Good evening"
    }

    val goTab: (String) -> Unit = { route ->
        tabNavController?.navigate(route) {
            popUpTo(Screen.HomeTab.route)
            launchSingleTop = true
        }
    }

    val banners = remember(state) {
        buildList {
            if (state.plantsNeedingWater.isNotEmpty()) {
                add(
                    HomeBanner(
                        title = "${state.plantsNeedingWater.size} plant${if (state.plantsNeedingWater.size == 1) "" else "s"} need water",
                        subtitle = "Tap to log a watering and reset the schedule.",
                        cta = "Open Garden",
                        icon = Icons.Default.WaterDrop,
                        gradient = listOf(Color(0xFF2E5D2E), Color(0xFF1E3D1E)),
                        onClick = { goTab(Screen.GardenTab.route) }
                    )
                )
            }
            val tipText = state.aiTip ?: tipOfTheDay()
            add(
                HomeBanner(
                    title = if (state.aiTip != null) "AI Care Tip" else "Tip of the day",
                    subtitle = tipText,
                    cta = "Open Scan",
                    icon = Icons.Default.WbSunny,
                    gradient = listOf(Color(0xFF1E3D1E), Color(0xFF152415)),
                    onClick = { goTab(Screen.ScanTab.route) }
                )
            )
            if (state.weatherAlertsCount > 0) {
                add(
                    HomeBanner(
                        title = "${state.weatherAlertsCount} weather alert${if (state.weatherAlertsCount == 1) "" else "s"}",
                        subtitle = "Active weather conditions for your area. Adjust care today.",
                        cta = "View",
                        icon = Icons.Default.NotificationsActive,
                        gradient = listOf(Color(0xFF5D2E1E), Color(0xFF3D1E12)),
                        onClick = { goTab(Screen.HomeTab.route) }
                    )
                )
            }
            if (isEmpty()) {
                add(
                    HomeBanner(
                        title = "Welcome to Lufious",
                        subtitle = "Add your first plant or scan one with the camera to get started.",
                        cta = "Scan a Plant",
                        icon = Icons.Default.LocalFlorist,
                        gradient = listOf(Color(0xFF2E5D2E), Color(0xFF1E3D1E)),
                        onClick = { goTab(Screen.ScanTab.route) }
                    )
                )
            }
        }
    }

    val features = listOf(
        FeatureCard("My Garden", "Track your plants", Icons.Default.LocalFlorist, HealthyGreen) {
            goTab(Screen.GardenTab.route)
        },
        FeatureCard("Scan Plant", "Identify with AI", Icons.Default.PhotoCamera, LimeAccent) {
            goTab(Screen.ScanTab.route)
        },
        FeatureCard("Marketplace", "Buy & sell items", Icons.Default.ShoppingBag, WarningOrange) {
            goTab(Screen.ShopTab.route)
        },
        FeatureCard("Profile", "Settings & account", Icons.Default.Person, PrimaryColor) {
            outerNavController.navigate(Screen.Profile.route)
        }
    )

    val suggestedSpecies = remember(state.userName) {
        plantCatalog.shuffled().take(8)
    }
    val featured = remember { featuredListings.shuffled().take(6) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Background)
            .padding(horizontal = 18.dp, vertical = 24.dp)
            .testTag("home_screen"),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item {
            HomeHeader(
                greeting = greeting,
                userName = state.userName,
                onProfileClick = { outerNavController.navigate(Screen.Profile.route) }
            )
        }

        item {
            // Reserved 2dp slot — visible bar while loading, transparent when idle.
            // Prevents LazyColumn reflow when isLoading toggles.
            Box(modifier = Modifier.fillMaxWidth().height(2.dp)) {
                if (state.isLoading) {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth(),
                        color = PrimaryColor,
                        trackColor = Color.Transparent
                    )
                }
            }
        }

        item { BannerCarousel(banners = banners) }

        item {
            StatsRow(
                totalPlants = state.totalPlants,
                needsWater = state.plantsNeedingWater.size,
                weatherAlerts = state.weatherAlertsCount
            )
        }

        item { SectionTitle("Quick actions") }

        item { FeatureGrid(cards = features) }

        if (state.plantsNeedingWater.isNotEmpty()) {
            item { SectionTitle("Needs water") }
            items(state.plantsNeedingWater, key = { it.id }) { plant ->
                WaterReminderCard(plant = plant) { goTab(Screen.GardenTab.route) }
            }
        } else if (state.totalPlants > 0 && !state.isLoading) {
            item { AllHappyCard() }
        }

        item { SectionTitle("Plants to try") }
        item { SuggestedSpeciesRow(species = suggestedSpecies) }

        item { SectionTitle("Trending in Shop") }
        item {
            FeaturedListingsRow(
                listings = featured,
                onClick = { goTab(Screen.ShopTab.route) }
            )
        }

        item { Spacer(Modifier.height(8.dp)) }
    }
}

@Composable
private fun SuggestedSpeciesRow(species: List<PlantSpecies>) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(species, key = { it.id }) { s ->
            Column(
                modifier = Modifier
                    .width(120.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(TextPrimary.copy(alpha = 0.06f))
                    .padding(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(PrimaryColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(s.emoji, fontSize = 24.sp)
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    text = s.name,
                    color = TextPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                Text(
                    text = s.difficulty.name,
                    color = TextPrimary.copy(alpha = 0.55f),
                    fontSize = 10.sp
                )
            }
        }
    }
}

@Composable
private fun FeaturedListingsRow(
    listings: List<DemoListing>,
    onClick: () -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(listings, key = { it.id }) { l ->
            Column(
                modifier = Modifier
                    .width(150.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(TextPrimary.copy(alpha = 0.06f))
                    .clickable { onClick() }
                    .padding(10.dp)
            ) {
                AsyncImage(
                    model = l.photoUrl,
                    contentDescription = l.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(90.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(TextPrimary.copy(alpha = 0.05f))
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = l.title,
                    color = TextPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                Text(
                    text = "₹${l.priceCents / 100}",
                    color = PrimaryColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun HomeHeader(
    greeting: String,
    userName: String,
    onProfileClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "$greeting 🌿",
                color = TextPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = userName,
                color = TextPrimary.copy(alpha = 0.6f),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
        }
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(TextPrimary.copy(alpha = 0.08f))
                .clickable { onProfileClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Profile",
                tint = TextPrimary.copy(alpha = 0.85f),
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun BannerCarousel(banners: List<HomeBanner>) {
    if (banners.isEmpty()) return
    val pagerState = rememberPagerState(pageCount = { banners.size })

    LaunchedEffect(banners.size) {
        if (banners.size <= 1) return@LaunchedEffect
        while (true) {
            delay(5_000L)
            val next = (pagerState.currentPage + 1) % banners.size
            pagerState.animateScrollToPage(next)
        }
    }

    Column {
        HorizontalPager(
            state = pagerState,
            pageSpacing = 12.dp,
            modifier = Modifier.fillMaxWidth().height(150.dp)
        ) { page ->
            BannerCard(banners[page])
        }
        Spacer(Modifier.height(10.dp))
        // Always reserve indicator slot so growing banner count doesn't reflow.
        Row(
            modifier = Modifier.fillMaxWidth().height(6.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            if (banners.size > 1) {
                repeat(banners.size) { i ->
                    val active = pagerState.currentPage == i
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 3.dp)
                            .height(6.dp)
                            .width(if (active) 20.dp else 6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(
                                if (active) PrimaryColor
                                else TextPrimary.copy(alpha = 0.25f)
                            )
                    )
                }
            }
        }
    }
}

@Composable
private fun BannerCard(banner: HomeBanner) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .clickable { banner.onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(brush = Brush.linearGradient(banner.gradient))
                .padding(18.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(TextPrimary.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = banner.icon,
                            contentDescription = null,
                            tint = LimeAccent,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    Text(
                        text = banner.title,
                        color = TextPrimary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.ExtraBold,
                        maxLines = 1
                    )
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    text = banner.subtitle,
                    color = TextPrimary.copy(alpha = 0.78f),
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    maxLines = 3
                )
                Spacer(Modifier.weight(1f))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(PrimaryColor)
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = banner.cta,
                            color = TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatsRow(totalPlants: Int, needsWater: Int, weatherAlerts: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        StatTile(
            modifier = Modifier.weight(1f),
            value = totalPlants.toString(),
            label = "Plants",
            color = PrimaryColor,
            tintBg = PrimaryColor.copy(alpha = 0.15f)
        )
        StatTile(
            modifier = Modifier.weight(1f),
            value = needsWater.toString(),
            label = "Need water",
            color = if (needsWater == 0) HealthyGreen else WarningOrange,
            tintBg = (if (needsWater == 0) HealthyGreen else WarningOrange).copy(alpha = 0.15f)
        )
        StatTile(
            modifier = Modifier.weight(1f),
            value = weatherAlerts.toString(),
            label = "Alerts",
            color = if (weatherAlerts == 0) LimeAccent else CriticalRed,
            tintBg = (if (weatherAlerts == 0) LimeAccent else CriticalRed).copy(alpha = 0.12f)
        )
    }
}

@Composable
private fun StatTile(
    modifier: Modifier = Modifier,
    value: String,
    label: String,
    color: Color,
    tintBg: Color
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(tintBg)
            .padding(14.dp)
    ) {
        Column {
            Text(
                text = value,
                color = color,
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = label,
                color = TextPrimary.copy(alpha = 0.7f),
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        color = TextPrimary,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
    )
}

@Composable
private fun FeatureGrid(cards: List<FeatureCard>) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        cards.chunked(2).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                row.forEach { card ->
                    FeatureTile(modifier = Modifier.weight(1f), card = card)
                }
                if (row.size == 1) Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun FeatureTile(modifier: Modifier = Modifier, card: FeatureCard) {
    Box(
        modifier = modifier
            .height(118.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(TextPrimary.copy(alpha = 0.06f))
            .clickable { card.onClick() }
            .padding(14.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(9.dp))
                    .background(card.tint.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = card.icon,
                    contentDescription = null,
                    tint = card.tint,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(Modifier.weight(1f))
            Text(
                text = card.title,
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = card.subtitle,
                color = TextPrimary.copy(alpha = 0.55f),
                fontSize = 11.sp,
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun WaterReminderCard(plant: PlantModel, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(WarningOrange.copy(alpha = 0.10f))
            .clickable { onClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(WarningOrange.copy(alpha = 0.22f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = plant.nickname.firstOrNull()?.uppercaseChar()?.toString() ?: "🌱",
                fontSize = 16.sp,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = plant.nickname,
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Due for watering",
                color = WarningOrange,
                fontSize = 12.sp
            )
        }
        Text(
            text = "›",
            color = TextPrimary.copy(alpha = 0.4f),
            fontSize = 22.sp
        )
    }
}

@Composable
private fun AllHappyCard() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(HealthyGreen.copy(alpha = 0.10f))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "✅", fontSize = 18.sp)
        Spacer(Modifier.width(10.dp))
        Text(
            text = "All plants are happy today",
            color = HealthyGreen,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
