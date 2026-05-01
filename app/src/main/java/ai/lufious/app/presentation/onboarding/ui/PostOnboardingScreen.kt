package ai.lufious.app.presentation.onboarding.ui

import ai.lufious.app.core.notifications.NotificationScheduler
import ai.lufious.app.core.theme.Background
import ai.lufious.app.core.theme.PrimaryColor
import ai.lufious.app.core.utils.MAIN_GRAPH
import ai.lufious.app.core.utils.UiEffect
import ai.lufious.app.presentation.onboarding.viewmodel.PostOnboardingEvent
import ai.lufious.app.presentation.onboarding.viewmodel.PostOnboardingViewModel
import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

private val steps = listOf(
    Triple(
        "Allow Location",
        "We use your location to give weather-adjusted care tips for your plants.",
        "Allow"
    ),
    Triple(
        "Add Your First Plant",
        "Scan a plant with your camera or add one manually from the Garden tab.",
        "Got it"
    ),
    Triple(
        "Stay on Track",
        "Get daily reminders so you never miss a watering or care task.",
        "Enable Notifications"
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

    val (title, subtitle, ctaLabel) = steps[state.currentStep]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "${state.currentStep + 1} / ${steps.size}",
            color = Color.White.copy(alpha = 0.5f),
            fontSize = 13.sp
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = title,
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = subtitle,
            color = Color.White.copy(alpha = 0.72f),
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            lineHeight = 24.sp
        )
        Spacer(modifier = Modifier.height(48.dp))
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
                .height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor)
        ) {
            Text(
                text = ctaLabel,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedButton(
            onClick = {
                if (state.currentStep < steps.size - 1) {
                    viewModel.onEvent(PostOnboardingEvent.NextStep)
                } else {
                    viewModel.onEvent(PostOnboardingEvent.Complete)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text(text = "Skip", color = Color.White.copy(alpha = 0.7f), fontSize = 15.sp)
        }
    }
}
