package ai.lufious.app.presentation.scan.ui

import ai.lufious.app.R
import ai.lufious.app.core.theme.TextPrimary
import ai.lufious.app.presentation.scan.data.models.AiChatMessageModel
import ai.lufious.app.presentation.scan.viewmodel.AiChatEvent
import ai.lufious.app.presentation.scan.viewmodel.AiChatState
import ai.lufious.app.presentation.scan.viewmodel.AiChatViewModel
import ai.lufious.app.presentation.scan.viewmodel.QuickReply
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val HeaderTop = Color(0xFF0E2A1B)
private val HeaderMid = Color(0xFF154A2C)
private val HeaderBottom = Color(0xFF1E6B3E)
private val Brand = Color(0xFF1F8A4C)
private val BrandAccent = Color(0xFF35B461)
private val BrandSoft = Color(0xFF43A958)
private val AssistantBubble = Color(0xFFFFFFFF)
private val UserBubbleTop = Color(0xFF2BB063)
private val UserBubbleBottom = Color(0xFF0F6A3A)
private val SheetBg = Color(0xFFF4F8F2)
private val ChipBg = Color(0xFFEAF6E2)
private val Border = Color(0xFFE2EBD8)
private val Hint = Color(0xFF8FA192)
private val DropBlue = Color(0xFF61A8E8)
private val SunYellow = Color(0xFFF1B43D)
private val LeafGreen = Color(0xFF6FBE3C)
private val OnlineDot = Color(0xFF35D26B)
private val GlassWhite = Color(0x33FFFFFF)
private val GlassStroke = Color(0x55FFFFFF)

@Composable
fun AiChatScreen(
    navController: NavController,
    viewModel: AiChatViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val listState = rememberLazyListState()

    val firstAssistantIndex = state.messages.indexOfFirst { it.role == "assistant" }
    val showQuickReplies = state.messages.isNotEmpty() &&
        !state.isReplying &&
        state.messages.last().role == "assistant" &&
        state.inputText.isBlank()

    LaunchedEffect(state.messages.size, state.isReplying) {
        if (state.messages.isNotEmpty()) {
            listState.animateScrollToItem(state.messages.size - 1 + if (state.isReplying) 1 else 0)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SheetBg)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            ChatHeroHeader(
                species = state.speciesName.ifBlank { "Plant assistant" },
                commonName = state.commonName,
                healthStatus = state.healthStatus,
                isReplying = state.isReplying,
                onBack = { navController.popBackStack() }
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                    .background(SheetBg)
            ) {

                if (state.messages.isEmpty() && !state.isReplying) {
                    AnalysingState()
                } else {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        itemsIndexed(state.messages, key = { _, m -> m.timestamp }) { index, msg ->
                            val attachPhoto = index == firstAssistantIndex && state.plantPhotoUrl.isNotBlank()
                            MessageItem(
                                msg = msg,
                                plantPhotoUrl = if (attachPhoto) state.plantPhotoUrl else "",
                                highlightTerms = listOfNotNull(
                                    state.speciesName.takeIf { it.isNotBlank() },
                                    state.commonName.takeIf { it.isNotBlank() }
                                )
                            )
                        }

                        if (state.isReplying) {
                            item { TypingBubble() }
                        }

                        if (showQuickReplies) {
                            item {
                                AnimatedVisibility(
                                    visible = true,
                                    enter = fadeIn(),
                                    exit = fadeOut()
                                ) {
                                    QuickReplyRow(
                                        replies = state.quickReplies,
                                        onTap = { viewModel.onEvent(AiChatEvent.QuickReplyTapped(it)) }
                                    )
                                }
                            }
                        }
                    }
                }

                ChatComposer(
                    value = state.inputText,
                    enabled = !state.isReplying && state.messages.isNotEmpty(),
                    onValueChange = { viewModel.onEvent(AiChatEvent.InputChanged(it)) },
                    onSend = { viewModel.onEvent(AiChatEvent.Send) }
                )
            }
        }
    }
}

