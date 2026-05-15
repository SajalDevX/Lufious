@file:Suppress("DEPRECATION")

package ai.lufious.app.presentation.auth.login.ui

import ai.lufious.app.core.utils.LaunchFacebookSignIn
import ai.lufious.app.core.utils.LaunchGoogleSignIn
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ai.lufious.app.core.utils.UiEffect
import ai.lufious.app.presentation.auth.login.viewmodel.LoginEvent
import ai.lufious.app.presentation.auth.login.viewmodel.LoginViewModel
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.res.stringResource
import kotlinx.coroutines.flow.collectLatest
import  ai.lufious.app.R
import ai.lufious.app.core.theme.Background
import ai.lufious.app.core.theme.PrimaryColor
import ai.lufious.app.core.utils.rememberResponsiveDimensions
import androidx.compose.foundation.Image
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import ai.lufious.app.core.theme.TextPrimary
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.zIndex
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

@Composable
@Suppress("DEPRECATION", "UNUSED_PARAMETER")
fun LoginSelectionScreen(
    navigateToHome: () -> Unit,
    onEmailLogin: () -> Unit,
    onSignUp: () -> Unit,
    launchGoogleSignIn: () -> Unit,
    launchFacebookSignIn: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val dims = rememberResponsiveDimensions()

    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestEmail()
        .requestIdToken(stringResource(R.string.default_web_client_id))
        .build()
    val googleClient = GoogleSignIn.getClient(context, gso)

    val googleLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val token = runCatching {
            GoogleSignIn.getSignedInAccountFromIntent(result.data)
                .getResult(ApiException::class.java)
                ?.idToken
        }.getOrNull()
        viewModel.onEvent(LoginEvent.GoogleSignInResult(token ?: ""))
    }

    LaunchedEffect(Unit) {
        viewModel.effects.collectLatest { effect ->
            when (effect) {
                is UiEffect.Navigate -> {
                    Toast.makeText(context, "Login successful!", Toast.LENGTH_SHORT).show()
                    if (effect.route == "home") navigateToHome()
                }

                is UiEffect.ShowError -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_LONG).show()
                }

                is LaunchGoogleSignIn -> {
                    googleClient.signOut().addOnCompleteListener {
                        googleLauncher.launch(googleClient.signInIntent)
                    }
                }

                is LaunchFacebookSignIn -> launchFacebookSignIn()
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .safeDrawingPadding()
    ) {
        Text(
            modifier = Modifier
                .padding(
                    top = dims.heightFraction(0.1f).dp,
                    start = dims.wR(16f).dp,
                    end = dims.wR(16f).dp
                )
                .align(Alignment.TopStart),
            text = buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary.copy(alpha = 0.6f),
                        fontSize = MaterialTheme.typography.h3.fontSize
                    )
                ) {
                    append("Because Your \n")
                }
                withStyle(
                    style = SpanStyle(
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                ) {
                    append("Plants Deserve It")
                }
            },
            textAlign = TextAlign.Start,
            style = MaterialTheme.typography.h3
        )

        Surface(
            shape = RoundedCornerShape(dims.R(16f).dp),
            color = MaterialTheme.colors.onBackground,
            elevation = dims.R(6f).dp,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(
                    horizontal = dims.wR(8f).dp,
                    vertical = dims.hR(8f).dp
                )
                .zIndex(1f)
        ) {
            Column(
                modifier = Modifier
                    .height(dims.heightFraction(0.3f).dp)
                    .fillMaxWidth()
                    .padding(
                        horizontal = dims.R(12f).dp,
                        vertical = dims.R(12f).dp
                    ),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Log Back Into Your Lufious Account",
                    style = MaterialTheme.typography.subtitle1.copy(color = Color.White)
                )

                Spacer(modifier = Modifier.height(dims.hR(12f).dp))

                Button(
                    shape = RoundedCornerShape(dims.R(16f).dp),
                    onClick = { viewModel.onEvent(LoginEvent.GoogleSignInClicked) },
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .height(dims.hR(48f).dp),
                    border = BorderStroke(1.dp, Color(0xFFD9D9D9)),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.White
                    )
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_google),
                        contentDescription = null,
                        tint = Color.Unspecified
                    )
                    Spacer(modifier = Modifier.width(dims.wR(8f).dp))
                    Text(
                        "Continue with Google",
                        style = MaterialTheme.typography.body2.copy(color = TextPrimary, fontWeight = FontWeight.W500)
                    )
                }

                Spacer(modifier = Modifier.height(dims.hR(8f).dp))

                Text(
                    text = "or",
                    style = MaterialTheme.typography.body2.copy(color = Color.Gray)
                )

                Spacer(modifier = Modifier.height(dims.hR(8f).dp))

                Button(
                    shape = RoundedCornerShape(dims.R(16f).dp),
                    onClick = { onEmailLogin() },
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .height(dims.hR(48f).dp),
                    border = BorderStroke(1.dp, Color(0xFFD9D9D9)),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.White
                    ),
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_email),
                        contentDescription = null,
                        tint = TextPrimary
                    )
                    Spacer(modifier = Modifier.width(dims.wR(8f).dp))
                    Text("Continue with email",
                        style = MaterialTheme.typography.body2.copy(color = TextPrimary, fontWeight = FontWeight.W500)
                    )
                }

                Spacer(modifier = Modifier.height(dims.hR(8f).dp))

                Row {
                    Text(
                        text = "Don’t have an account?",
                        style = MaterialTheme.typography.body2.copy(color = Color.LightGray)
                    )
                    Spacer(modifier = Modifier.width(dims.wR(4f).dp))
                    Text(
                        text = "Sign up here",
                        style = MaterialTheme.typography.body2.copy(
                            color = PrimaryColor,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.clickable { onSignUp() }
                    )
                }
            }
        }

        Image(
            painter = painterResource(id = R.drawable.waving_mascot),
            contentDescription = "Mascot",
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(
                    top = dims.heightFraction(0.385f).dp,
                    start = dims.heightFraction(0.19f).dp
                )
                .size(dims.heightFraction(0.4f).dp)
                .zIndex(0f)
        )

    }
}
