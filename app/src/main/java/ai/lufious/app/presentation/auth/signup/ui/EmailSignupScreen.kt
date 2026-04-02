package ai.lufious.app.presentation.auth.signup.ui

import ai.lufious.app.core.theme.Background
import ai.lufious.app.core.utils.BACK_BUTTON_HEIGHT_FRACTION
import ai.lufious.app.core.utils.R
import ai.lufious.app.core.utils.ShadowButton
import ai.lufious.app.core.utils.UiEffect
import ai.lufious.app.core.utils.ValidationResult
import ai.lufious.app.core.utils.hR
import ai.lufious.app.core.utils.rememberResponsiveDimensions
import ai.lufious.app.core.utils.wR
import ai.lufious.app.presentation.auth.signup.viewmodel.SignupEvent
import ai.lufious.app.presentation.auth.signup.viewmodel.SignupViewModel
import ai.lufious.app.presentation.utils.CommonTextField
import android.widget.Toast
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun EmailSignupScreen(
    viewModel: SignupViewModel = hiltViewModel(),
    onNavigate: (String) -> Unit,
    onLogin: () -> Unit,
    onBack: () -> Unit,
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val dimensions = rememberResponsiveDimensions()
    val isDarkTheme = isSystemInDarkTheme()

    LaunchedEffect(Unit) {
        viewModel.effects.collectLatest { effect ->
            when (effect) {
                is UiEffect.ShowError -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }

                is UiEffect.Navigate -> {
                    onNavigate(effect.route)
                }

                else -> {}
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(), containerColor = Background, topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = dimensions.heightFraction(BACK_BUTTON_HEIGHT_FRACTION).dp)
                    .padding(
                        horizontal = dimensions.wR(8f).dp, vertical = dimensions.hR(8f).dp
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                IconButton(
                    onClick = { onBack() },
                    modifier = Modifier.size(32.R(dimensions).dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        modifier = Modifier.size(28.R(dimensions).dp),
                        tint = Color.White,
                    )
                }
                Spacer(modifier = Modifier.width(8.R(dimensions).dp))

                Text(
                    text = "Lufious",
                    fontSize = 28.R(dimensions).sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                )
            }
        }) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    horizontal = dimensions.wR(8f).dp, vertical = dimensions.hR(8f).dp
                ), contentAlignment = Alignment.BottomCenter
        ) {
            Card(
                modifier = Modifier
                    .height(dimensions.heightFraction(0.85f).dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(16.R(dimensions).dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colors.onBackground),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.R(dimensions).dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Create your account",
                            color = Color(0xFFB0AFFF),
                            fontSize = 20.R(dimensions).sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(16.hR(dimensions).dp))

                        CommonTextField(
                            value = state.email,
                            onValueChange = { viewModel.onEvent(SignupEvent.EmailChanged(it)) },
                            responsive = dimensions,
                            placeholder = "Enter your email here",
                            imeAction = ImeAction.Next,
                            onImeAction = { },
                            isError = state.emailValidation is ValidationResult.Invalid
                        )

                        Spacer(modifier = Modifier.height(16.hR(dimensions).dp))

                        CommonTextField(
                            value = state.password,
                            onValueChange = { viewModel.onEvent(SignupEvent.PasswordChanged(it)) },
                            responsive = dimensions,
                            placeholder = "Enter your password",
                            isPassword = true,
                            imeAction = ImeAction.Done,
                            onImeAction = { viewModel.onEvent(SignupEvent.Submit) },
                            isError = state.passwordValidation is ValidationResult.Invalid
                        )
                    }

                    Column {
                        ShadowButton(
                            text = "CREATE ACCOUNT",
                            onClick = { viewModel.onEvent(SignupEvent.Submit) },
                            enabled = state.isSubmitEnabled && !state.isLoading,
                            responsive = dimensions,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.hR(dimensions).dp),
                        )

                        Spacer(modifier = Modifier.height(16.hR(dimensions).dp))

                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Already have an account?",
                                color = Color.Gray,
                                fontSize = 12.R(dimensions).sp
                            )
                            Spacer(modifier = Modifier.width(4.wR(dimensions).dp))
                            TextButton(
                                onClick = onLogin, contentPadding = PaddingValues(0.dp)
                            ) {
                                Text(
                                    text = "Log in here",
                                    color = Color(0xFFB0AFFF),
                                    fontSize = 12.R(dimensions).sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
