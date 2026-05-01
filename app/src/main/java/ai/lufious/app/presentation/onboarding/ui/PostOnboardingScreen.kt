package ai.lufious.app.presentation.onboarding.ui

import ai.lufious.app.core.notifications.NotificationScheduler
import ai.lufious.app.core.theme.Background
import ai.lufious.app.core.theme.LimeAccent
import ai.lufious.app.core.theme.PrimaryColor
import ai.lufious.app.core.utils.MAIN_GRAPH
import ai.lufious.app.core.utils.UiEffect
import ai.lufious.app.presentation.onboarding.viewmodel.PostOnboardingEvent
import ai.lufious.app.presentation.onboarding.viewmodel.PostOnboardingViewModel
import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

private data class OnboardStep(
    val emoji: String,
    val title: String,
    val subtitle: String,
    val cta: String
)

private val steps = listOf(
    OnboardStep(
        emoji = "📍",
        title = "Allow Location",
        subtitle = "We use your location to give weather-adjusted care tips for your plants.",
        cta = "Allow Location"
    ),
    OnboardStep(
        emoji = "🌱",
        title = "Add Your First Plant",
        subtitle = "Scan a plant with your camera or add one manually from the Garden tab.",
        cta = "Got it"
    ),
    OnboardStep(
        emoji = "🔔",
        title = "Stay on Track",
        subtitle = "Get daily reminders so you never miss a watering or care task.",
        cta = "Enable Notifications"
    )
)

@Composable
fun PostOnboardingScreen(
    navController: NavController,
    viewModel: PostOnboardingViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    val locationPermLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { viewModel.onEvent(PostOnboardingEvent.NextStep) }

    val notifPermLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        NotificationScheduler.scheduleDailyCarePlan(context)
        viewModel.onEvent(PostOnboardingEvent.Complete)
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

    val step = steps[state.currentStep]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF1E3D1E), Background)
                )
            )
            .padding(horizontal = 28.dp, vertical = 32.dp)
    ) {
        // Progress dots
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            steps.indices.forEach { i ->
                val active = i == state.currentStep
                val done = i < state.currentStep
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .height(6.dp)
                        .width(if (active) 28.dp else 18.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(
                            when {
                                active -> PrimaryColor
                                done -> PrimaryColor.copy(alpha = 0.5f)
                                else -> Color.White.copy(alpha = 0.15f)
                            }
                        )
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Step ${state.currentStep + 1} of ${steps.size}",
            color = Color.White.copy(alpha = 0.5f),
            fontSize = 12.sp,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Hero emoji circle
        Box(
            modifier = Modifier
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(180.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(
                                PrimaryColor.copy(alpha = 0.30f),
                                LimeAccent.copy(alpha = 0.05f),
                                Color.Transparent
                            )
                        )
                    )
                    .border(
                        width = 1.dp,
                        color = PrimaryColor.copy(alpha = 0.25f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(text = step.emoji, fontSize = 84.sp)
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = step.title,
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = step.subtitle,
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 15.sp,
            textAlign = TextAlign.Center,
            lineHeight = 22.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                when (state.currentStep) {
                    0 -> locationPermLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                    1 -> viewModel.onEvent(PostOnboardingEvent.NextStep)
                    2 -> {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            notifPermLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        } else {
                            NotificationScheduler.scheduleDailyCarePlan(context)
                            viewModel.onEvent(PostOnboardingEvent.Complete)
                        }
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor)
        ) {
            Text(
                text = step.cta,
                color = Color.White,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 15.sp,
                letterSpacing = 0.5.sp
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    if (state.currentStep < steps.size - 1) {
                        viewModel.onEvent(PostOnboardingEvent.NextStep)
                    } else {
                        viewModel.onEvent(PostOnboardingEvent.Complete)
                    }
                }
                .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (state.currentStep < steps.size - 1) "Skip for now" else "Maybe later",
                color = Color.White.copy(alpha = 0.55f),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
