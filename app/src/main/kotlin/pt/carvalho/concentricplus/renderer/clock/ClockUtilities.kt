package pt.carvalho.concentricplus.renderer.clock

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import pt.carvalho.concentricplus.renderer.A_THOUSAND
import pt.carvalho.concentricplus.renderer.BORDER_CLEAR_ALWAYS_ON_DISPLAY_OFFSET
import pt.carvalho.concentricplus.renderer.BORDER_CLEAR_AREA_RECT_BOTTOM
import pt.carvalho.concentricplus.renderer.BORDER_CLEAR_AREA_RECT_RIGHT
import pt.carvalho.concentricplus.renderer.BORDER_CLEAR_AREA_RECT_TOP
import pt.carvalho.concentricplus.renderer.BORDER_RADIUS
import pt.carvalho.concentricplus.renderer.BORDER_X_HALF_DIAL_OVERFLOW
import pt.carvalho.concentricplus.renderer.BORDER_X_OFFSET
import pt.carvalho.concentricplus.renderer.BORDER_X_OFFSET_ALWAYS_ON_DISPLAY
import pt.carvalho.concentricplus.renderer.BORDER_X_OVERFLOW
import pt.carvalho.concentricplus.renderer.BORDER_Y_OFFSET
import pt.carvalho.concentricplus.renderer.DEFAULT_SCALE_FACTOR
import pt.carvalho.concentricplus.renderer.DIAL_MAX_VALUE
import pt.carvalho.concentricplus.renderer.DIAL_MIN_VALUE
import pt.carvalho.concentricplus.renderer.DIAL_ROTATION_MODIFIER
import pt.carvalho.concentricplus.renderer.DIAL_TEXT_MARGIN
import pt.carvalho.concentricplus.renderer.DIAL_TICKS_MAJOR_LENGTH
import pt.carvalho.concentricplus.renderer.DIAL_TICKS_MINOR_LENGTH
import pt.carvalho.concentricplus.renderer.HALF_MODIFIER
import pt.carvalho.concentricplus.renderer.HOURS_X_OFFSET
import pt.carvalho.concentricplus.renderer.MAJOR_MODIFIER
import pt.carvalho.concentricplus.renderer.MAX_TEXT
import pt.carvalho.concentricplus.renderer.MINUTES_TICKS_PADDING
import pt.carvalho.concentricplus.renderer.MINUTES_X_OFFSET
import pt.carvalho.concentricplus.renderer.SECONDS_NORMALIZE
import pt.carvalho.concentricplus.renderer.TEXT_ALIGNMENT_FACTOR
import pt.carvalho.concentricplus.utilities.restoreToCountAfter
import java.time.ZonedDateTime

internal fun Canvas.drawHours(
    bounds: Rect,
    time: ZonedDateTime,
    paint: Paint
) {
    drawText(
        bounds = bounds,
        timeValue = "%02d".format(time.hour),
        xOffset = { centerX, textWidth -> centerX - (textWidth * (HALF_MODIFIER + HOURS_X_OFFSET)) },
        paint = paint
    )
}

internal fun Canvas.drawMinutes(
    bounds: Rect,
    time: ZonedDateTime,
    isInAlwaysOnDisplay: Boolean,
    ticksPaint: Paint,
    dialTextPaint: Paint,
    textPaint: Paint,
    hasTicks: Boolean,
    backgroundPaint: Paint
) {
    restoreToCountAfter {
        val minutes = time.minute
        val seconds = time.second

        val calculateDegrees: (Int) -> Float = { value ->
            val secondsOffset = seconds + if (seconds % 2 != 0) 1 else 0

            (minutes - value) * DIAL_ROTATION_MODIFIER +
                (secondsOffset * DIAL_ROTATION_MODIFIER) / DIAL_MAX_VALUE
        }

        dial(
            bounds = bounds,
            marginX = (bounds.width() * MINUTES_TICKS_PADDING),
            marginY = (bounds.height() * MINUTES_TICKS_PADDING),
            ticksPaint = ticksPaint,
            textPaint = dialTextPaint,
            hasTicks = hasTicks,
            rotationDegrees = { calculateDegrees(0) },
            textRotationDegrees = calculateDegrees,
            beforeTicksDrawn = {
                clearActiveMinute(
                    bounds = bounds,
                    isInAlwaysOnDisplay = isInAlwaysOnDisplay,
                    paint = backgroundPaint
                )
            }
        )
    }

    drawText(
        bounds = bounds,
        timeValue = "%02d".format(time.minute),
        xOffset = { centerX, _ -> centerX * MINUTES_X_OFFSET },
        paint = textPaint
    )
}

