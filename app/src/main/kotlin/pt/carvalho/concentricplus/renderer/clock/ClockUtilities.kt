package pt.carvalho.concentricplus.renderer.clock

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import java.time.ZonedDateTime

internal fun Canvas.drawHours(
    bounds: Rect,
    time: ZonedDateTime,
    paint: Paint
) {
    drawLargeText(
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
    drawLargeText(
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

private fun Canvas.drawLargeText(
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
