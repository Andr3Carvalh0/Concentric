package pt.carvalho.concentricplus.renderer

import android.graphics.Bitmap
import android.graphics.Rect
import android.graphics.Typeface
import android.util.Log
import androidx.annotation.ColorInt
import androidx.wear.watchface.ComplicationSlotsManager
import androidx.wear.watchface.style.CurrentUserStyleRepository
import androidx.wear.watchface.style.UserStyle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import pt.carvalho.concentricplus.renderer.clock.clockDialText
import pt.carvalho.concentricplus.renderer.clock.clockDialTicks
import pt.carvalho.concentricplus.renderer.data.ConcentricConfiguration
import pt.carvalho.concentricplus.renderer.data.DEFAULT
import kotlin.math.max

internal class ConcentricController(
    private val styleRepository: CurrentUserStyleRepository,
    private val sharedAssets: ConcentricRendererAssets,
    private val complicationSlotsManager: ComplicationSlotsManager
) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    var configuration: ConcentricConfiguration = DEFAULT

    init {
        scope.launch {
            styleRepository.userStyle
                .collect { userStyle -> updateWatchFaceData(userStyle) }
        }
    }

    fun destroy() = scope.cancel()

    fun minutesTextMask(
        bounds: Rect,
        @ColorInt textColor: Int,
        textFont: Typeface
    ): Bitmap {
        sharedAssets.minutesDialTextMask?.let { return it }

        return text(
            bounds = bounds,
            textColor = textColor,
            margin = max(
                (bounds.width() * MINUTES_TEXT_PADDING),
                (bounds.height() * MINUTES_TEXT_PADDING)
            ),
            textFont = textFont
        ).also { sharedAssets.minutesDialTextMask = it }
    }

    fun minutesTicksMask(
        bounds: Rect,
        @ColorInt color: Int
    ): Bitmap {
        sharedAssets.minutesDialTicksMask?.let { return it }

        return ticks(
            bounds = bounds,
            color = color,
            marginX = (bounds.width() * MINUTES_TICKS_PADDING),
            marginY = (bounds.height() * MINUTES_TICKS_PADDING)
        ).also { sharedAssets.minutesDialTicksMask = it }
    }

    fun secondsTextMask(
        bounds: Rect,
        @ColorInt textColor: Int,
        textFont: Typeface
    ): Bitmap = text(
        bounds = bounds,
        textColor = textColor,
        textFont = textFont
    )

    fun secondsTicksMask(
        bounds: Rect,
        @ColorInt color: Int
    ): Bitmap {
        sharedAssets.secondsDialTicksMask?.let { return it }

        return ticks(bounds = bounds, color = color)
            .also { sharedAssets.secondsDialTicksMask = it }
    }

    private fun text(
        bounds: Rect,
        textFont: Typeface,
        @ColorInt textColor: Int,
        margin: Float = 0.0f
    ): Bitmap = clockDialText(
        bounds = bounds,
        textSize = SMALL_FONT_SIZE,
        textFont = textFont,
        textColor = textColor,
        margin = margin,
        isInverted = true
    )

    private fun ticks(
        bounds: Rect,
        @ColorInt color: Int,
        marginX: Float = 0.0f,
        marginY: Float = 0.0f
    ): Bitmap = clockDialTicks(
        bounds = bounds,
        ticksColor = color,
        marginX = marginX,
        marginY = marginY
    )

    private fun updateWatchFaceData(style: UserStyle) {
        Log.d("ConcentricController", "$style")
    }
}
