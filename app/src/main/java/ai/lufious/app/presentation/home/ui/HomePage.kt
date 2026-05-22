package ai.lufious.app.presentation.home.ui

import ai.lufious.app.core.theme.PrimaryColor
import ai.lufious.app.core.theme.TextPrimary
import ai.lufious.app.core.utils.Screen
import ai.lufious.app.presentation.home.viewmodel.HomeViewModel
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController

private val HeroGreenDeep = Color(0xFF124428)
private val HeroGreen = Color(0xFF1A5C35)
private val PageBg = Color(0xFFF2F8F3)
private val CardWhite = Color.White
private val GreenHealthy = Color(0xFF22C55E)
private val OrangeNeeds = Color(0xFFEA580C)
private val ProgressGreen = Color(0xFF4CAF50)
private val ProgressRed = Color(0xFFEF5350)
private val TipCardBg = Color(0xFFE8F5E8)
private val HeroDotRed = Color(0xFFEF5350)

private data class DemoPlant(
    val name: String,
    val emoji: String,
    val status: String,
    val isHealthy: Boolean,
    val health: Float
)

private data class TaskItem(
    val title: String,
    val subtitle: String,
    val emoji: String,
    val bgColor: Color
)

private data class GardenStat(
    val emoji: String,
    val count: Int,
    val label: String,
    val changeText: String,
    val changePositive: Boolean
)

@Composable
fun HomePage(
    outerNavController: NavHostController,
    tabNavController: NavHostController? = null,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val greeting = when (java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)) {
        in 5..11 -> "Good morning,"
        in 12..17 -> "Good afternoon,"
        else -> "Good evening,"
    }

    val goTab: (String) -> Unit = { route ->
        tabNavController?.let { nav ->
            nav.navigate(route) {
                popUpTo(nav.graph.startDestinationId) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        }
    }

    // Build real lists from backend state — sections hide themselves when empty.
    val realPlants: List<DemoPlant> = remember(state.recentPlants, state.plantsNeedingWater) {
        val merged = (state.recentPlants + state.plantsNeedingWater).distinctBy { it.id }
        merged.take(4).map { p ->
            val needsWater = state.plantsNeedingWater.any { it.id == p.id }
            val healthy = !needsWater && p.healthStatus.equals("healthy", ignoreCase = true)
            DemoPlant(
                name = p.nickname.ifBlank { p.species.ifBlank { "Plant" } },
                emoji = "🌿",
                status = when {
                    needsWater -> "Needs water"
                    healthy -> "Healthy"
                    else -> p.healthStatus.replaceFirstChar { it.uppercase() }
                },
                isHealthy = healthy,
                health = if (healthy) 0.8f else 0.35f
            )
        }
    }

    val tasks: List<TaskItem> = remember(state.plantsNeedingWater) {
        if (state.plantsNeedingWater.isEmpty()) emptyList()
        else listOf(
            TaskItem(
                title = "Water ${state.plantsNeedingWater.size} plant${if (state.plantsNeedingWater.size == 1) "" else "s"}",
                subtitle = state.plantsNeedingWater.joinToString(", ") { it.nickname.ifBlank { it.species } }
                    .take(60),
                emoji = "💧",
                bgColor = Color(0xFFE3F2FD)
            )
        )
    }

    val healthyCount = state.totalPlants - state.plantsNeedingWater.size
    val gardenStats: List<GardenStat> = remember(state.totalPlants, state.plantsNeedingWater) {
        if (state.totalPlants == 0) emptyList()
        else listOf(
            GardenStat("🌿", healthyCount.coerceAtLeast(0), "Healthy", "", true),
            GardenStat("💧", state.plantsNeedingWater.size, "Need care", "", false),
            GardenStat("🌱", state.totalPlants, "Total plants", "", true)
        )
    }

    val aiTip = state.aiTip
    val needsWaterCount = state.plantsNeedingWater.size

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(PageBg)
            .testTag("home_screen")
    ) {
        item {
            HeroAndWeatherSection(
                greeting = greeting,
                needsWaterCount = needsWaterCount,
                tempC = state.currentTempC,
                condition = state.currentCondition,
                icon = state.currentIcon,
                humidity = state.currentHumidity,
                windKph = state.currentWindKph,
                uvi = state.currentUvi,
                forecast = state.weatherForecast
            )
        }
        if (realPlants.isNotEmpty()) {
            item {
                Spacer(Modifier.height(20.dp))
                MyPlantsSection(
                    plants = realPlants,
                    onViewAll = { goTab(Screen.GardenTab.route) }
                )
            }
        }
        if (aiTip != null) {
            item {
                Spacer(Modifier.height(16.dp))
                AiCareTipSection(
                    tip = aiTip,
                    onSeeDetails = { goTab(Screen.ScanTab.route) }
                )
            }
        }
        if (tasks.isNotEmpty()) {
            item {
                Spacer(Modifier.height(20.dp))
                TodaysTasksSection(
                    tasks = tasks,
                    onViewAll = { goTab(Screen.GardenTab.route) }
                )
            }
        }
        if (gardenStats.isNotEmpty()) {
            item {
                Spacer(Modifier.height(20.dp))
                GardenOverviewSection(stats = gardenStats)
            }
        }
        item { Spacer(Modifier.height(32.dp)) }
    }
}

