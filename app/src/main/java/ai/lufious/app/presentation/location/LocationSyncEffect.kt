package ai.lufious.app.presentation.location

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.location.LocationServices
import java.util.TimeZone

/**
 * Drop this once near the root of the authenticated graph. On first composition it ensures
 * the user's lat/lon is pushed to the backend so the dashboard can fetch weather. Silent on
 * failure / permission denial — never blocks the UI.
 */
@Composable
fun LocationSyncEffect(viewModel: LocationSyncViewModel = hiltViewModel()) {
    val context = LocalContext.current

    val permLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) fetchAndPush(context, viewModel)
    }

    LaunchedEffect(Unit) {
        val granted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

        if (granted) {
            fetchAndPush(context, viewModel)
        } else {
            permLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
        }
    }
}

private fun fetchAndPush(
    context: android.content.Context,
    viewModel: LocationSyncViewModel
) {
    try {
        LocationServices.getFusedLocationProviderClient(context)
            .lastLocation
            .addOnSuccessListener { loc ->
                if (loc != null) {
                    viewModel.push(loc.latitude, loc.longitude, TimeZone.getDefault().id)
                }
            }
    } catch (_: SecurityException) {
        // Permission revoked between check and call — ignore.
    }
}
