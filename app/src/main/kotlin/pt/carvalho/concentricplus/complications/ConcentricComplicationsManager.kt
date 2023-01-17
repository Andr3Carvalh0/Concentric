package pt.carvalho.concentricplus.complications

import androidx.wear.watchface.ComplicationSlotsManager
import androidx.wear.watchface.style.CurrentUserStyleRepository

internal object ConcentricComplicationsManager {

    fun create(repository: CurrentUserStyleRepository): ComplicationSlotsManager =
        ComplicationSlotsManager(emptyList(), repository)
}
