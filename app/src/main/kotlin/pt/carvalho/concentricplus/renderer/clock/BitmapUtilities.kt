package pt.carvalho.concentricplus.renderer.clock

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import androidx.annotation.ColorInt
import pt.carvalho.concentricplus.renderer.DIAL_MAX_VALUE
import pt.carvalho.concentricplus.renderer.DIAL_MIN_VALUE
import pt.carvalho.concentricplus.renderer.DIAL_ROTATION_MODIFIER
import pt.carvalho.concentricplus.renderer.DIAL_TEXT_PADDING
import pt.carvalho.concentricplus.renderer.DIAL_TICKS_MAJOR_LENGTH
import pt.carvalho.concentricplus.renderer.DIAL_TICKS_MINOR_LENGTH
import pt.carvalho.concentricplus.renderer.DIAL_TICKS_STROKE
import pt.carvalho.concentricplus.renderer.HALF_MODIFIER
import pt.carvalho.concentricplus.renderer.MAJOR_MODIFIER

internal fun clockDialTicks(
    bounds: Rect,
    marginX: Float,
    marginY: Float,
    @ColorInt ticksColor: Int
): Bitmap {
    val ticksPaint = Paint()
        .apply {
            isAntiAlias = true
            strokeCap = Paint.Cap.ROUND
            strokeWidth = DIAL_TICKS_STROKE
            color = ticksColor
        }

    val transformedBounds = Rect(
        bounds.left + marginX.toInt(),
        bounds.top + marginY.toInt(),
        bounds.right - marginX.toInt(),
        bounds.bottom - marginY.toInt()
    )

    return Bitmap.createBitmap(bounds.width(), bounds.height(), Bitmap.Config.ARGB_8888)
        .also {
            with(Canvas(it)) {
                (DIAL_MIN_VALUE until DIAL_MAX_VALUE).forEach { value ->
                    val isMajorValue = value % MAJOR_MODIFIER == 0
                    val tickLength = if (isMajorValue) DIAL_TICKS_MAJOR_LENGTH else DIAL_TICKS_MINOR_LENGTH

                    drawLine(
                        transformedBounds.width().toFloat() * tickLength,
                        transformedBounds.exactCenterY(),
                        transformedBounds.width().toFloat(),
                        transformedBounds.exactCenterY(),
                        ticksPaint
                    )
                    rotate(
                        DIAL_ROTATION_MODIFIER,
                        transformedBounds.exactCenterX(),
                        transformedBounds.exactCenterY()
                    )
                }
            }
        }
}

internal fun clockDialText(
    bounds: Rect,
    margin: Float,
    textSize: Float,
    textFont: Typeface,
    @ColorInt textColor: Int,
    isInverted: Boolean = false
): Bitmap {
    val paint = Paint()
        .apply {
            isAntiAlias = true
            typeface = textFont
        }

    return Bitmap.createBitmap(bounds.width(), bounds.height(), Bitmap.Config.ARGB_8888)
        .also {
            with(Canvas(it)) {
                (DIAL_MIN_VALUE until DIAL_MAX_VALUE).forEach { value ->
                    val isMajorValue = value % MAJOR_MODIFIER == 0
                    val textValue = (DIAL_MAX_VALUE - value)
                        .let { seconds -> if (isInverted) seconds else value }
                        .let { seconds -> if (seconds == DIAL_MAX_VALUE) 0 else seconds }
                        .let { seconds -> "%02d".format(seconds) }

                    val padding = if (isMajorValue) DIAL_TEXT_PADDING else 0.0f
                    val fontSize = if (isMajorValue) textSize else 0.0f

                    paint.color = if (isMajorValue) textColor else Color.TRANSPARENT
                    paint.textSize = bounds.height() * fontSize
                    val textBounds = Rect().also {
                        paint.getTextBounds(textValue, 0, textValue.length, it)
                    }

                    val x = bounds.centerX() - (textBounds.width() * HALF_MODIFIER)
                    val y = bounds.height() * (1.0f - padding) + margin +
                        textBounds.height() + DIAL_ROTATION_MODIFIER

                    save()
                    rotate(
                        -value * DIAL_ROTATION_MODIFIER,
                        x + textBounds.width().toFloat() * HALF_MODIFIER,
                        y - textBounds.height().toFloat() * HALF_MODIFIER
                    )

                    drawText(textValue, x, y, paint)
                    restore()

                    rotate(DIAL_ROTATION_MODIFIER, bounds.exactCenterX(), bounds.exactCenterY())
                }
            }
        }
}
