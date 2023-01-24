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
import pt.carvalho.concentricplus.style.COLOR_STYLE
import pt.carvalho.concentricplus.style.ColorStyleOptions
import pt.carvalho.concentricplus.style.LAYOUT_STYLE

internal class ConcentricController(
    private val styleRepository: CurrentUserStyleRepository,
    private val complicationSlotsManager: ComplicationSlotsManager
) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    var configuration: ConcentricConfiguration = DEFAULT

    init {
        configuration = configuration.copy(
            complications = complicationSlotsManager.complicationSlots.entries.map { it.value }
        )

        scope.launch {
            styleRepository.userStyle
                .collect { userStyle -> updateWatchFaceData(userStyle) }
        }
    }

    fun destroy() = scope.cancel()

    private fun updateWatchFaceData(style: UserStyle) {
        var updatedConfiguration = configuration

        style.forEach { (setting, option) ->
            when (setting.id.value) {
                COLOR_STYLE -> {
                    ColorStyleOptions.colors[option.id.toString().toInt()]
                        ?.let { color ->
                            updatedConfiguration = updatedConfiguration.copy(
                                secondsDialTextColorId = color,
                                borderColorId = color
                            )
                        }
                }
                LAYOUT_STYLE -> {
                }
                else ->
                    Log.d("ConcentricController", "Unknown style setting ${setting.id.value}")
            }
        }

        if (updatedConfiguration != configuration) {
            configuration = updatedConfiguration
        }
    }
}
