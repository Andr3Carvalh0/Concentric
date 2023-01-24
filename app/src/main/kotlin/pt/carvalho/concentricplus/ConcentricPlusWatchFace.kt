package pt.carvalho.concentricplus

import android.view.SurfaceHolder
import androidx.wear.watchface.ComplicationSlotsManager
import androidx.wear.watchface.WatchFace
import androidx.wear.watchface.WatchFaceService
import androidx.wear.watchface.WatchFaceType
import androidx.wear.watchface.WatchState
import androidx.wear.watchface.style.CurrentUserStyleRepository
import androidx.wear.watchface.style.UserStyleSchema
import pt.carvalho.concentricplus.complications.ConcentricComplicationsManager
import pt.carvalho.concentricplus.renderer.ConcentricRenderer
import pt.carvalho.concentricplus.style.ConcentricStyleSchema

class ConcentricPlusWatchFace : WatchFaceService() {

    override fun createUserStyleSchema(): UserStyleSchema = ConcentricStyleSchema.create(this)

    override fun createComplicationSlotsManager(
        currentUserStyleRepository: CurrentUserStyleRepository
    ): ComplicationSlotsManager = ConcentricComplicationsManager.create(
        context = this,
        repository = currentUserStyleRepository
    )

    override suspend fun createWatchFace(
        surfaceHolder: SurfaceHolder,
        watchState: WatchState,
        complicationSlotsManager: ComplicationSlotsManager,
        currentUserStyleRepository: CurrentUserStyleRepository
    ): WatchFace = WatchFace(
        watchFaceType = WatchFaceType.DIGITAL,
        renderer = ConcentricRenderer(
            context = this,
            surface = surfaceHolder,
            state = watchState,
            styleRepository = currentUserStyleRepository,
            complicationSlotsManager = complicationSlotsManager
        )
    )
}
