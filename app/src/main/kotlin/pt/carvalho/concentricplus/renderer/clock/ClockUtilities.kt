package pt.carvalho.concentricplus.renderer.clock

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import pt.carvalho.concentricplus.renderer.BORDER_RADIUS
import pt.carvalho.concentricplus.renderer.BORDER_X_HALF_DIAL_OVERFLOW
import pt.carvalho.concentricplus.renderer.BORDER_X_OFFSET
import pt.carvalho.concentricplus.renderer.BORDER_X_OFFSET_ALWAYS_ON_DISPLAY
import pt.carvalho.concentricplus.renderer.BORDER_X_OVERFLOW
import pt.carvalho.concentricplus.renderer.HALF_MODIFIER
import pt.carvalho.concentricplus.renderer.MAX_TEXT
import pt.carvalho.concentricplus.renderer.MINUTES_X_OFFSET
import java.time.ZonedDateTime

internal fun Canvas.drawHours(
    bounds: Rect,
    time: ZonedDateTime,
    paint: Paint
) {
    drawText(
        bounds = bounds,
        timeValue = "%02d".format(time.hour),
        xOffset = { centerX, textWidth -> centerX - (textWidth * HALF_MODIFIER) },
        paint = paint
    )
}

internal fun Canvas.drawMinutes(
    bounds: Rect,
    time: ZonedDateTime,
    minutesTickBitmap: Bitmap,
    minutesTextBitmap: Bitmap,
    textPaint: Paint,
    bitmapPaint: Paint
) {
    drawBitmap(minutesTickBitmap, 0.0f, 0.0f, bitmapPaint)
    drawBitmap(minutesTextBitmap, 0.0f, 0.0f, bitmapPaint)
    drawText(
        bounds = bounds,
        timeValue = "%02d".format(time.minute),
        xOffset = { centerX, _ -> centerX * MINUTES_X_OFFSET },
        paint = textPaint
    )
}

internal fun Canvas.drawSeconds(
    isInAlwaysOnDisplay: Boolean,
    bitmapPaint: Paint,
    time: ZonedDateTime,
    secondsTickBitmap: Bitmap,
    secondsTextBitmap: Bitmap
) {
    drawBitmap(secondsTickBitmap, 0.0f, 0.0f, bitmapPaint)

    if (!isInAlwaysOnDisplay) {
        drawBitmap(secondsTextBitmap, 0.0f, 0.0f, bitmapPaint)
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
    val startY = bounds.centerY() - (textBounds.height() * HALF_MODIFIER)
    val endY = bounds.centerY() + (textBounds.height() * HALF_MODIFIER)
    val endX = when {
        isInAlwaysOnDisplay -> startX + (endY - startY)
        else -> bounds.width().toFloat() * overflow
    }

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
