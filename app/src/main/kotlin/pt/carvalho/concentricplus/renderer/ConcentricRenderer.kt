package pt.carvalho.concentricplus.renderer

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.view.SurfaceHolder
import androidx.wear.watchface.CanvasType
import androidx.wear.watchface.DrawMode
import androidx.wear.watchface.Renderer
import androidx.wear.watchface.WatchState
import androidx.wear.watchface.style.CurrentUserStyleRepository
import pt.carvalho.concentricplus.R
import pt.carvalho.concentricplus.renderer.clock.ALWAYS_ON_DISPLAY_DIM
import pt.carvalho.concentricplus.renderer.clock.LARGE_FONT_SIZE
import pt.carvalho.concentricplus.renderer.clock.LAYOUT_ALT_CLOCK_SHIFT
import pt.carvalho.concentricplus.renderer.clock.MEDIUM_FONT_SIZE
import pt.carvalho.concentricplus.renderer.clock.drawHours
import pt.carvalho.concentricplus.renderer.clock.drawMinutes
import pt.carvalho.concentricplus.renderer.clock.drawSeconds
import pt.carvalho.concentricplus.renderer.color.ColorUtilities.dim
import pt.carvalho.concentricplus.renderer.data.ConcentricConfiguration
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
    private val textPaint = Paint().apply {
        isAntiAlias = true
        typeface = font
    }

    private val bitmapPaint = Paint().apply {
        isAntiAlias = false
        isFilterBitmap = true
    }

    override suspend fun createSharedAssets(): ConcentricRendererAssets = sharedAssets

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
        val translationX = when (style()) {
            ConcentricConfiguration.Style.HALF_DIAL -> LAYOUT_ALT_CLOCK_SHIFT
            else -> 0.0f
        }

        canvas.drawColor(context.color(configuration.backgroundColorId))

        val savedCanvasCount = canvas.save()
        canvas.translate(-bounds.width() * translationX, 0f)

        drawHours(canvas, bounds, zonedDateTime)
        drawMinutes(canvas, bounds, zonedDateTime)
        drawSeconds(canvas, bounds, zonedDateTime)

        canvas.restoreToCount(savedCanvasCount)
    }

    override fun onDestroy() = controller.destroy()

    private fun drawHours(canvas: Canvas, bounds: Rect, zonedDateTime: ZonedDateTime) {
        canvas.drawHours(
            bounds = bounds,
            time = zonedDateTime,
            paint = textPaint.apply {
                color = context.color(configuration.hoursTextColorId).dim(dim())
                textSize = bounds.height() * LARGE_FONT_SIZE
            }
        )
    }

    private fun drawMinutes(canvas: Canvas, bounds: Rect, zonedDateTime: ZonedDateTime) {
        canvas.drawMinutes(
            bounds = bounds,
            time = zonedDateTime,
            minutesTextBitmap = controller.minutesTextMask(
                bounds = bounds,
                textColor = context.color(configuration.minutesTextColorId),
                textFont = font
            ),
            minutesTickBitmap = controller.minutesTicksMask(
                bounds = bounds,
                color = context.color(configuration.minutesTickColorId)
            ),
            textPaint = textPaint.apply {
                color = context.color(configuration.minutesLargeTextColorId).dim(dim())
                textSize = bounds.height() * MEDIUM_FONT_SIZE
            },
            bitmapPaint = bitmapPaint
        )
    }

    private fun drawSeconds(canvas: Canvas, bounds: Rect, zonedDateTime: ZonedDateTime) {
        canvas.drawSeconds(
            isInAlwaysOnDisplay = isInAlwaysOnDisplay(),
            time = zonedDateTime,
            secondsTickBitmap = controller.secondsTicksMask(
                bounds = bounds,
                color = context.color(configuration.secondsTickColorId)
            ),
            secondsTextBitmap = controller.secondsTextMask(
                bounds = bounds,
                textColor = context.color(configuration.secondsTextColorId),
                textFont = font
            ),
            bitmapPaint = bitmapPaint
        )
    }

    private fun isInAlwaysOnDisplay(): Boolean =
        renderParameters.drawMode == DrawMode.AMBIENT

    private fun style(): ConcentricConfiguration.Style =
        configuration.style

    private fun dim(): Float =
        if (isInAlwaysOnDisplay()) ALWAYS_ON_DISPLAY_DIM else 1.0f

    private val sharedAssets = ConcentricRendererAssets()

    private val controller = ConcentricController(
        styleRepository = styleRepository,
        sharedAssets = sharedAssets
    )

    private val font: Typeface = context.font(R.font.google_sans)

    private val configuration: ConcentricConfiguration
        get() = controller.configuration

    companion object {
        internal const val DEFAULT_CANVAS_TYPE = CanvasType.HARDWARE
        internal const val DEFAULT_REFRESH_RATE = 16L
    }
}