internal fun Canvas.drawSeconds(
    bounds: Rect,
    isInAlwaysOnDisplay: Boolean,
    ticksPaint: Paint,
    textPaint: Paint,
    hasTicks: Boolean,
    time: ZonedDateTime
) {
    restoreToCountAfter {
        val seconds = time.second
        val nanoseconds = time.nano / SECONDS_NORMALIZE

        dial(
            bounds = bounds,
            rotationDegrees = {
                when {
                    isInAlwaysOnDisplay -> 0.0f
                    else ->
                        seconds * DIAL_ROTATION_MODIFIER +
                            (nanoseconds / A_THOUSAND) * DIAL_ROTATION_MODIFIER
                }
            },
            textRotationDegrees = { value ->
                (seconds - value) * DIAL_ROTATION_MODIFIER +
                    (nanoseconds / A_THOUSAND) * DIAL_ROTATION_MODIFIER
            },
            hasText = !isInAlwaysOnDisplay,
            ticksPaint = ticksPaint,
            hasTicks = hasTicks,
            textPaint = textPaint
        )
    }
}

internal fun Canvas.drawBorder(
    bounds: Rect,
    isInAlwaysOnDisplay: Boolean,
    isHalfCircle: Boolean,
    paint: Paint
) {
    val textBounds = Rect().also {
        paint.getTextBounds(MAX_TEXT, 0, MAX_TEXT.length, it)
    }

    val overflow = when {
        isHalfCircle -> BORDER_X_HALF_DIAL_OVERFLOW
        else -> BORDER_X_OVERFLOW
    }

    val xOffset = when {
        isInAlwaysOnDisplay -> BORDER_X_OFFSET_ALWAYS_ON_DISPLAY
        else -> BORDER_X_OFFSET
    }

    val startX = bounds.centerX() * xOffset
    val startY = bounds.centerY() - (textBounds.height() * HALF_MODIFIER) *
        (DEFAULT_SCALE_FACTOR - TEXT_ALIGNMENT_FACTOR) - BORDER_Y_OFFSET
    val endY = bounds.centerY() + (textBounds.height() * HALF_MODIFIER) + BORDER_Y_OFFSET
    val endX = when {
        isInAlwaysOnDisplay -> startX + (endY - startY)
        else -> bounds.width().toFloat() * overflow
    } + (DEFAULT_SCALE_FACTOR + TEXT_ALIGNMENT_FACTOR)

    drawRoundRect(
        startX, startY, endX, endY, BORDER_RADIUS, BORDER_RADIUS, paint
    )
}

private fun Canvas.drawText(
    bounds: Rect,
    xOffset: (Int, Int) -> Float,
    timeValue: String,
    paint: Paint
) {
    val textBounds = Rect().also {
        paint.getTextBounds(MAX_TEXT, 0, MAX_TEXT.length, it)
    }

    val x = xOffset(bounds.centerX(), textBounds.width())
    val y = bounds.centerY() + (textBounds.height() * HALF_MODIFIER)

    drawText(timeValue, x, y, paint)
}

/**
 * Handles the drawing and rotation of the dial "hands".
 * Code hugely inspired by: https://github.com/daverein/WearOSConcentricWatchface
 **/
private fun Canvas.dial(
    bounds: Rect,
    marginX: Float = 0.0f,
    marginY: Float = 0.0f,
    rotationDegrees: () -> Float,
    textRotationDegrees: (Int) -> Float,
    beforeTicksDrawn: () -> Unit = { },
    hasText: Boolean = true,
    hasTicks: Boolean = true,
    ticksPaint: Paint,
    textPaint: Paint
) {
    val transformedBounds = Rect(
        bounds.left + marginX.toInt(),
        bounds.top + marginY.toInt(),
        bounds.right - marginX.toInt(),
        bounds.bottom - marginY.toInt()
    )

    if (hasText) {
        drawDialText(
            bounds = transformedBounds,
            margin = DIAL_TEXT_MARGIN,
            rotationDegrees = rotationDegrees,
            textRotationDegrees = textRotationDegrees,
            paint = textPaint
        )
    }

    beforeTicksDrawn()

    if (hasTicks) {
        drawDialTicks(
            bounds = transformedBounds,
            rotationDegrees = rotationDegrees,
            paint = ticksPaint
        )
    }
}

