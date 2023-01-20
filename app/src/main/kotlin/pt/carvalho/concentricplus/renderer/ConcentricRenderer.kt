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
import pt.carvalho.concentricplus.renderer.clock.hoursTextMask
import pt.carvalho.concentricplus.renderer.clock.minutesTextMask
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
        val isInAlwaysOnDisplay = isInAlwaysOnDisplay()
        val dim = if (isInAlwaysOnDisplay) ALWAYS_ON_DISPLAY_DIM else 1.0f

        val translationX = when (style()) {
            ConcentricConfiguration.Style.HALF_DIAL -> LAYOUT_ALT_CLOCK_SHIFT
            else -> 0.0f
        }

        canvas.drawColor(context.color(configuration.backgroundColorId))

        canvas.translate(-bounds.width() * translationX, 0f)
        canvas.drawHours(
            dim = dim,
            bounds = bounds,
            zonedDateTime = zonedDateTime
        )
        canvas.drawMinutes(
            dim = dim,
            bounds = bounds,
            zonedDateTime = zonedDateTime
        )
        canvas.drawSeconds(
            bounds = bounds,
            isInAlwaysOnDisplay = isInAlwaysOnDisplay,
        )
    }

    override fun onDestroy() = controller.destroy()

    private fun Canvas.drawSeconds(
        bounds: Rect,
        isInAlwaysOnDisplay: Boolean
    ) {
        drawBitmap(
            controller.secondsTicksMask(
                bounds = bounds,
                color = context.color(configuration.secondsTickColorId)
            ),
            0.0f,
            0.0f,
            bitmapPaint
        )

        if (!isInAlwaysOnDisplay) {
            drawBitmap(
                controller.secondsTextMask(
                    bounds = bounds,
                    textColor = context.color(configuration.secondsTextColorId),
                    textFont = font
                ),
                0.0f,
                0.0f,
                bitmapPaint
            )
        }
    }

    private fun Canvas.drawMinutes(
        dim: Float,
        bounds: Rect,
        zonedDateTime: ZonedDateTime,
    ) {
        drawBitmap(
            controller.minutesTicksMask(
                bounds = bounds,
                color = context.color(configuration.minutesTickColorId)
            ),
            0.0f,
            0.0f,
            bitmapPaint
        )
        drawBitmap(
            controller.minutesTextMask(
                bounds = bounds,
                textColor = context.color(configuration.minutesTextColorId),
                textFont = font
            ),
            0.0f,
            0.0f,
            bitmapPaint
        )
        minutesTextMask(
            bounds = bounds,
            textSize = MEDIUM_FONT_SIZE,
            textFont = font,
            textColor = context.color(configuration.minutesLargeTextColorId).dim(dim),
            time = zonedDateTime
        )
    }

    private fun Canvas.drawHours(
        bounds: Rect,
        zonedDateTime: ZonedDateTime,
        dim: Float
    ) {
        hoursTextMask(
            bounds = bounds,
            time = zonedDateTime,
            textSize = LARGE_FONT_SIZE,
            textFont = font,
            textColor = context.color(configuration.hoursTextColorId).dim(dim)
        )
    }

    private fun isInAlwaysOnDisplay(): Boolean =
        renderParameters.drawMode == DrawMode.AMBIENT

    private fun style(): ConcentricConfiguration.Style =
        configuration.style

    private val sharedAssets = ConcentricRendererAssets()

    private val controller = ConcentricController(
        styleRepository = styleRepository,
        sharedAssets = sharedAssets
    )

    private val bitmapPaint = Paint().apply {
        isAntiAlias = false
        isFilterBitmap = true
    }

    private val font: Typeface = context.font(R.font.google_sans)

    private val configuration: ConcentricConfiguration
        get() = controller.configuration

    companion object {
        internal const val DEFAULT_CANVAS_TYPE = CanvasType.HARDWARE
        internal const val DEFAULT_REFRESH_RATE = 16L
    }
}
