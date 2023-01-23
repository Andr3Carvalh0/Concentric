package pt.carvalho.concentricplus.renderer

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.graphics.fonts.FontStyle.FONT_WEIGHT_LIGHT
import android.graphics.fonts.FontStyle.FONT_WEIGHT_MEDIUM
import android.graphics.fonts.FontStyle.FONT_WEIGHT_THIN
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
import pt.carvalho.concentricplus.renderer.data.ConcentricConfiguration
import pt.carvalho.concentricplus.renderer.data.ConcentricConfiguration.Style.HALF_DIAL
import pt.carvalho.concentricplus.utilities.color
import pt.carvalho.concentricplus.utilities.restoreToCountAfter
import pt.carvalho.concentricplus.utilities.typeface
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
        complicationSlotsManager = complicationSlotsManager
    )

    private val font: Typeface = context.typeface(R.font.product_sans)
    private val alternativeFont: Typeface = context.typeface(R.font.product_sans_medium_alt)

    private val configuration: ConcentricConfiguration = controller.configuration

    private val textPaint by lazy {
        Paint().apply {
            isAntiAlias = true
        }
    }

    private val borderPaint by lazy {
        Paint().apply {
            isAntiAlias = true
            typeface = typeface(FONT_WEIGHT_MEDIUM)
            style = Paint.Style.STROKE
        }
    }

    private val dialTicksPaint by lazy {
        Paint().apply {
            isAntiAlias = true
            strokeCap = Paint.Cap.ROUND
            typeface = typeface(FONT_WEIGHT_THIN)
            strokeWidth = DIAL_TICKS_STROKE
        }
    }

    private val dialTextPaint by lazy {
        Paint().apply {
            isAntiAlias = true
            typeface = typeface(FONT_WEIGHT_MEDIUM)
        }
    }

    private val backgroundPaint by lazy {
        Paint().apply {
            style = Paint.Style.FILL
            isAntiAlias = true
            typeface = typeface(FONT_WEIGHT_LIGHT)
        }
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
            isInAlwaysOnDisplay() && style() != HALF_DIAL -> ALWAYS_ON_DISPLAY_SCALE_FACTOR
            else -> DEFAULT_SCALE_FACTOR
        }

        canvas.drawColor(context.color(configuration.backgroundColorId))

        canvas.withScale(scale, scale, bounds.exactCenterX(), bounds.exactCenterY()) {
            canvas.restoreToCountAfter {
                canvas.translate(-bounds.width() * translationX, 0f)

                drawMinutes(canvas, bounds, zonedDateTime)
                drawSeconds(canvas, bounds, zonedDateTime)
                drawHours(canvas, bounds, zonedDateTime)
                drawBorder(canvas, bounds)
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
                color = context.color(configuration.hoursTextColorId)
                textSize = bounds.height() * LARGE_FONT_SIZE
                typeface = alternativeFont
            }
        )
    }

    private fun drawMinutes(canvas: Canvas, bounds: Rect, zonedDateTime: ZonedDateTime) {
        canvas.drawMinutes(
            bounds = bounds,
            time = zonedDateTime,
            isInAlwaysOnDisplay = isInAlwaysOnDisplay(),
            ticksPaint = dialTicksPaint.apply {
                color = context.color(configuration.minutesDialTickColorId)
            },
            dialTextPaint = dialTextPaint.apply {
                color = context.color(configuration.minutesDialTextColorId)
                textSize = bounds.height() * XSMALL_FONT_SIZE
            },
            textPaint = textPaint.apply {
                color = context.color(configuration.minutesTextColorId)
                textSize = bounds.height() * MEDIUM_FONT_SIZE
                typeface = alternativeFont
            },
            backgroundPaint = backgroundPaint.apply {
                color = context.color(configuration.backgroundColorId)
                textSize = bounds.height() * LARGE_FONT_SIZE
            }
        )
    }

    private fun drawSeconds(canvas: Canvas, bounds: Rect, zonedDateTime: ZonedDateTime) {
        canvas.drawSeconds(
            bounds = bounds,
            isInAlwaysOnDisplay = isInAlwaysOnDisplay(),
            time = zonedDateTime,
            ticksPaint = dialTicksPaint.apply {
                color = context.color(configuration.secondsDialTickColorId)
            },
            textPaint = dialTextPaint.apply {
                color = context.color(configuration.secondsDialTextColorId)
                textSize = bounds.height() * SMALL_FONT_SIZE
            }
        )
    }

    private fun drawBorder(canvas: Canvas, bounds: Rect) {
        canvas.drawBorder(
            bounds = bounds,
            isInAlwaysOnDisplay = isInAlwaysOnDisplay(),
            isHalfCircle = configuration.style == HALF_DIAL,
            paint = borderPaint.apply {
                color = context.color(configuration.borderColorId)
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

    private fun typeface(weight: Int): Typeface = Typeface.create(font, weight, false)

    companion object {
        internal const val DEFAULT_CANVAS_TYPE = CanvasType.HARDWARE
        internal const val DEFAULT_REFRESH_RATE = 16L
    }
}