@Composable
private fun ChatHeroHeader(
    species: String,
    commonName: String,
    healthStatus: String,
    isReplying: Boolean,
    onBack: () -> Unit
) {
    val (statusLabel, statusColor) = healthPill(healthStatus)
    val infiniteTransition = rememberInfiniteTransition(label = "headerPulse")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(HeaderTop, HeaderMid, HeaderBottom)
                )
            )
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color(0x4035D26B), Color.Transparent),
                        radius = 520f
                    )
                )
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 12.dp)
                .padding(top = 6.dp, bottom = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(GlassWhite)
                    .border(BorderStroke(1.dp, GlassStroke), CircleShape)
                    .clickable { onBack() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(OnlineDot)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "CatPill AI",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0x3335D26B))
                            .border(BorderStroke(1.dp, Color(0x6635D26B)), RoundedCornerShape(8.dp))
                            .padding(horizontal = 5.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = "BETA",
                            color = Color(0xFFB7F2C8),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Text(
                    text = if (isReplying) "thinking…" else "your plant assistant",
                    color = Color.White.copy(alpha = 0.75f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(GlassWhite)
                    .border(BorderStroke(1.dp, GlassStroke), RoundedCornerShape(20.dp))
                    .padding(horizontal = 8.dp, vertical = 5.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .scale(pulse)
                        .clip(CircleShape)
                        .background(statusColor)
                )
                Spacer(Modifier.width(5.dp))
                Text(
                    text = statusLabel,
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun MessageItem(
    msg: AiChatMessageModel,
    plantPhotoUrl: String,
    highlightTerms: List<String>
) {
    if (msg.role == "user") {
        UserMessage(msg)
    } else {
        AssistantMessage(msg, plantPhotoUrl, highlightTerms)
    }
}

@Composable
private fun AssistantMessage(
    msg: AiChatMessageModel,
    plantPhotoUrl: String,
    highlightTerms: List<String>
) {
    val blocks = parseAssistantBlocks(msg.content)

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color.White)
                .border(BorderStroke(1.5.dp, Color(0xFF35D26B)), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.toon_assistant_avatar),
                contentDescription = null,
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        }
        Spacer(Modifier.width(10.dp))

        Column(
            modifier = Modifier
                .widthIn(max = 302.dp)
                .weight(1f, fill = false),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "CatPill AI",
                    color = TextPrimary.copy(alpha = 0.86f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.width(6.dp))
                Box(
                    modifier = Modifier
                        .size(3.dp)
                        .clip(CircleShape)
                        .background(TextPrimary.copy(alpha = 0.3f))
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = formatTime(msg.timestamp),
                    color = TextPrimary.copy(alpha = 0.46f),
                    fontSize = 10.sp
                )
            }

            blocks.forEachIndexed { index, block ->
                when (block) {
                    is AssistantBlock.Text -> AssistantTextBubble(
                        text = highlight(block.text, highlightTerms),
                        photoUrl = if (index == 0) plantPhotoUrl else ""
                    )

                    is AssistantBlock.IconCard -> AssistantIconCard(block)
                }
            }
        }
    }
}

@Composable
private fun AssistantTextBubble(text: AnnotatedString, photoUrl: String) {
    val shape = RoundedCornerShape(
        topStart = 4.dp,
        topEnd = 18.dp,
        bottomStart = 18.dp,
        bottomEnd = 18.dp
    )
    Column(
        modifier = Modifier
            .shadow(elevation = 2.dp, shape = shape, ambientColor = Color(0x22000000))
            .clip(shape)
            .background(AssistantBubble)
            .border(BorderStroke(1.dp, Border), shape)
            .padding(14.dp)
    ) {
        Text(
            text = text,
            color = TextPrimary,
            fontSize = 14.sp,
            lineHeight = 20.sp
        )

        if (photoUrl.isNotBlank()) {
            Spacer(Modifier.height(10.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(176.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFE9F2DE))
            ) {
                AsyncImage(
                    model = photoUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
private fun AssistantIconCard(card: AssistantBlock.IconCard) {
    val shape = RoundedCornerShape(16.dp)
    Row(
        modifier = Modifier
            .shadow(elevation = 1.dp, shape = shape, ambientColor = Color(0x18000000))
            .clip(shape)
            .background(Color.White)
            .border(BorderStroke(1.dp, Border), shape)
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            card.iconTint.copy(alpha = 0.22f),
                            card.iconTint.copy(alpha = 0.10f)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(text = card.emoji, fontSize = 18.sp)
        }
        Spacer(Modifier.width(11.dp))
        Text(
            text = card.text,
            color = TextPrimary,
            fontSize = 13.sp,
            lineHeight = 19.sp,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun UserMessage(msg: AiChatMessageModel) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.Top
    ) {
        Column(
            modifier = Modifier.widthIn(max = 286.dp),
            horizontalAlignment = Alignment.End
        ) {
            val userShape = RoundedCornerShape(
                topStart = 18.dp,
                topEnd = 4.dp,
                bottomStart = 18.dp,
                bottomEnd = 18.dp
            )
            Box(
                modifier = Modifier
                    .shadow(elevation = 3.dp, shape = userShape, ambientColor = Color(0x33000000))
                    .clip(userShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(UserBubbleTop, UserBubbleBottom)
                        )
                    )
                    .padding(horizontal = 14.dp, vertical = 11.dp)
            ) {
                Text(
                    text = msg.content,
                    color = Color.White,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            }

            Spacer(Modifier.height(4.dp))
            Text(
                text = formatTime(msg.timestamp),
                color = TextPrimary.copy(alpha = 0.48f),
                fontSize = 10.sp
            )
        }
    }
}

@Composable
private fun TypingBubble() {
    val shape = RoundedCornerShape(
        topStart = 4.dp,
        topEnd = 18.dp,
        bottomStart = 18.dp,
        bottomEnd = 18.dp
    )
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color.White)
                .border(BorderStroke(1.5.dp, Color(0xFF35D26B)), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.toon_assistant_avatar),
                contentDescription = null,
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        }
        Spacer(Modifier.width(10.dp))

        Row(
            modifier = Modifier
                .shadow(elevation = 2.dp, shape = shape, ambientColor = Color(0x22000000))
                .clip(shape)
                .background(AssistantBubble)
                .border(BorderStroke(1.dp, Border), shape)
                .padding(horizontal = 14.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TypingDot(0)
            TypingDot(150)
            TypingDot(300)
        }
    }
}

@Composable
private fun TypingDot(delayMillis: Int) {
    val transition = rememberInfiniteTransition(label = "typingDot")
    val alpha by transition.animateFloat(
        initialValue = 0.25f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 600, delayMillis = delayMillis, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dotAlpha"
    )
    Box(
        modifier = Modifier
            .size(7.dp)
            .clip(CircleShape)
            .background(Brand.copy(alpha = alpha))
    )
}

@Composable
private fun QuickReplyRow(
    replies: List<QuickReply>,
    onTap: (QuickReply) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 2.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        replies.forEach { reply ->
            val shape = RoundedCornerShape(16.dp)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .shadow(elevation = 1.dp, shape = shape, ambientColor = Color(0x18000000))
                    .clip(shape)
                    .background(Color.White)
                    .border(BorderStroke(1.dp, Border), shape)
                    .clickable { onTap(reply) }
                    .padding(horizontal = 10.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFFEAF6E2),
                                    Color(0xFFD7EFC6)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = reply.emoji, fontSize = 17.sp)
                }
                Spacer(Modifier.height(6.dp))
                Text(
                    text = reply.label,
                    color = TextPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = 14.sp
                )
            }
        }
    }
}

