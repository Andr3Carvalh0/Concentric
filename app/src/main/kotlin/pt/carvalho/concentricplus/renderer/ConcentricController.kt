package pt.carvalho.concentricplus.renderer

import androidx.wear.watchface.ComplicationSlot
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
import pt.carvalho.concentricplus.utilities.isHalfDialLayout
import pt.carvalho.concentricplus.utilities.selectedColorValue
import pt.carvalho.concentricplus.utilities.selectedLayoutValue

internal class ConcentricController(
    private val styleRepository: CurrentUserStyleRepository,
    private val complicationSlotsManager: ComplicationSlotsManager
) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    var configuration: ConcentricConfiguration = DEFAULT

    init {
        scope.launch(Dispatchers.Main.immediate) {
            styleRepository.userStyle
                .collect { userStyle -> updateWatchFaceData(userStyle) }
        }
    }

    fun destroy() = scope.cancel()

    private fun updateWatchFaceData(style: UserStyle) {
        val updatedConfiguration = configuration.copy(
            secondsDialTextColorId = style.selectedColorValue(),
            borderColorId = style.selectedColorValue(),
            complicationsTintColorId = style.selectedColorValue(),
            style = style.selectedLayoutValue(),
            complications = if (style.isHalfDialLayout()) complications() else emptyList()
        )

        if (updatedConfiguration != configuration) {
            configuration = updatedConfiguration
        }
    }

    private fun complications(): List<ComplicationSlot> =
        complicationSlotsManager.complicationSlots.entries.map { it.value }
}
