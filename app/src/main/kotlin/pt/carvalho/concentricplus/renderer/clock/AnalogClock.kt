package pt.carvalho.concentricplus.renderer.clock

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Paint.Cap.ROUND
import android.graphics.Rect
import android.graphics.Typeface
import androidx.annotation.ColorInt
import java.time.ZonedDateTime

private const val MIN_SECONDS = 0
private const val MAX_SECONDS = 60

private const val PADDING = 0.98f

private const val TICKS_STROKE = 2.3f
private const val TICKS_MAJOR_LENGTH = 0.98f
private const val TICKS_MINOR_LENGTH = 0.99f

private const val ROTATION_MODIFIER = 6.0f

private const val HOUR_OFFSET = 0.14f
private const val HALF_MODIFIER = 0.5f

private const val MAJOR_MODIFIER = 5

internal fun Canvas.hoursTextMask(
    bounds: Rect,
    textSize: Float,
    textFont: Typeface,
    time: ZonedDateTime,
    @ColorInt textColor: Int
) {
    val timeValue = "%02d".format(time.hour)

    val paint = Paint()
        .apply {
            this.isAntiAlias = true
            this.typeface = textFont
            this.color = textColor
            this.textSize = bounds.height() * textSize
        }

    val textBounds = Rect().also {
        paint.getTextBounds(timeValue, 0, timeValue.length, it)
    }

    val x = bounds.centerX() - (textBounds.width() * (HALF_MODIFIER + HOUR_OFFSET))
    val y = bounds.centerY() + (textBounds.height() * HALF_MODIFIER)

    drawText(timeValue, x, y, paint)
}

internal fun Canvas.drawClockTicks(
    bounds: Rect,
    marginX: Float,
    marginY: Float,
    @ColorInt ticksColor: Int
) {
    val ticksPaint = Paint()
        .apply {
            isAntiAlias = true
            strokeCap = ROUND
            strokeWidth = TICKS_STROKE
            color = ticksColor
        }

    val transformedBounds = Rect(
        bounds.left + marginX.toInt(),
        bounds.top + marginY.toInt(),
        bounds.right - marginX.toInt(),
        bounds.bottom - marginY.toInt()
    )

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
        rotate(ROTATION_MODIFIER, transformedBounds.exactCenterX(), transformedBounds.exactCenterY())
    }
}

internal fun Canvas.drawMinorClockText(
    bounds: Rect,
    margin: Float,
    textSize: Float,
    textFont: Typeface,
    @ColorInt textColor: Int,
    isInverted: Boolean = false
) {
    val paint = Paint()
        .apply {
            isAntiAlias = true
            typeface = textFont
        }

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
        val y = bounds.height() * (1.0f - padding) + margin + textBounds.height() + ROTATION_MODIFIER

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
