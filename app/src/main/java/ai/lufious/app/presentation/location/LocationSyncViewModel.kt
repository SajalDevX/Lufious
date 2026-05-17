package ai.lufious.app.presentation.location

import ai.lufious.app.core.network.LufiousApi
import ai.lufious.app.core.network.dto.LocationPatchRequest
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@HiltViewModel
class LocationSyncViewModel @Inject constructor(
    private val api: LufiousApi
) : ViewModel() {

    fun push(lat: Double, lon: Double, timezone: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                api.patchLocation(
                    LocationPatchRequest(lat = lat, lon = lon, timezone = timezone)
                )
            }
        }
    }
}
