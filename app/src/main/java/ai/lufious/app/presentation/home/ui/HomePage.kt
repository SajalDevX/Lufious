package ai.lufious.app.presentation.home.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalTime

@Composable
fun HomePage(modifier: Modifier = Modifier) {
    val greeting = remember {
        when (LocalTime.now().hour) {
            in 5..11 -> "Good morning"
            in 12..17 -> "Good afternoon"
            else -> "Good evening"
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 32.dp)
            .testTag("home_screen"),
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "$greeting 🌿",
            color = Color.White,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Let's take care of your plants",
            color = Color.White.copy(alpha = 0.65f),
            fontSize = 14.sp
        )
        Spacer(modifier = Modifier.height(40.dp))
        Text(
            text = "Your dashboard is on its way.",
            color = Color.White.copy(alpha = 0.45f),
            fontSize = 14.sp
        )
    }
}
