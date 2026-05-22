package ai.lufious.app.presentation.profile.ui

import ai.lufious.app.core.theme.PrimaryColor
import ai.lufious.app.core.theme.TextPrimary
import ai.lufious.app.core.utils.UiEffect
import ai.lufious.app.presentation.profile.viewmodel.ProfileEvent
import ai.lufious.app.presentation.profile.viewmodel.ProfileViewModel
import ai.lufious.app.R
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import kotlinx.coroutines.flow.collectLatest

private val HeroTop = Color(0xFF0A2E18)
private val HeroMid = Color(0xFF124428)
private val HeroBottom = Color(0xFF1F8A4C)
private val NeonGreen = Color(0xFF39FF7A)
private val GlowGreen = Color(0xFF35D26B)
private val MintGreen = Color(0xFFB7F2C8)
private val PageBg = Color(0xFFF2F8F3)
private val CardWhite = Color.White
private val DividerColor = Color(0xFFEAF1E5)
private val IconBgGreen = Color(0xFFE8F5E8)
private val IconBgOrange = Color(0xFFFFF3E0)
private val IconBgBlue = Color(0xFFE3F2FD)
private val IconBgPurple = Color(0xFFF3E5F5)
private val IconBgRed = Color(0xFFFFEBEE)
private val GreenIcon = Color(0xFF1A5C35)
private val OrangeIcon = Color(0xFFEA580C)
private val BlueIcon = Color(0xFF1565C0)
private val PurpleIcon = Color(0xFF7B1FA2)
private val RedIcon = Color(0xFFD32F2F)
private val CardBorder = Color(0xFFE2EBD8)