@Composable
private fun AnalysingState() {
    val transition = rememberInfiniteTransition(label = "analysing")
    val pulse by transition.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(1100),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scanPulse"
    )
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(contentAlignment = Alignment.Center) {
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .scale(pulse)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    Color(0x3335D26B),
                                    Color.Transparent
                                )
                            )
                        )
                )
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .border(BorderStroke(1.dp, Border), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.toon_camera_caterpillar),
                        contentDescription = null,
                        modifier = Modifier.size(96.dp)
                    )
                }
            }
            Spacer(Modifier.height(20.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White)
                    .border(BorderStroke(1.dp, Border), RoundedCornerShape(20.dp))
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                TypingDot(0)
                Spacer(Modifier.width(4.dp))
                TypingDot(150)
                Spacer(Modifier.width(4.dp))
                TypingDot(300)
                Spacer(Modifier.width(10.dp))
                Text(
                    text = "Analysing your plant",
                    color = TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Spacer(Modifier.height(10.dp))
            Text(
                text = "Identifying species and checking its health",
                color = TextPrimary.copy(alpha = 0.62f),
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun ChatComposer(
    value: String,
    enabled: Boolean,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(SheetBg)
            .navigationBarsPadding()
            .imePadding()
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 32.dp,
                shape = RoundedCornerShape(28.dp),
                ambientColor = Color(0xFF39FF7A),
                spotColor = Color(0xFF39FF7A)
            )
            .shadow(
                elevation = 22.dp,
                shape = RoundedCornerShape(28.dp),
                ambientColor = Color(0xFF35D26B),
                spotColor = Color(0xFF35D26B)
            )
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(28.dp),
                ambientColor = Color(0xFF2BB063),
                spotColor = Color(0xFF2BB063)
            )
            .clip(RoundedCornerShape(28.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFFEAF7E0),
                        Color(0xFFD8EFC4)
                    )
                )
            )
            .border(BorderStroke(2.5.dp, Color(0xFF1F8A4C)), RoundedCornerShape(28.dp))
            .padding(horizontal = 6.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text("Ask CatPill AI about this plant…", color = Hint, fontSize = 14.sp) },
            modifier = Modifier
                .weight(1f)
                .padding(start = 6.dp),
            enabled = enabled,
            shape = RoundedCornerShape(24.dp),
            maxLines = 4,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                disabledBorderColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                disabledTextColor = TextPrimary.copy(alpha = 0.72f)
            )
        )

        Spacer(Modifier.width(6.dp))

        val canSend = enabled && value.isNotBlank()
        val sendPulse by rememberInfiniteTransition(label = "sendPulse").animateFloat(
            initialValue = 0.96f,
            targetValue = 1.06f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1100),
                repeatMode = RepeatMode.Reverse
            ),
            label = "sendScale"
        )

        Box(
            modifier = Modifier.size(54.dp),
            contentAlignment = Alignment.Center
        ) {
            if (canSend) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .scale(sendPulse)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    Color(0x880A4E22),
                                    Color(0x440A4E22),
                                    Color.Transparent
                                )
                            )
                        )
                )
            }
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .shadow(
                        elevation = if (canSend) 14.dp else 0.dp,
                        shape = CircleShape,
                        ambientColor = Color(0xFF0A4E22),
                        spotColor = Color(0xFF0A4E22)
                    )
                    .clip(CircleShape)
                    .background(
                        if (canSend) {
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF1F8A4C),
                                    Color(0xFF0F6A2E),
                                    Color(0xFF0A4E22)
                                )
                            )
                        } else {
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF0F6A2E).copy(alpha = 0.45f),
                                    Color(0xFF0A4E22).copy(alpha = 0.35f)
                                )
                            )
                        }
                    )
                    .border(
                        BorderStroke(2.dp, Color.White.copy(alpha = if (canSend) 0.95f else 0.4f)),
                        CircleShape
                    )
                    .clickable(enabled = canSend) { onSend() },
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clip(CircleShape)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.28f),
                                    Color.Transparent
                                ),
                                endY = 28f
                            )
                        )
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send",
                    tint = Color.White,
                    modifier = Modifier
                        .size(20.dp)
                        .padding(start = 2.dp)
                )
            }
        }
    }
    }
}

