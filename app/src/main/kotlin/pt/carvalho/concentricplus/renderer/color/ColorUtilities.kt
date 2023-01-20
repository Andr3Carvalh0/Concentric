package pt.carvalho.concentricplus.renderer.color

import android.graphics.Color

private const val MAX_COLOR_VALUE = 255

internal object ColorUtilities {

    fun Int.dim(value: Float): Int = Color.argb(
        Color.alpha(this),
        (Color.red(this) * value).toInt().coerceAtMost(MAX_COLOR_VALUE),
        (Color.green(this) * value).toInt().coerceAtMost(MAX_COLOR_VALUE),
        (Color.blue(this) * value).toInt().coerceAtMost(MAX_COLOR_VALUE)
    )
}