@Composable
private fun HeroAndWeatherSection(
    greeting: String,
    needsWaterCount: Int,
    tempC: Double?,
    condition: String?,
    icon: String?,
    humidity: Int?,
    windKph: Int?,
    uvi: Double?,
    forecast: List<ai.lufious.app.core.network.dto.DailyForecastDto>
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(HeroGreenDeep, HeroGreen)
                    )
                )
                .statusBarsPadding()
                .padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 44.dp)
        ) {
            // Decorative plant emoji
            Text(
                text = "🌿",
                fontSize = 96.sp,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(top = 8.dp)
            )

            // Greeting text
            Column(modifier = Modifier.padding(end = 100.dp)) {
                Text(
                    text = greeting,
                    color = Color.White.copy(alpha = 0.88f),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Normal
                )
                Text(
                    text = "Plant lover 🌿",
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Your garden is thriving!\n$needsWaterCount plants need watering today.",
                    color = Color.White.copy(alpha = 0.80f),
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            }
        }

        // Weather carousel overlapping the hero bottom (today + 6 forecast days, swipeable)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = (-22).dp)
        ) {
            WeatherCarousel(
                tempC = tempC,
                condition = condition,
                icon = icon,
                humidity = humidity,
                windKph = windKph,
                uvi = uvi,
                forecast = forecast
            )
        }
    }
}

private data class WeatherPage(
    val label: String,
    val tempLabel: String,
    val condition: String,
    val iconCode: String?,
    val stats: List<Triple<String, String, String>>
)

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
private fun WeatherCarousel(
    tempC: Double?,
    condition: String?,
    icon: String?,
    humidity: Int?,
    windKph: Int?,
    uvi: Double?,
    forecast: List<ai.lufious.app.core.network.dto.DailyForecastDto>
) {
    val pages = remember(tempC, condition, icon, humidity, windKph, uvi, forecast) {
        buildList {
            add(
                WeatherPage(
                    label = "Today",
                    tempLabel = tempC?.let { "${it.toInt()}°" } ?: "--°",
                    condition = condition?.replaceFirstChar { it.uppercase() } ?: "—",
                    iconCode = icon,
                    stats = buildList {
                        humidity?.let { add(Triple("💧", "Humidity", "$it%")) }
                        windKph?.let { add(Triple("💨", "Wind", "$it km/h")) }
                        uvi?.let { add(Triple("☀️", "UV", "${it.toInt()}")) }
                    }
                )
            )
            forecast.drop(1).take(6).forEach { d ->
                val day = java.time.Instant.ofEpochMilli(d.dt)
                    .atZone(java.time.ZoneId.systemDefault())
                    .dayOfWeek
                    .getDisplayName(java.time.format.TextStyle.SHORT, java.util.Locale.getDefault())
                val hi = d.tempMax?.toInt()
                val lo = d.tempMin?.toInt()
                val temp = when {
                    hi != null && lo != null -> "$hi° / $lo°"
                    hi != null -> "$hi°"
                    else -> "--°"
                }
                val stats = buildList<Triple<String, String, String>> {
                    if (hi != null) add(Triple("🔺", "High", "$hi°"))
                    if (lo != null) add(Triple("🔻", "Low", "$lo°"))
                }
                add(
                    WeatherPage(
                        label = day,
                        tempLabel = temp,
                        condition = d.description?.replaceFirstChar { it.uppercase() } ?: "—",
                        iconCode = d.icon,
                        stats = stats
                    )
                )
            }
        }
    }

    val pagerState = androidx.compose.foundation.pager.rememberPagerState(pageCount = { pages.size })

    Column {
        androidx.compose.foundation.pager.HorizontalPager(
            state = pagerState,
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp),
            pageSpacing = 10.dp
        ) { idx ->
            WeatherPageCard(pages[idx])
        }
        Spacer(Modifier.height(8.dp))
        if (pages.size > 1) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(pages.size) { i ->
                    val active = pagerState.currentPage == i
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 2.5.dp)
                            .height(6.dp)
                            .width(if (active) 18.dp else 6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(if (active) PrimaryColor else Color(0xFFCFD8DC))
                    )
                }
            }
        }
    }
}

private fun emojiFromIconCode(code: String?): String = when {
    code == null -> "🌤️"
    code.startsWith("01") -> "☀️"
    code.startsWith("02") -> "🌤️"
    code.startsWith("03") -> "⛅"
    code.startsWith("04") -> "☁️"
    code.startsWith("09") -> "🌧️"
    code.startsWith("10") -> "🌦️"
    code.startsWith("11") -> "⛈️"
    code.startsWith("13") -> "❄️"
    code.startsWith("50") -> "🌫️"
    else -> "🌤️"
}