@Composable
fun ProfileScreen(
    navController: NavController,
    outerNavController: NavController = navController,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effects.collectLatest { effect ->
            when (effect) {
                is UiEffect.Navigate -> {
                    outerNavController.navigate(effect.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
                else -> {}
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(PageBg)
            .testTag("profile_screen")
    ) {
        item {
            ProfileHeroSection(
                displayName = state.displayName,
                email = state.email
            )
        }

        item {
            // Stats card overlapping hero
            Box(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .offset(y = (-30).dp)
            ) {
                StatsCard()
            }
        }

        item {
            Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                StreakBanner()
            }
            Spacer(Modifier.height(24.dp))
        }

        item {
            SectionLabel(text = "Account", modifier = Modifier.padding(horizontal = 20.dp))
            Spacer(Modifier.height(8.dp))
            SettingsCard(
                modifier = Modifier.padding(horizontal = 20.dp),
                items = listOf(
                    SettingItem(
                        icon = Icons.Default.Edit,
                        iconBg = IconBgGreen,
                        iconTint = GreenIcon,
                        title = "Edit Profile",
                        subtitle = "Update your name and photo"
                    ),
                    SettingItem(
                        icon = Icons.Default.Notifications,
                        iconBg = IconBgOrange,
                        iconTint = OrangeIcon,
                        title = "Notifications",
                        subtitle = "Watering reminders, tips"
                    ),
                    SettingItem(
                        icon = Icons.Default.WaterDrop,
                        iconBg = IconBgBlue,
                        iconTint = BlueIcon,
                        title = "Watering Schedule",
                        subtitle = "Set care reminders"
                    )
                )
            )
        }

        item {
            Spacer(Modifier.height(20.dp))
            SectionLabel(text = "Preferences", modifier = Modifier.padding(horizontal = 20.dp))
            Spacer(Modifier.height(8.dp))
            SettingsCard(
                modifier = Modifier.padding(horizontal = 20.dp),
                items = listOf(
                    SettingItem(
                        icon = Icons.Default.Palette,
                        iconBg = IconBgPurple,
                        iconTint = PurpleIcon,
                        title = "App Theme",
                        subtitle = "Light / Dark"
                    ),
                    SettingItem(
                        icon = Icons.Default.Language,
                        iconBg = IconBgBlue,
                        iconTint = BlueIcon,
                        title = "Language",
                        subtitle = "English"
                    )
                )
            )
        }

        item {
            Spacer(Modifier.height(20.dp))
            SectionLabel(text = "Support", modifier = Modifier.padding(horizontal = 20.dp))
            Spacer(Modifier.height(8.dp))
            SettingsCard(
                modifier = Modifier.padding(horizontal = 20.dp),
                items = listOf(
                    SettingItem(
                        icon = Icons.AutoMirrored.Filled.HelpOutline,
                        iconBg = IconBgGreen,
                        iconTint = GreenIcon,
                        title = "Help & Support",
                        subtitle = "FAQs and contact us"
                    ),
                    SettingItem(
                        icon = Icons.Default.Star,
                        iconBg = IconBgOrange,
                        iconTint = OrangeIcon,
                        title = "Rate the App",
                        subtitle = "Share your feedback"
                    ),
                    SettingItem(
                        icon = Icons.Default.PrivacyTip,
                        iconBg = IconBgBlue,
                        iconTint = BlueIcon,
                        title = "Privacy Policy",
                        subtitle = "How we use your data"
                    )
                )
            )
        }

        item {
            Spacer(Modifier.height(32.dp))
            Box(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .fillMaxWidth()
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(16.dp),
                        ambientColor = Color(0x44D32F2F),
                        spotColor = Color(0x44D32F2F)
                    )
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .border(BorderStroke(1.5.dp, RedIcon.copy(alpha = 0.35f)), RoundedCornerShape(16.dp))
                    .clickable(enabled = !state.isLoading) {
                        viewModel.onEvent(ProfileEvent.Logout)
                    }
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        IconBgRed,
                                        RedIcon.copy(alpha = 0.22f)
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (state.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = RedIcon,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Logout,
                                contentDescription = null,
                                tint = RedIcon,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    Spacer(Modifier.width(12.dp))
                    Text(
                        text = "Sign Out",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = RedIcon
                    )
                }
            }
            Spacer(Modifier.height(24.dp))
            Text(
                text = "Lufious • v1.0",
                fontSize = 11.sp,
                color = TextPrimary.copy(alpha = 0.35f),
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@Composable
private fun ProfileHeroSection(
    displayName: String,
    email: String
) {
    val pulse by rememberInfiniteTransition(label = "avatarPulse").animateFloat(
        initialValue = 0.92f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500),
            repeatMode = RepeatMode.Reverse
        ),
        label = "avatarScale"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
            .background(
                Brush.verticalGradient(listOf(HeroTop, HeroMid, HeroBottom))
            )
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color(0x5535D26B), Color.Transparent),
                        radius = 700f
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(bottom = 56.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.12f))
                        .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.2f)), CircleShape)
                        .clickable {},
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(Modifier.weight(1f))
                Text(
                    text = "My Profile",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.weight(1f))
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.12f))
                        .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.2f)), CircleShape)
                        .clickable {},
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit profile",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(Modifier.height(10.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Box(
                        modifier = Modifier
                            .size(122.dp)
                            .scale(pulse)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        Color(0x9939FF7A),
                                        Color(0x4435D26B),
                                        Color.Transparent
                                    )
                                )
                            )
                    )
                    Box(
                        modifier = Modifier
                            .size(98.dp)
                            .shadow(elevation = 18.dp, shape = CircleShape, ambientColor = NeonGreen, spotColor = NeonGreen)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(Color(0xFF7DF7A8), Color(0xFF1F8A4C), Color(0xFF0A4E22))
                                )
                            )
                            .border(BorderStroke(3.dp, Color.White), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = displayName.firstOrNull()?.uppercaseChar()?.toString() ?: "G",
                            fontSize = 40.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    }
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(32.dp)
                            .shadow(elevation = 6.dp, shape = CircleShape, ambientColor = Color(0x88000000))
                            .clip(CircleShape)
                            .background(Color.White)
                            .border(BorderStroke(2.dp, HeroMid), CircleShape)
                            .clickable {},
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Change photo",
                            tint = HeroMid,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))
                Text(
                    text = displayName.ifBlank { "Plant Lover" },
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = email.ifBlank { " " },
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 12.sp
                )

                Spacer(Modifier.height(14.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    HeroChip(emoji = "🌱", label = "Plant Parent")
                    Spacer(Modifier.width(8.dp))
                    HeroChip(emoji = "⚡", label = "Level 3")
                    Spacer(Modifier.width(8.dp))
                    HeroChip(emoji = "🏆", label = "Pro")
                }
            }
        }
    }
}

