package pt.carvalho.concentricplus.renderer

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.Log
import android.view.SurfaceHolder
import androidx.wear.watchface.CanvasType
import androidx.wear.watchface.Renderer
import androidx.wear.watchface.WatchState
import androidx.wear.watchface.style.CurrentUserStyleRepository
import pt.carvalho.concentricplus.R
import pt.carvalho.concentricplus.renderer.clock.hoursTextMask
import pt.carvalho.concentricplus.utilities.color
import pt.carvalho.concentricplus.utilities.font
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

    private val paint = Paint().apply {
        isAntiAlias = true
        isFilterBitmap = true
    }

    private var secondsTextBitmap: Bitmap? = null

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
        canvas.drawColor(context.color(R.color.background_watch_face_color))
        canvas.drawHours(bounds = bounds, zonedDateTime = zonedDateTime)
        canvas.drawMinutes(
            hasTicks = true,
            bounds = bounds,
            assets = sharedAssets,
            zonedDateTime = zonedDateTime
        )
        canvas.drawSeconds(hasTicks = true, bounds = bounds, assets = sharedAssets)
    }

    private fun Canvas.drawSeconds(
        hasTicks: Boolean,
        bounds: Rect,
        assets: ConcentricRendererAssets
    ) {
        if (hasTicks) {
            drawBitmap(
                assets.secondsTicksMask(bounds), 0.0f, 0.0f, paint
            )
        }

        drawBitmap(
            assets.secondsTextMask(bounds), 0.0f, 0.0f, paint
        )
    }

    private fun Canvas.drawMinutes(
        hasTicks: Boolean,
        bounds: Rect,
        assets: ConcentricRendererAssets,
        zonedDateTime: ZonedDateTime
    ) {
        Log.v("Concentric", "$zonedDateTime")

        if (hasTicks) {
            drawBitmap(
                assets.minutesTicksMask(bounds), 0.0f, 0.0f, paint
            )
        }

        val textBitmap = secondsTextBitmap ?: assets.minutesTextMask(bounds)
            .also { secondsTextBitmap = it }

        drawBitmap(
            textBitmap, 0.0f, 0.0f, paint
        )
    }

    private fun Canvas.drawHours(
        bounds: Rect,
        zonedDateTime: ZonedDateTime
    ) {
        hoursTextMask(
            bounds = bounds,
            time = zonedDateTime,
            textSize = LARGE_FONT_SIZE,
            textFont = context.font(R.font.google_sans),
            textColor = context.getColor(R.color.background_hours_color)
        )
    }

    override fun onDestroy() {
        secondsTextBitmap?.recycle()
        secondsTextBitmap = null
    }

    companion object {
        internal const val DEFAULT_CANVAS_TYPE = CanvasType.HARDWARE
        internal const val DEFAULT_REFRESH_RATE = 32L
    }
}
