package pt.carvalho.concentricplus.renderer.clock

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import androidx.annotation.ColorInt

internal fun clockTicks(
    bounds: Rect,
    marginX: Float,
    marginY: Float,
    @ColorInt ticksColor: Int
): Bitmap {
    val ticksPaint = Paint()
        .apply {
            isAntiAlias = true
            strokeCap = Paint.Cap.ROUND
            strokeWidth = TICKS_STROKE
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
                (MIN_SECONDS until MAX_SECONDS).forEach { value ->
                    val isMajorValue = value % MAJOR_MODIFIER == 0
                    val tickLength = if (isMajorValue) TICKS_MAJOR_LENGTH else TICKS_MINOR_LENGTH

                    drawLine(
                        transformedBounds.width().toFloat() * tickLength,
                        transformedBounds.exactCenterY(),
                        transformedBounds.width().toFloat(),
                        transformedBounds.exactCenterY(),
                        ticksPaint
                    )
                    rotate(
                        ROTATION_MODIFIER,
                        transformedBounds.exactCenterX(),
                        transformedBounds.exactCenterY()
                    )
                }
            }
        }
}

internal fun clockText(
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
                (MIN_SECONDS until MAX_SECONDS).forEach { value ->
                    val isMajorValue = value % MAJOR_MODIFIER == 0
                    val textValue = (MAX_SECONDS - value)
                        .let { seconds -> if (isInverted) seconds else value }
                        .let { seconds -> if (seconds == MAX_SECONDS) 0 else seconds }
                        .let { seconds -> "%02d".format(seconds) }

                    val padding = if (isMajorValue) PADDING else 0.0f
                    val fontSize = if (isMajorValue) textSize else 0.0f

                    paint.color = if (isMajorValue) textColor else Color.TRANSPARENT
                    paint.textSize = bounds.height() * fontSize
                    val textBounds = Rect().also {
                        paint.getTextBounds(textValue, 0, textValue.length, it)
                    }

                    val x = bounds.centerX() - (textBounds.width() * HALF_MODIFIER)
                    val y = bounds.height() * (1.0f - padding) + margin +
                        textBounds.height() + ROTATION_MODIFIER

                    save()
                    rotate(
                        -value * ROTATION_MODIFIER,
                        x + textBounds.width().toFloat() * HALF_MODIFIER,
                        y - textBounds.height().toFloat() * HALF_MODIFIER
                    )

                    drawText(textValue, x, y, paint)
                    restore()

                    rotate(ROTATION_MODIFIER, bounds.exactCenterX(), bounds.exactCenterY())
                }
            }
        }
}
