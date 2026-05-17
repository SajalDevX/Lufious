package ai.lufious.app.presentation.profile.ui

import ai.lufious.app.core.theme.PrimaryColor
import ai.lufious.app.core.theme.TextPrimary
import ai.lufious.app.core.utils.UiEffect
import ai.lufious.app.presentation.profile.viewmodel.ProfileEvent
import ai.lufious.app.presentation.profile.viewmodel.ProfileViewModel
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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

private val HeroGreenDeep = Color(0xFF124428)
private val HeroGreen = Color(0xFF1A5C35)
private val PageBg = Color(0xFFF2F8F3)
private val CardWhite = Color.White
private val DividerColor = Color(0xFFF0F0F0)
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

@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effects.collectLatest { effect ->
            when (effect) {
                is UiEffect.Navigate -> {
                    navController.navigate(effect.route) {
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
                email = state.email,
                onBackClick = { navController.popBackStack() }
            )
        }

        item {
            // Stats card overlapping hero
            Box(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .offset(y = (-24).dp)
            ) {
                StatsCard()
            }
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
            Spacer(Modifier.height(28.dp))
            // Sign out button
            Box(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFFFEBEE))
                    .clickable(enabled = !state.isLoading) {
                        viewModel.onEvent(ProfileEvent.Logout)
                    }
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(IconBgRed),
                        contentAlignment = Alignment.Center
                    ) {
                        if (state.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = RedIcon,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Logout,
                                contentDescription = null,
                                tint = RedIcon,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(Modifier.width(14.dp))
                    Text(
                        text = "Sign Out",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = RedIcon
                    )
                }
            }
            Spacer(Modifier.height(40.dp))
        }
    }
}

@Composable
private fun ProfileHeroSection(
    displayName: String,
    email: String,
    onBackClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(listOf(HeroGreenDeep, HeroGreen))
            )
            .statusBarsPadding()
            .padding(bottom = 48.dp)
    ) {
        // Back button
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 16.dp, top = 14.dp)
                .size(38.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.15f))
                .clickable { onBackClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 64.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Avatar
            Box(contentAlignment = Alignment.BottomEnd) {
                Box(
                    modifier = Modifier
                        .size(88.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFF22C55E), Color(0xFF15803D))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = displayName.firstOrNull()?.uppercaseChar()?.toString() ?: "G",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Change photo",
                        tint = HeroGreen,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            Spacer(Modifier.height(14.dp))
            Text(
                text = displayName.ifBlank { "Plant Lover" },
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = email.ifBlank { "" },
                color = Color.White.copy(alpha = 0.72f),
                fontSize = 13.sp
            )
            Spacer(Modifier.height(16.dp))

            // Edit profile pill button
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White.copy(alpha = 0.18f))
                    .clickable {}
                    .padding(horizontal = 20.dp, vertical = 7.dp)
            ) {
                Text(
                    text = "Edit Profile",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun StatsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem(value = "12", label = "Plants", emoji = "🌿")
            StatDivider()
            StatItem(value = "7", label = "Day Streak", emoji = "🔥")
            StatDivider()
            StatItem(value = "34", label = "Watered", emoji = "💧")
        }
    }
}

@Composable
private fun StatItem(value: String, label: String, emoji: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(emoji, fontSize = 22.sp)
        Spacer(Modifier.height(4.dp))
        Text(
            text = value,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Text(
            text = label,
            fontSize = 11.sp,
            color = TextPrimary.copy(alpha = 0.5f)
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
    Text(
        text = text,
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
        color = TextPrimary.copy(alpha = 0.45f),
        modifier = modifier
    )
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
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            items.forEachIndexed { index, item ->
                SettingRow(item = item)
                if (index < items.size - 1) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .padding(start = 68.dp)
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
            .padding(horizontal = 16.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(item.iconBg),
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
            Text(
                text = item.subtitle,
                fontSize = 11.sp,
                color = TextPrimary.copy(alpha = 0.50f)
            )
        }
        if (item.trailingContent != null) {
            item.trailingContent.invoke()
        } else {
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = TextPrimary.copy(alpha = 0.30f),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
