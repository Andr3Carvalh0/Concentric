package pt.carvalho.concentricplus.renderer

import android.util.Log
import androidx.wear.watchface.ComplicationSlotsManager
import androidx.wear.watchface.style.CurrentUserStyleRepository
import androidx.wear.watchface.style.UserStyle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import pt.carvalho.concentricplus.renderer.data.ConcentricConfiguration
import pt.carvalho.concentricplus.renderer.data.DEFAULT

internal class ConcentricController(
    private val styleRepository: CurrentUserStyleRepository,
    private val complicationSlotsManager: ComplicationSlotsManager
) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    var configuration: ConcentricConfiguration = DEFAULT

    init {
        scope.launch {
            styleRepository.userStyle
                .collect { userStyle -> updateWatchFaceData(userStyle) }
        }
    }

    fun destroy() = scope.cancel()

    private fun updateWatchFaceData(style: UserStyle) {
        Log.d("ConcentricController", "$style")
    }
}
