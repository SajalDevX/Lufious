package ai.lufious.app.presentation.garden.ui

import ai.lufious.app.core.theme.TextPrimary
import ai.lufious.app.presentation.garden.data.models.PlantModel
import coil.compose.AsyncImage
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val WaterGreen    = Color(0xFF22C55E)
private val HealthyBg2    = Color(0xFFE8F5E8)
private val HealthyText2  = Color(0xFF1A7A3C)
private val NeedsWaterBg2 = Color(0xFFFFF3E0)
private val NeedsWaterTx2 = Color(0xFFEA580C)

@Composable
fun PlantCard(
    plant: PlantModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val daysUntilWater = remember(plant.lastWatered, plant.wateringIntervalDays) {
        val nextWater = plant.lastWatered + plant.wateringIntervalDays * 86_400_000L
        val diff = (nextWater - System.currentTimeMillis()) / 86_400_000L
        diff.toInt().coerceAtLeast(0)
    }
    val needsWater = daysUntilWater == 0 || plant.healthStatus == "warning" || plant.healthStatus == "critical"
    val isHealthy  = !needsWater && plant.healthStatus != "critical"

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column {
            Box(modifier = Modifier.fillMaxWidth()) {
                if (plant.photoUrl.isNotBlank()) {
                    AsyncImage(
                        model = plant.photoUrl,
                        contentDescription = plant.nickname,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                            .background(HealthyBg2),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = plant.nickname.firstOrNull()?.uppercaseChar()?.toString() ?: "🌱",
                            fontSize = 38.sp,
                            fontWeight = FontWeight.Bold,
                            color = HealthyText2
                        )
                    }
                }
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
                        text = if (needsWater) "Needs water" else "Water in ${daysUntilWater}d",
                        fontSize = 10.sp,
                        color = WaterGreen
                    )
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    text = plant.nickname,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = plant.species.ifBlank { plant.locationTag },
                    fontSize = 10.sp,
                    color = TextPrimary.copy(alpha = 0.45f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isHealthy) HealthyBg2 else NeedsWaterBg2)
                        .padding(horizontal = 7.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = if (isHealthy) "Healthy" else "Needs Water",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isHealthy) HealthyText2 else NeedsWaterTx2
                    )
                }
            }
        }
    }
}