private fun Canvas.drawDialTicks(
    bounds: Rect,
    rotationDegrees: () -> Float,
    paint: Paint
) {
    restoreToCountAfter {
        rotate(rotationDegrees(), bounds.exactCenterX(), bounds.exactCenterY())

        (DIAL_MIN_VALUE until DIAL_MAX_VALUE).forEach { value ->
            val isMajorValue = value % MAJOR_MODIFIER == 0

            val tickLength = if (isMajorValue) DIAL_TICKS_MAJOR_LENGTH else DIAL_TICKS_MINOR_LENGTH

            drawLine(
                bounds.width().toFloat() * tickLength,
                bounds.exactCenterY(),
                bounds.width().toFloat(),
                bounds.exactCenterY(),
                paint
            )

            rotate(
                DIAL_ROTATION_MODIFIER,
                bounds.exactCenterX(),
                bounds.exactCenterY()
            )
        }
    }
}

private fun Canvas.drawDialText(
    bounds: Rect,
    margin: Float,
    rotationDegrees: () -> Float,
    textRotationDegrees: (Int) -> Float,
    paint: Paint
) {
    restoreToCountAfter {
        rotate(rotationDegrees(), bounds.exactCenterX(), bounds.exactCenterY())

        (DIAL_MIN_VALUE until DIAL_MAX_VALUE).forEach { value ->
            val isMajorValue = value % MAJOR_MODIFIER == 0

            if (isMajorValue) {
                val textValue = "%02d".format(value % DIAL_MAX_VALUE)

                val textBounds = Rect().also {
                    paint.getTextBounds(textValue, 0, textValue.length, it)
                }

                restoreToCountAfter {
                    val x = bounds.width() - (textBounds.width() + bounds.width() * margin)
                    val y = bounds.exactCenterY() + (textBounds.height() * HALF_MODIFIER)
                    rotate(
                        -textRotationDegrees(value),
                        x + textBounds.width() * HALF_MODIFIER,
                        y - textBounds.height() * HALF_MODIFIER
                    )
                    drawText(textValue, x, y, paint)
                }
            }

            rotate(
                -DIAL_ROTATION_MODIFIER,
                bounds.exactCenterX(),
                bounds.exactCenterY()
            )
        }
    }
}

private fun Canvas.clearActiveMinute(
    bounds: Rect,
    isInAlwaysOnDisplay: Boolean,
    paint: Paint
) {
    if (isInAlwaysOnDisplay) {
        val textBounds = Rect().also {
            paint.getTextBounds(MAX_TEXT, 0, MAX_TEXT.length, it)
        }

        val startX = bounds.centerX() * BORDER_X_OFFSET_ALWAYS_ON_DISPLAY
        val startY = bounds.centerY() - (textBounds.height() * HALF_MODIFIER) *
            (DEFAULT_SCALE_FACTOR - TEXT_ALIGNMENT_FACTOR) - BORDER_CLEAR_ALWAYS_ON_DISPLAY_OFFSET
        val endY = bounds.centerY() + (textBounds.height() * HALF_MODIFIER) +
            BORDER_CLEAR_ALWAYS_ON_DISPLAY_OFFSET
        val endX = startX + (endY - startY) + (DEFAULT_SCALE_FACTOR + TEXT_ALIGNMENT_FACTOR)

        drawRoundRect(
            startX, startY, endX, endY, BORDER_RADIUS, BORDER_RADIUS, paint
        )
    } else {
        drawRect(
            bounds.exactCenterX(),
            bounds.height() * BORDER_CLEAR_AREA_RECT_TOP,
            bounds.width() * BORDER_CLEAR_AREA_RECT_RIGHT,
            bounds.height() * BORDER_CLEAR_AREA_RECT_BOTTOM,
            paint
        )
    }
}
