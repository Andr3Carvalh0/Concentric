package pt.carvalho.concentricplus.renderer.clock

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Paint.Cap.ROUND
import android.graphics.Rect
import android.graphics.Typeface
import androidx.annotation.ColorInt
import java.time.ZonedDateTime

internal fun Canvas.hoursTextMask(
    bounds: Rect,
    textSize: Float,
    textFont: Typeface,
    time: ZonedDateTime,
    @ColorInt textColor: Int
) {
    drawLargeText(
        bounds = bounds,
        textSize = textSize,
        textFont = textFont,
        timeValue = "%02d".format(time.hour),
        textColor = textColor,
        xOffset = { centerX, textWidth -> centerX - (textWidth * HALF_MODIFIER) }
    )
}

internal fun Canvas.minutesTextMask(
    bounds: Rect,
    textSize: Float,
    textFont: Typeface,
    time: ZonedDateTime,
    @ColorInt textColor: Int
) {
    drawLargeText(
        bounds = bounds,
        textSize = textSize,
        textFont = textFont,
        timeValue = "%02d".format(time.minute),
        textColor = textColor,
        xOffset = { centerX, _ -> centerX * MINUTES_X_OFFSET }
    )
}

internal fun Canvas.clockTicks(
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

internal fun Canvas.clockText(
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

private fun Canvas.drawLargeText(
    bounds: Rect,
    textSize: Float,
    textFont: Typeface,
    xOffset: (Int, Int) -> Float,
    timeValue: String,
    @ColorInt textColor: Int
) {
    val paint = Paint()
        .apply {
            this.isAntiAlias = true
            this.typeface = textFont
            this.color = textColor
            this.textSize = bounds.height() * textSize
        }

    val textBounds = Rect().also {
        paint.getTextBounds(MAX_TEXT, 0, MAX_TEXT.length, it)
    }

    val x = xOffset(bounds.centerX(), textBounds.width())
    val y = bounds.centerY() + (textBounds.height() * HALF_MODIFIER)

    drawText(timeValue, x, y, paint)
}
