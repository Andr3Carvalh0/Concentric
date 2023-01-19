package pt.carvalho.concentricplus.renderer

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import androidx.annotation.ColorInt
import androidx.wear.watchface.Renderer
import pt.carvalho.concentricplus.R
import pt.carvalho.concentricplus.renderer.clock.drawClockTicks
import pt.carvalho.concentricplus.renderer.clock.drawMinorClockText
import pt.carvalho.concentricplus.utilities.color
import pt.carvalho.concentricplus.utilities.font
import java.lang.Float.max

internal const val LARGE_FONT_SIZE = 0.20f
internal const val MEDIUM_FONT_SIZE = 0.14f
internal const val SMALL_FONT_SIZE = 0.06f

internal const val MINUTES_TICKS_PADDING = 0.07f
internal const val MINUTES_TEXT_PADDING = 0.14f

internal class ConcentricRendererAssets(
    private val context: Context
) : Renderer.SharedAssets {

    private var _minutesTicksMask: Bitmap? = null
    private var _secondsTicksMask: Bitmap? = null
    private var _secondsTextMask: Bitmap? = null

    fun minutesTextMask(bounds: Rect): Bitmap =
        text(
            bounds = bounds,
            textColor = context.color(R.color.background_minutes_color),
            margin = max(
                (bounds.width() * MINUTES_TEXT_PADDING),
                (bounds.height() * MINUTES_TEXT_PADDING)
            )
        )

    fun minutesTicksMask(bounds: Rect): Bitmap {
        _minutesTicksMask?.let { return it }

        return ticks(
            bounds = bounds,
            marginX = (bounds.width() * MINUTES_TICKS_PADDING),
            marginY = (bounds.height() * MINUTES_TICKS_PADDING)
        ).also { _minutesTicksMask = it }
    }

    fun secondsTextMask(bounds: Rect): Bitmap {
        _secondsTextMask?.let { return it }

        return text(
            bounds = bounds,
            textColor = context.color(R.color.spearmint)
        ).also { _secondsTextMask = it }
    }

    fun secondsTicksMask(bounds: Rect): Bitmap {
        _secondsTicksMask?.let { return it }

        return ticks(bounds).also { _secondsTicksMask = it }
    }

    private fun text(
        bounds: Rect,
        @ColorInt textColor: Int,
        margin: Float = 0.0f
    ): Bitmap {
        return Bitmap.createBitmap(bounds.width(), bounds.height(), Bitmap.Config.ARGB_8888)
            .also {
                Canvas(it)
                    .drawMinorClockText(
                        bounds = bounds,
                        textSize = SMALL_FONT_SIZE,
                        textFont = context.font(R.font.google_sans),
                        textColor = textColor,
                        margin = margin,
                        isInverted = true
                    )
            }
    }

    private fun ticks(
        bounds: Rect,
        marginX: Float = 0.0f,
        marginY: Float = 0.0f
    ): Bitmap {
        return Bitmap.createBitmap(bounds.width(), bounds.height(), Bitmap.Config.ARGB_8888)
            .also {
                Canvas(it)
                    .drawClockTicks(
                        bounds = bounds,
                        ticksColor = context.color(R.color.tick_indicator_color),
                        marginX = marginX,
                        marginY = marginY
                    )
            }
    }

    override fun onDestroy() {
        _minutesTicksMask?.recycle()
        _secondsTicksMask?.recycle()
        _secondsTextMask?.recycle()

        _minutesTicksMask = null
        _secondsTicksMask = null
        _secondsTextMask = null
    }
}
