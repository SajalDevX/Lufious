@file:Suppress("DEPRECATION")

package ai.lufious.app.presentation.auth.login.ui

import ai.lufious.app.core.theme.Background
import ai.lufious.app.core.utils.BACK_BUTTON_HEIGHT_FRACTION
import ai.lufious.app.core.utils.R
import ai.lufious.app.core.utils.Screen
import ai.lufious.app.core.utils.ShadowButton
import ai.lufious.app.core.utils.UiEffect
import ai.lufious.app.core.utils.ValidationResult
import ai.lufious.app.core.utils.hR
import ai.lufious.app.core.utils.rememberResponsiveDimensions
import ai.lufious.app.core.utils.wR
import ai.lufious.app.presentation.auth.login.viewmodel.LoginEvent
import ai.lufious.app.presentation.auth.login.viewmodel.LoginViewModel
import ai.lufious.app.presentation.utils.CommonTextField
import android.widget.Toast
import androidx.compose.foundation.border
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
import androidx.compose.foundation.text.KeyboardActionScope
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
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
import ai.lufious.app.core.theme.TextPrimary
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.flow.collectLatest


@Composable
fun EmailLoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    onNavigate: (String) -> Unit,
    onSignUp: () -> Unit,
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val dimensions = rememberResponsiveDimensions()

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
                    onClick = { onNavigate(Screen.Onboarding.route) },
                    modifier = Modifier.size(32.R(dimensions).dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        modifier = Modifier.size(28.R(dimensions).dp),
                        tint = TextPrimary,
                    )
                }
                Spacer(modifier = Modifier.width(8.R(dimensions).dp))

                Text(
                    text = "Lufious",
                    fontSize = 28.R(dimensions).sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                )
            }
        }) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
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
                            text = "Let’s get started!",
                            color = Color(0xFFB0AFFF),
                            fontSize = 20.R(dimensions).sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(16.hR(dimensions).dp))

                        CommonTextField(
                            value = state.email,
                            onValueChange = { viewModel.onEvent(LoginEvent.EmailChanged(it)) },
                            responsive = dimensions,
                            placeholder = "Enter your email here",
                            imeAction = ImeAction.Next,
                            onImeAction = { /* Focus will automatically move to next */ },
                            isError = state.emailValidation is ValidationResult.Invalid
                        )

                        Spacer(modifier = Modifier.height(16.hR(dimensions).dp))

                        CommonTextField(
                            value = state.password,
                            onValueChange = { viewModel.onEvent(LoginEvent.PasswordChanged(it)) },
                            responsive = dimensions,
                            placeholder = "Enter your password",
                            isPassword = true,
                            imeAction = ImeAction.Done,
                            onImeAction = { viewModel.onEvent(LoginEvent.Submit) },
                            isError = state.passwordValidation is ValidationResult.Invalid
                        )


                    }

                    Column {
                        ShadowButton(
                            text = "CONTINUE",
                            onClick = { viewModel.onEvent(LoginEvent.Submit) },
                            enabled = state.isSubmitEnabled && !state.isLoading,
                            responsive = dimensions,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.hR(dimensions).dp),

//                            onClick = { viewModel.onEvent(LoginEvent.Submit) },
//                            enabled = state.isSubmitEnabled && !state.isLoading,
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .height(48.hR(dimensions).dp),

                        )
//                        {
//                            if (state.isLoading) {
//                                CircularProgressIndicator(
//                                    modifier = Modifier.size(24.R(dimensions).dp),
//                                    color = TextPrimary,
//                                    strokeWidth = 2.dp
//                                )
//                            } else {
//                                Text(
//                                    text = "CONTINUE",
//                                    fontSize = 14.R(dimensions).sp
//                                )
//                            }
//                        }

                        Spacer(modifier = Modifier.height(16.hR(dimensions).dp))

                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Don’t have an account?",
                                color = Color.Gray,
                                fontSize = 12.R(dimensions).sp
                            )
                            Spacer(modifier = Modifier.width(4.wR(dimensions).dp))
                            TextButton(
                                onClick = onSignUp, contentPadding = PaddingValues(0.dp)
                            ) {
                                Text(
                                    text = "Sign up here",
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
