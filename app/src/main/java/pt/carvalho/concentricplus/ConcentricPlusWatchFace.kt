package pt.carvalho.concentricplus

import android.view.SurfaceHolder
import androidx.wear.watchface.ComplicationSlotsManager
import androidx.wear.watchface.WatchFace
import androidx.wear.watchface.WatchFaceType
import androidx.wear.watchface.WatchFaceService
import androidx.wear.watchface.WatchState
import androidx.wear.watchface.style.CurrentUserStyleRepository
import androidx.wear.watchface.style.UserStyleSchema
import pt.carvalho.concentricplus.renderer.ConcentricRenderer

internal class ConcentricPlusWatchFace : WatchFaceService() {

    override fun createUserStyleSchema(): UserStyleSchema {
        return super.createUserStyleSchema()
    }

    override fun createComplicationSlotsManager(
        currentUserStyleRepository: CurrentUserStyleRepository
    ): ComplicationSlotsManager {
        return super.createComplicationSlotsManager(currentUserStyleRepository)
    }

    override suspend fun createWatchFace(
        surfaceHolder: SurfaceHolder,
        watchState: WatchState,
        complicationSlotsManager: ComplicationSlotsManager,
        currentUserStyleRepository: CurrentUserStyleRepository
    ): WatchFace = WatchFace(
        watchFaceType = WatchFaceType.DIGITAL,
        renderer = ConcentricRenderer(
            context = applicationContext,
            surface = surfaceHolder,
            state = watchState,
            styleRepository = currentUserStyleRepository
        )
    )
}