package pt.carvalho.concentricplus.renderer

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.view.SurfaceHolder
import androidx.wear.watchface.CanvasType
import androidx.wear.watchface.Renderer
import androidx.wear.watchface.WatchState
import androidx.wear.watchface.style.CurrentUserStyleRepository
import pt.carvalho.concentricplus.renderer.clock.drawAnalogClock
import java.time.ZonedDateTime

internal class ConcentricRenderer(
    private val context: Context,
    surface: SurfaceHolder,
    state: WatchState,
    styleRepository: CurrentUserStyleRepository,
    @CanvasType canvasType: Int = DEFAULT_CANVAS_TYPE,
    refreshRateMs: Long = DEFAULT_REFRESH_RATE
) : Renderer.CanvasRenderer2<ConcentricRendererAssets>(
    surfaceHolder = surface,
    currentUserStyleRepository = styleRepository,
    watchState = state,
    canvasType = canvasType,
    interactiveDrawModeUpdateDelayMillis = refreshRateMs,
    clearWithBackgroundTintBeforeRenderingHighlightLayer = true
) {

    override suspend fun createSharedAssets(): ConcentricRendererAssets =
        ConcentricRendererAssets(context = context)

    override fun renderHighlightLayer(
        canvas: Canvas,
        bounds: Rect,
        zonedDateTime: ZonedDateTime,
        sharedAssets: ConcentricRendererAssets
    ) {
        TODO("Not yet implemented")
    }

    override fun render(
        canvas: Canvas,
        bounds: Rect,
        zonedDateTime: ZonedDateTime,
        sharedAssets: ConcentricRendererAssets
    ) {
        canvas.drawAnalogClock(
            bounds = bounds,
            assets = sharedAssets,
            time = zonedDateTime
        )
    }

    companion object {
        internal const val DEFAULT_CANVAS_TYPE = CanvasType.HARDWARE
        internal const val DEFAULT_REFRESH_RATE = 64L
    }
}