@Composable
private fun WeatherPageCard(p: WeatherPage) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(emojiFromIconCode(p.iconCode), fontSize = 44.sp)
            Spacer(Modifier.width(10.dp))
            Column {
                Text(
                    text = p.tempLabel,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = p.condition,
                    fontSize = 12.sp,
                    color = TextPrimary.copy(alpha = 0.6f)
                )
                Text(
                    text = p.label,
                    fontSize = 11.sp,
                    color = TextPrimary.copy(alpha = 0.45f)
                )
            }
            if (p.stats.isNotEmpty()) {
                Spacer(Modifier.width(14.dp))
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(52.dp)
                        .background(Color(0xFFE0E0E0))
                )
                Spacer(Modifier.width(14.dp))
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    p.stats.forEach { (i, l, v) ->
                        WeatherStatItem(icon = i, label = l, value = v)
                    }
                }
            }
        }
    }
}

@Composable
private fun WeatherStatItem(icon: String, label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(icon, fontSize = 18.sp)
        Text(
            text = label,
            fontSize = 10.sp,
            color = TextPrimary.copy(alpha = 0.5f)
        )
        Text(
            text = value,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
    }
}

@Composable
private fun MyPlantsSection(plants: List<DemoPlant>, onViewAll: () -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "My Plants",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = "View all",
                fontSize = 13.sp,
                color = PrimaryColor,
                modifier = Modifier.clickable { onViewAll() }
            )
        }
        Spacer(Modifier.height(14.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(plants) { plant ->
                PlantCardItem(plant = plant)
            }
        }
    }
}

@Composable
private fun PlantCardItem(plant: DemoPlant) {
    val statusColor = if (plant.isHealthy) GreenHealthy else OrangeNeeds
    val progressColor = if (plant.isHealthy) ProgressGreen else ProgressRed

    Card(
        modifier = Modifier.width(148.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF0F4F0)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(plant.emoji, fontSize = 52.sp)
                }
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .align(Alignment.TopEnd)
                        .offset(x = (-4).dp, y = 4.dp)
                        .clip(CircleShape)
                        .background(statusColor)
                )
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text = plant.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = plant.status,
                fontSize = 12.sp,
                color = statusColor
            )
            Spacer(Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                LinearProgressIndicator(
                    progress = { plant.health },
                    modifier = Modifier
                        .weight(1f)
                        .height(5.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = progressColor,
                    trackColor = progressColor.copy(alpha = 0.2f)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = "${(plant.health * 100).toInt()}%",
                    fontSize = 11.sp,
                    color = TextPrimary.copy(alpha = 0.55f)
                )
            }
        }
    }
}

@Composable
private fun AiCareTipSection(tip: String, onSeeDetails: () -> Unit) {
    Box(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(TipCardBg)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(CardWhite),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🤖", fontSize = 22.sp)
                    }
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = "AI Care Tip",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    text = tip,
                    fontSize = 13.sp,
                    color = TextPrimary.copy(alpha = 0.72f),
                    lineHeight = 19.sp
                )
                Spacer(Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(HeroGreen)
                        .clickable { onSeeDetails() }
                        .padding(horizontal = 18.dp, vertical = 9.dp)
                ) {
                    Text(
                        text = "See Details",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            Spacer(Modifier.width(8.dp))
            Text("🪴", fontSize = 72.sp)
        }
    }
}

@Composable
private fun TodaysTasksSection(tasks: List<TaskItem>, onViewAll: () -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Today's Tasks",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = "View all",
                fontSize = 13.sp,
                color = PrimaryColor,
                modifier = Modifier.clickable { onViewAll() }
            )
        }
        Spacer(Modifier.height(12.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CardWhite),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column {
                tasks.forEachIndexed { index, task ->
                    TaskRow(task = task)
                    if (index < tasks.size - 1) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .padding(horizontal = 16.dp)
                                .background(Color(0xFFF0F0F0))
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TaskRow(task: TaskItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(task.bgColor),
            contentAlignment = Alignment.Center
        ) {
            Text(task.emoji, fontSize = 20.sp)
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = task.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
            Text(
                text = task.subtitle,
                fontSize = 12.sp,
                color = TextPrimary.copy(alpha = 0.5f)
            )
        }
        Text(
            text = "›",
            color = TextPrimary.copy(alpha = 0.3f),
            fontSize = 22.sp
        )
    }
}

@Composable
private fun GardenOverviewSection(stats: List<GardenStat>) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Garden Overview",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable {}
            ) {
                Text(
                    text = "This week",
                    fontSize = 13.sp,
                    color = PrimaryColor
                )
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = PrimaryColor,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            stats.forEach { stat ->
                GardenStatCard(stat = stat, modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun GardenStatCard(stat: GardenStat, modifier: Modifier = Modifier) {
    val changeColor = if (stat.changePositive) GreenHealthy else OrangeNeeds
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(stat.emoji, fontSize = 28.sp)
            Text(
                text = stat.count.toString(),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = stat.label,
                fontSize = 11.sp,
                color = TextPrimary.copy(alpha = 0.55f)
            )
            Text(
                text = stat.changeText,
                fontSize = 10.sp,
                color = changeColor
            )
        }
    }
}
