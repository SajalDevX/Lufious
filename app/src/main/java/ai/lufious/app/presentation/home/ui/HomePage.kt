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
        tabNavController?.navigate(route) {
            popUpTo(Screen.HomeTab.route)
            launchSingleTop = true
        }
    }

    val demoPlants = listOf(
        DemoPlant("Monstera", "🌿", "Healthy", true, 0.80f),
        DemoPlant("Aloe Vera", "🌵", "Needs water", false, 0.35f),
        DemoPlant("Rose", "🌹", "Healthy", true, 0.75f),
        DemoPlant("Snake Plant", "🌱", "Needs light", false, 0.20f)
    )

    val tasks = listOf(
        TaskItem("Water 2 plants", "Monstera, Aloe Vera", "💧", Color(0xFFE3F2FD)),
        TaskItem("Fertilize 1 plant", "Rose", "🌿", Color(0xFFFFF8E1)),
        TaskItem("Check sunlight", "3 plants", "☀️", Color(0xFFFFFDE7))
    )

    val gardenStats = listOf(
        GardenStat("🌿", maxOf(state.totalPlants, 12), "Healthy", "+2 from last week", true),
        GardenStat("💧", maxOf(state.plantsNeedingWater.size, 2), "Need care", "+1 from last week", false),
        GardenStat("🌱", 4, "New leaves", "+3 from last week", true)
    )

    val aiTip = state.aiTip ?: "Your Monstera looks a bit dry.\nWater it today for healthy growth."
    val needsWaterCount = state.plantsNeedingWater.size.coerceAtLeast(3)

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
                condition = state.currentCondition
            )
        }
        item {
            Spacer(Modifier.height(20.dp))
            MyPlantsSection(
                plants = demoPlants,
                onViewAll = { goTab(Screen.GardenTab.route) }
            )
        }
        item {
            Spacer(Modifier.height(16.dp))
            AiCareTipSection(
                tip = aiTip,
                onSeeDetails = { goTab(Screen.ScanTab.route) }
            )
        }
        item {
            Spacer(Modifier.height(20.dp))
            TodaysTasksSection(
                tasks = tasks,
                onViewAll = { goTab(Screen.GardenTab.route) }
            )
        }
        item {
            Spacer(Modifier.height(20.dp))
            GardenOverviewSection(stats = gardenStats)
        }
        item { Spacer(Modifier.height(32.dp)) }
    }
}

@Composable
private fun HeroAndWeatherSection(
    greeting: String,
    needsWaterCount: Int,
    tempC: Double?,
    condition: String?
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
            // Notification bell
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.15f))
                    .clickable {},
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notifications",
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .align(Alignment.TopEnd)
                        .offset(x = (-2).dp, y = 2.dp)
                        .clip(CircleShape)
                        .background(HeroDotRed)
                )
            }

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

        // Weather card overlapping the hero bottom
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .offset(y = (-22).dp)
        ) {
            WeatherCard(tempC = tempC, condition = condition)
        }
    }
}

@Composable
private fun WeatherCard(tempC: Double?, condition: String?) {
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
            Text("☀️", fontSize = 44.sp)
            Spacer(Modifier.width(10.dp))
            Column {
                Text(
                    text = tempC?.let { "${it.toInt()}°" } ?: "28°",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = condition?.replaceFirstChar { it.uppercase() } ?: "Sunny",
                    fontSize = 12.sp,
                    color = TextPrimary.copy(alpha = 0.6f)
                )
                Text(
                    text = "New York",
                    fontSize = 11.sp,
                    color = TextPrimary.copy(alpha = 0.45f)
                )
            }
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
                WeatherStatItem(icon = "💧", label = "Humidity", value = "60%")
                WeatherStatItem(icon = "🌤️", label = "Sunlight", value = "6 hrs")
                WeatherStatItem(icon = "💨", label = "Wind", value = "12 km/h")
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