@Composable
private fun HeroChip(emoji: String, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White.copy(alpha = 0.14f))
            .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.22f)), RoundedCornerShape(20.dp))
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Text(text = emoji, fontSize = 12.sp)
        Spacer(Modifier.width(5.dp))
        Text(
            text = label,
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun StatsCard() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 14.dp, shape = RoundedCornerShape(22.dp), ambientColor = Color(0x4435D26B), spotColor = Color(0x6635D26B))
            .clip(RoundedCornerShape(22.dp))
            .background(CardWhite)
            .border(BorderStroke(1.dp, CardBorder), RoundedCornerShape(22.dp))
            .padding(vertical = 18.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem(value = "12", label = "Plants", emoji = "🌿", accent = Color(0xFF35D26B))
            StatDivider()
            StatItem(value = "7", label = "Day Streak", emoji = "🔥", accent = Color(0xFFEA580C))
            StatDivider()
            StatItem(value = "34", label = "Watered", emoji = "💧", accent = Color(0xFF1565C0))
        }
    }
}

@Composable
private fun StreakBanner() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 14.dp, shape = RoundedCornerShape(20.dp), ambientColor = Color(0x6635D26B), spotColor = Color(0x8835D26B))
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0xFF1F8A4C), Color(0xFF0A4E22))
                )
            )
            .border(BorderStroke(1.5.dp, NeonGreen.copy(alpha = 0.45f)), RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.toon_assistant_avatar),
                    contentDescription = null,
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "7-day streak!",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(text = "🔥", fontSize = 16.sp)
                }
                Spacer(Modifier.height(2.dp))
                Text(
                    text = "Keep watering your plants daily",
                    color = Color.White.copy(alpha = 0.82f),
                    fontSize = 11.sp
                )
                Spacer(Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(Color.White.copy(alpha = 0.2f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.7f)
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(NeonGreen, MintGreen)
                                )
                            )
                    )
                }
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "210 / 300 XP to Level 4",
                    color = Color.White.copy(alpha = 0.75f),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun StatItem(value: String, label: String, emoji: String, accent: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            accent.copy(alpha = 0.22f),
                            accent.copy(alpha = 0.10f)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(emoji, fontSize = 20.sp)
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = value,
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            color = TextPrimary
        )
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = TextPrimary.copy(alpha = 0.55f)
        )
    }
}

@Composable
private fun StatDivider() {
    Box(
        modifier = Modifier
            .width(1.dp)
            .height(48.dp)
            .background(DividerColor)
    )
}

@Composable
private fun SectionLabel(text: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(width = 3.dp, height = 14.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(NeonGreen, GlowGreen)
                    )
                )
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = text.uppercase(),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary.copy(alpha = 0.5f),
            letterSpacing = 1.2.sp
        )
    }
}

private data class SettingItem(
    val icon: ImageVector,
    val iconBg: Color,
    val iconTint: Color,
    val title: String,
    val subtitle: String,
    val trailingContent: @Composable (() -> Unit)? = null
)

@Composable
private fun SettingsCard(items: List<SettingItem>, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 8.dp, shape = RoundedCornerShape(18.dp), ambientColor = Color(0x3335D26B), spotColor = Color(0x4435D26B))
            .clip(RoundedCornerShape(18.dp))
            .background(CardWhite)
            .border(BorderStroke(1.dp, CardBorder), RoundedCornerShape(18.dp))
    ) {
        Column {
            items.forEachIndexed { index, item ->
                SettingRow(item = item)
                if (index < items.size - 1) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 68.dp)
                            .height(1.dp)
                            .background(DividerColor)
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingRow(item: SettingItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {}
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .shadow(
                    elevation = 4.dp,
                    shape = RoundedCornerShape(14.dp),
                    ambientColor = item.iconTint.copy(alpha = 0.4f),
                    spotColor = item.iconTint.copy(alpha = 0.4f)
                )
                .clip(RoundedCornerShape(14.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            item.iconBg,
                            item.iconTint.copy(alpha = 0.18f)
                        )
                    )
                )
                .border(BorderStroke(1.dp, item.iconTint.copy(alpha = 0.25f)), RoundedCornerShape(14.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = null,
                tint = item.iconTint,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = item.subtitle,
                fontSize = 11.sp,
                color = TextPrimary.copy(alpha = 0.50f)
            )
        }
        if (item.trailingContent != null) {
            item.trailingContent.invoke()
        } else {
            Box(
                modifier = Modifier
                    .size(26.dp)
                    .clip(CircleShape)
                    .background(PageBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = TextPrimary.copy(alpha = 0.45f),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
