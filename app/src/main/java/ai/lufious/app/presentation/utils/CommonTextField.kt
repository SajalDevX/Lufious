package ai.lufious.app.presentation.utils

import androidx.compose.foundation.border
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import ai.lufious.app.core.utils.ResponsiveDimensions
import ai.lufious.app.core.utils.R
import ai.lufious.app.core.utils.wR
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.ui.focus.FocusDirection

@Composable
fun CommonTextField(
    value: String,
    onValueChange: (String) -> Unit,
    responsive: ResponsiveDimensions,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    isPassword: Boolean = false,
    isError: Boolean = false,
    enabled: Boolean = true,
    textColor: Color = Color.White,
    placeholderColor: Color = Color.White.copy(alpha = 0.5f),
    backgroundColor: Color = Color.White.copy(alpha = 0.1f),
    borderColor: Color = Color.White.copy(alpha = 0.5f),
    cursorColor: Color = Color.White,
    imeAction: ImeAction = ImeAction.Done,
    onImeAction: (() -> Unit)? = null,
    singleLine: Boolean = true,
    textStyle: TextStyle = TextStyle.Default
) {
    val focusManager = LocalFocusManager.current
    var passwordVisible by remember { mutableStateOf(false) }

    val radius = 8.R(responsive).dp
    val borderWidth = 1.R(responsive).dp
    val iconSize = 20.R(responsive).dp
    val iconPadding = 8.wR(responsive).dp

    TextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = singleLine,
        enabled = enabled,
        visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
        placeholder = {
            Text(
                placeholder,
                style = textStyle.copy(color = placeholderColor)
            )
        },
        trailingIcon = {
            if (isPassword) {
                val icon = if (!passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility
                val description = if (passwordVisible) "Hide password" else "Show password"
                IconButton(
                    onClick = { passwordVisible = !passwordVisible },
                    modifier = Modifier.padding(end = iconPadding)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = description,
                        tint = placeholderColor,
                        modifier = Modifier.size(iconSize)
                    )
                }
            }
        },
        textStyle = textStyle.copy(color = textColor),
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = borderWidth,
                color = if (isError) MaterialTheme.colors.error else borderColor,
                shape = RoundedCornerShape(radius)
            )
            .background(backgroundColor, shape = RoundedCornerShape(radius)),
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = imeAction),
        keyboardActions = KeyboardActions(
            onNext = { onImeAction?.invoke() ?: focusManager.moveFocus(FocusDirection.Down) },
            onDone = { onImeAction?.invoke() ?: focusManager.clearFocus() }
        ),
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = Color.Transparent,
            cursorColor = cursorColor,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        )
    )
}