private sealed class AssistantBlock {
    data class Text(val text: String) : AssistantBlock()
    data class IconCard(val emoji: String, val text: String, val iconTint: Color) : AssistantBlock()
}

private fun parseAssistantBlocks(content: String): List<AssistantBlock> {
    val lines = content.split('\n').map { it.trim() }.filter { it.isNotEmpty() }
    val blocks = mutableListOf<AssistantBlock>()
    val textBuffer = StringBuilder()

    fun flushText() {
        if (textBuffer.isNotEmpty()) {
            blocks += AssistantBlock.Text(textBuffer.toString().trim())
            textBuffer.clear()
        }
    }

    for (line in lines) {
        val card = detectIconCard(line)
        if (card != null) {
            flushText()
            blocks += card
        } else {
            if (textBuffer.isNotEmpty()) textBuffer.append('\n')
            textBuffer.append(line)
        }
    }

    flushText()
    return blocks
}

private fun detectIconCard(line: String): AssistantBlock.IconCard? {
    val waterEmojis = listOf("💧", "💦", "🚿")
    val sunEmojis = listOf("☀️", "🌞", "🌤️", "🔆")
    val leafEmojis = listOf("🌿", "🍃", "🌱")

    return when {
        waterEmojis.any { line.startsWith(it) } ->
            AssistantBlock.IconCard("💧", line.dropWhile { !it.isLetterOrDigit() }.trim(), DropBlue)

        sunEmojis.any { line.startsWith(it) } ->
            AssistantBlock.IconCard("☀️", line.dropWhile { !it.isLetterOrDigit() }.trim(), SunYellow)

        leafEmojis.any { line.startsWith(it) } ->
            AssistantBlock.IconCard("🌿", line.dropWhile { !it.isLetterOrDigit() }.trim(), LeafGreen)

        line.startsWith("- ") || line.startsWith("• ") || line.startsWith("* ") ->
            AssistantBlock.IconCard("🌿", line.drop(2).trim(), LeafGreen)

        else -> null
    }
}

private fun highlight(text: String, terms: List<String>): AnnotatedString = buildAnnotatedString {
    append(text)
    val style = SpanStyle(color = BrandSoft, fontWeight = FontWeight.Bold)

    for (term in terms) {
        if (term.isBlank()) continue
        var start = 0
        while (true) {
            val idx = text.indexOf(term, start, ignoreCase = true)
            if (idx < 0) break
            addStyle(style, idx, idx + term.length)
            start = idx + term.length
        }
    }
}

private fun healthPill(status: String): Pair<String, Color> {
    return if (status.equals("healthy", ignoreCase = true)) {
        "Healthy" to Brand
    } else {
        "Needs care" to SunYellow
    }
}

private val timeFormatter = SimpleDateFormat("hh:mm a", Locale.getDefault())

private fun formatTime(timestamp: Long): String =
    if (timestamp <= 0L) "" else timeFormatter.format(Date(timestamp))
