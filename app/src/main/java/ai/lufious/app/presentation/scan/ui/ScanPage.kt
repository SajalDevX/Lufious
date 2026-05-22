package ai.lufious.app.presentation.scan.ui

import ai.lufious.app.R
import ai.lufious.app.core.theme.ClashDisplay
import ai.lufious.app.core.theme.Background
import ai.lufious.app.core.theme.PrimaryColor
import ai.lufious.app.core.theme.TextPrimary
import ai.lufious.app.core.utils.UiEffect
import ai.lufious.app.presentation.scan.viewmodel.ScanEvent
import ai.lufious.app.presentation.scan.viewmodel.ScanViewModel
import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.concurrent.futures.await
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import kotlinx.coroutines.flow.collectLatest

@Composable
fun ScanPage(
    navController: NavController,
    outerNavController: NavController = navController,
    modifier: Modifier = Modifier,
    viewModel: ScanViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.effects.collectLatest { effect ->
            when (effect) {
                // AiChat / scan-result live in the outer graph (above bottom bar)
                is UiEffect.Navigate -> outerNavController.navigate(effect.route)
                is UiEffect.ShowError ->
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                else -> {}
            }
        }
    }

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED
        )
    }
    var showCamera by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasCameraPermission = granted
        if (granted) showCamera = true
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            runCatching {
                context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
            }.getOrNull()?.let { bytes ->
                viewModel.onEvent(ScanEvent.Scan(bytes))
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("scan_screen")
    ) {
        when {
            !showCamera -> ScanIntroContent(
                onStartScan = {
                    if (hasCameraPermission) showCamera = true
                    else permissionLauncher.launch(Manifest.permission.CAMERA)
                },
                onPickFromGallery = {
                    galleryLauncher.launch(
                        androidx.activity.result.PickVisualMediaRequest(
                            ActivityResultContracts.PickVisualMedia.ImageOnly
                        )
                    )
                }
            )
            hasCameraPermission -> CameraContent(
                isScanning = state.isScanning,
                onClose = { showCamera = false },
                onScan = { imageCapture ->
                    imageCapture.takePicture(
                        ContextCompat.getMainExecutor(context),
                        object : ImageCapture.OnImageCapturedCallback() {
                            override fun onCaptureSuccess(image: ImageProxy) {
                                val buffer = image.planes[0].buffer
                                val bytes = ByteArray(buffer.remaining())
                                buffer.get(bytes)
                                image.close()
                                viewModel.onEvent(ScanEvent.Scan(bytes))
                            }
                            override fun onError(exc: ImageCaptureException) {
                                viewModel.onEvent(ScanEvent.ScanFailed(exc.message ?: "Capture failed"))
                            }
                        }
                    )
                }
            )
            else -> PermissionDeniedContent(
                onRequestPermission = { permissionLauncher.launch(Manifest.permission.CAMERA) }
            )
        }
    }
}

private val ScanScreenBg = Color(0xFFF8FBF4)
private val ScanGreen = Color(0xFF138A45)
private val ScanGreenSoft = Color(0xFF44A557)
private val TipGreen = Color(0xFFF4FAEF)
private val TipYellow = Color(0xFFFFFAE9)
private val TipPurple = Color(0xFFF5F0FB)
private val HeroHeadlineShadow = Shadow(
    color = Color.Black.copy(alpha = 0.14f),
    offset = Offset(0f, 2f),
    blurRadius = 4f
)

private data class ScanTip(
    val imageRes: Int,
    val title: String,
    val body: String,
    val bgColor: Color,
    val borderColor: Color,
    val arrowColor: Color
)

