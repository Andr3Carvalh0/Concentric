package pt.carvalho.concentricplus.renderer

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.view.SurfaceHolder
import androidx.core.graphics.withScale
import androidx.wear.watchface.CanvasType
import androidx.wear.watchface.ComplicationSlotsManager
import androidx.wear.watchface.DrawMode
import androidx.wear.watchface.Renderer
import androidx.wear.watchface.WatchState
import androidx.wear.watchface.complications.rendering.CanvasComplicationDrawable
import androidx.wear.watchface.complications.rendering.ComplicationDrawable
import androidx.wear.watchface.style.CurrentUserStyleRepository
import pt.carvalho.concentricplus.R
import pt.carvalho.concentricplus.renderer.clock.drawBorder
import pt.carvalho.concentricplus.renderer.clock.drawHours
import pt.carvalho.concentricplus.renderer.clock.drawMinutes
import pt.carvalho.concentricplus.renderer.clock.drawSeconds
import pt.carvalho.concentricplus.renderer.color.ColorUtilities.dim
import pt.carvalho.concentricplus.renderer.data.ConcentricConfiguration
import pt.carvalho.concentricplus.renderer.data.ConcentricConfiguration.Style.HALF_DIAL
import pt.carvalho.concentricplus.utilities.color
import pt.carvalho.concentricplus.utilities.font
import java.time.ZonedDateTime

internal class ConcentricRenderer(
    private val context: Context,
    surface: SurfaceHolder,
    state: WatchState,
    complicationSlotsManager: ComplicationSlotsManager,
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
    private val sharedAssets = ConcentricRendererAssets()

    private val controller = ConcentricController(
        styleRepository = styleRepository,
        sharedAssets = sharedAssets,
        complicationSlotsManager = complicationSlotsManager
    )

    private val font: Typeface = context.font(R.font.google_sans)

    private val configuration: ConcentricConfiguration = controller.configuration

    private val textPaint = Paint().apply {
        isAntiAlias = true
        typeface = font
    }

    private val borderPaint = Paint().apply {
        isAntiAlias = true
        typeface = font
        style = Paint.Style.STROKE
    }

    private val bitmapPaint = Paint().apply {
        isFilterBitmap = true
    }

    private var lastComplicationColorId: Int = -1

    override suspend fun createSharedAssets(): ConcentricRendererAssets = sharedAssets

    override fun renderHighlightLayer(
        canvas: Canvas,
        bounds: Rect,
        zonedDateTime: ZonedDateTime,
        sharedAssets: ConcentricRendererAssets
    ) {
        renderParameters.highlightLayer?.backgroundTint?.let { canvas.drawColor(it) }

        configuration.complications.forEach { complication ->
            if (complication.enabled)
                complication.renderHighlightLayer(canvas, zonedDateTime, renderParameters)
        }
    }

    override fun render(
        canvas: Canvas,
        bounds: Rect,
        zonedDateTime: ZonedDateTime,
        sharedAssets: ConcentricRendererAssets
    ) {
        val translationX = when (style()) {
            HALF_DIAL -> LAYOUT_ALT_CLOCK_SHIFT
            else -> 0.0f
        }

        val scale = when {
            isInAlwaysOnDisplay() -> ALWAYS_ON_DISPLAY_SCALE_FACTOR
            else -> DEFAULT_SCALE_FACTOR
        }

        canvas.drawColor(context.color(configuration.backgroundColorId))

        canvas.withScale(scale, scale, bounds.exactCenterX(), bounds.exactCenterY()) {
            canvas.save().run {
                canvas.translate(-bounds.width() * translationX, 0f)

                drawHours(canvas, bounds, zonedDateTime)
                drawMinutes(canvas, bounds, zonedDateTime)
                drawSeconds(canvas, bounds, zonedDateTime)
                drawBorder(canvas, bounds)

                canvas.restoreToCount(this)
            }
        }

        drawComplications(canvas, zonedDateTime)
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
                textColor = context.color(configuration.minutesDialTextColorId),
                textFont = font
            ),
            minutesTickBitmap = controller.minutesTicksMask(
                bounds = bounds,
                color = context.color(configuration.minutesDialTickColorId)
            ),
            textPaint = textPaint.apply {
                color = context.color(configuration.minutesTextColorId).dim(dim())
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
                color = context.color(configuration.secondsDialTickColorId)
            ),
            secondsTextBitmap = controller.secondsTextMask(
                bounds = bounds,
                textColor = context.color(configuration.secondsDialTextColorId),
                textFont = font
            ),
            bitmapPaint = bitmapPaint
        )
    }

    private fun drawBorder(canvas: Canvas, bounds: Rect) {
        canvas.drawBorder(
            bounds = bounds,
            isInAlwaysOnDisplay = isInAlwaysOnDisplay(),
            isHalfCircle = configuration.style == HALF_DIAL,
            paint = borderPaint.apply {
                color = context.color(configuration.borderColorId).dim(dim())
                textSize = bounds.height() * BORDER_SIZE
                strokeWidth = when {
                    isInAlwaysOnDisplay() -> BORDER_THICKNESS_ALWAYS_ON_DISPLAY
                    else -> BORDER_THICKNESS
                }
            }
        )
    }

    private fun drawComplications(canvas: Canvas, zonedDateTime: ZonedDateTime) {
        configuration.complications.forEach { complication ->
            if (configuration.complicationsTintColorId != lastComplicationColorId) {
                ComplicationDrawable.getDrawable(context, configuration.complicationsTintColorId)
                    ?.let { (complication.renderer as? CanvasComplicationDrawable)?.drawable = it }
            }

            if (complication.enabled) complication.render(canvas, zonedDateTime, renderParameters)
        }

        if (configuration.complications.isNotEmpty()) {
            lastComplicationColorId = configuration.complicationsTintColorId
        }
    }

    private fun isInAlwaysOnDisplay(): Boolean =
        renderParameters.drawMode == DrawMode.AMBIENT

    private fun style(): ConcentricConfiguration.Style =
        configuration.style

    private fun dim(): Float =
        if (isInAlwaysOnDisplay()) ALWAYS_ON_DISPLAY_DIM else 1.0f

    companion object {
        internal const val DEFAULT_CANVAS_TYPE = CanvasType.HARDWARE
        internal const val DEFAULT_REFRESH_RATE = 16L
    }
}