@Composable
private fun ScanIntroContent(
    onStartScan: () -> Unit,
    onPickFromGallery: () -> Unit
) {
    val tips = listOf(
        ScanTip(
            imageRes = R.drawable.toon_camera_caterpillar,
            title = "Frame the Leaf",
            body = "Fill the frame with one leaf\nor full plant.",
            bgColor = TipGreen,
            borderColor = Color(0xFFD7E8CB),
            arrowColor = Color(0xFF83B22A)
        ),
        ScanTip(
            imageRes = R.drawable.toon_angry_caterpillar,
            title = "Good Lighting",
            body = "Natural daylight gives\nthe most accurate result.",
            bgColor = TipYellow,
            borderColor = Color(0xFFF0DEA9),
            arrowColor = Color(0xFFFF8B05)
        ),
        ScanTip(
            imageRes = R.drawable.toon_sleepy_leaf,
            title = "Hold Steady",
            body = "Keep the camera still\nuntil the scan completes.",
            bgColor = TipPurple,
            borderColor = Color(0xFFDDCFF1),
            arrowColor = Color(0xFF8E61DB)
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ScanScreenBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp)
                .padding(bottom = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(38.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(top = 8.dp)
                ) {
                    HeroHeadlineWord(text = "Identify", color = TextPrimary)
                    HeroHeadlineWord(text = "Any", color = ScanGreenSoft)
                    HeroHeadlineWord(text = "Plant", color = TextPrimary)
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = "Snap a photo to get the\nspecies, health check,\nand care tips in seconds.",
                        color = TextPrimary.copy(alpha = 0.92f),
                        fontSize = 14.sp,
                        lineHeight = 22.sp
                    )
                }

                Image(
                    painter = painterResource(id = R.drawable.toon_happy_lets_grow),
                    contentDescription = null,
                    modifier = Modifier
                        .size(250.dp)
                        .padding(start = 4.dp)
                )
            }

            Spacer(Modifier.height(20.dp))

            tips.forEachIndexed { index, tip ->
                TipRow(
                    imageRes = tip.imageRes,
                    title = tip.title,
                    body = tip.body,
                    bgColor = tip.bgColor,
                    borderColor = tip.borderColor,
                    arrowColor = tip.arrowColor
                )
                if (index < tips.lastIndex) {
                    Spacer(Modifier.height(16.dp))
                }
            }

            Spacer(Modifier.height(20.dp))

            StartScanButton(onClick = onStartScan)

            Spacer(Modifier.height(12.dp))

            UploadScanButton(onClick = onPickFromGallery)

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun HeroHeadlineWord(text: String, color: Color) {
    Text(
        text = text,
        color = color,
        fontSize = 35.sp,
        fontWeight = FontWeight.Black,
        fontFamily = ClashDisplay,
        letterSpacing = (-0.6).sp,
        style = TextStyle(shadow = HeroHeadlineShadow)
    )
}

@Composable
private fun StartScanButton(onClick: () -> Unit) {
    val shape = RoundedCornerShape(33.dp)
    Box(
        modifier = Modifier
            .fillMaxWidth(0.94f)
            .height(66.dp)
            .shadow(
                elevation = 14.dp,
                shape = shape,
                ambientColor = Color(0x662FA75B),
                spotColor = Color(0x552FA75B)
            )
            .clip(shape)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFF35B464), Color(0xFF1D954D), Color(0xFF0F8240)),
                    start = Offset.Zero,
                    end = Offset(900f, 220f)
                )
            )
            .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.22f)), shape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.18f))
                        .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.35f)), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(21.dp)
                    )
                }
                Spacer(Modifier.width(12.dp))
                Text(
                    text = "Start Scan",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.2.sp
                )
            }

            Text(
                text = "✦",
                color = Color(0xFFFFF59D),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun UploadScanButton(onClick: () -> Unit) {
    val shape = RoundedCornerShape(32.dp)
    Box(
        modifier = Modifier
            .fillMaxWidth(0.94f)
            .height(64.dp)
            .shadow(
                elevation = 8.dp,
                shape = shape,
                ambientColor = Color(0x22178A45),
                spotColor = Color(0x22178A45)
            )
            .clip(shape)
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(Color.White, Color(0xFFF3FBF5))
                )
            )
            .border(BorderStroke(2.dp, ScanGreen.copy(alpha = 0.9f)), shape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(ScanGreen.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PhotoLibrary,
                        contentDescription = null,
                        tint = ScanGreen,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(Modifier.width(12.dp))
                Text(
                    text = "Upload from Gallery",
                    color = ScanGreen,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.1.sp
                )
            }

            Text(
                text = "↗",
                color = ScanGreen,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun TipRow(
    imageRes: Int,
    title: String,
    body: String,
    bgColor: Color,
    borderColor: Color,
    arrowColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(30.dp))
            .background(bgColor)
            .border(BorderStroke(1.dp, borderColor), RoundedCornerShape(30.dp))
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(104.dp)
                .clip(RoundedCornerShape(22.dp))
                .background(Color.White.copy(alpha = 0.35f)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(20.dp))
            )
        }

        Spacer(Modifier.width(14.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp)
        ) {
            Text(
                text = title,
                color = TextPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = body,
                color = TextPrimary.copy(alpha = 0.9f),
                fontSize = 14.sp,
                lineHeight = 20.sp
            )
        }

        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(arrowColor),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "›", color = Color.White, fontSize = 40.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun CameraContent(
    isScanning: Boolean,
    onClose: () -> Unit,
    onScan: (ImageCapture) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val previewView = remember { PreviewView(context) }
    val imageCapture = remember { ImageCapture.Builder().build() }

    LaunchedEffect(lifecycleOwner) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        val cameraProvider = cameraProviderFuture.await()
        val preview = Preview.Builder().build().also {
            it.setSurfaceProvider(previewView.surfaceProvider)
        }
        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(
            lifecycleOwner,
            CameraSelector.DEFAULT_BACK_CAMERA,
            preview,
            imageCapture
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(factory = { previewView }, modifier = Modifier.fillMaxSize())

        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .statusBarsPadding()
                .padding(12.dp)
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.4f))
                .clickable { onClose() },
            contentAlignment = Alignment.Center
        ) {
            Text(text = "✕", color = Color.White, fontSize = 18.sp)
        }

        if (isScanning) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.55f)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = PrimaryColor, strokeWidth = 3.dp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Identifying plant…",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        } else {
            IconButton(
                onClick = { onScan(imageCapture) },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 48.dp)
                    .size(72.dp)
                    .background(color = PrimaryColor, shape = CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "Scan plant",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

@Composable
private fun PermissionDeniedContent(onRequestPermission: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("📷", fontSize = 52.sp)
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "Camera access needed",
            color = TextPrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Grant camera permission to scan and identify plants with AI.",
            color = TextPrimary.copy(alpha = 0.65f),
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(28.dp))
        OutlinedButton(onClick = onRequestPermission) {
            Text(
                text = "Grant Permission",
                color = PrimaryColor,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
