@file:Suppress("MagicNumber")

package pt.carvalho.concentricplus.renderer

import android.graphics.RectF

internal const val LARGE_FONT_SIZE = 0.215f
internal const val MEDIUM_FONT_SIZE = 0.102f
internal const val SMALL_FONT_SIZE = 0.07f
internal const val XSMALL_FONT_SIZE = 0.06f

internal const val HOURS_X_OFFSET = 0.1f

internal const val MINUTES_TICKS_PADDING = 0.08f
internal const val MINUTES_X_OFFSET = 1.37f

internal const val LAYOUT_ALT_CLOCK_SHIFT = 0.357f

internal const val DIAL_MIN_VALUE = 0
internal const val DIAL_MAX_VALUE = 60
internal const val DIAL_TEXT_MARGIN = 0.043f
internal const val DIAL_TICKS_STROKE = 3.0f
internal const val DIAL_TICKS_MAJOR_LENGTH = 0.975f
internal const val DIAL_TICKS_MINOR_LENGTH = 0.985f
internal const val DIAL_ROTATION_MODIFIER = 6.0f

internal const val HALF_MODIFIER = 0.5f
internal const val MAJOR_MODIFIER = 5

internal const val BORDER_SIZE = 0.21f
internal const val BORDER_Y_OFFSET = 4.0f
internal const val BORDER_X_OFFSET = 1.27f
internal const val BORDER_X_OFFSET_ALWAYS_ON_DISPLAY = 1.31f
internal const val BORDER_X_HALF_DIAL_OVERFLOW = 1.0f
internal const val BORDER_X_OVERFLOW = 1.5f
internal const val BORDER_RADIUS = 48f
internal const val BORDER_THICKNESS = 3.0f
internal const val BORDER_THICKNESS_ALWAYS_ON_DISPLAY = 2f

internal const val BORDER_CLEAR_ALWAYS_ON_DISPLAY_OFFSET = 4.0f
internal const val BORDER_CLEAR_AREA_RECT_TOP = 0.42f
internal const val BORDER_CLEAR_AREA_RECT_RIGHT = 0.87f
internal const val BORDER_CLEAR_AREA_RECT_BOTTOM = 0.585f

internal const val DEFAULT_SCALE_FACTOR = 1.0f
internal const val ALWAYS_ON_DISPLAY_SCALE_FACTOR = 0.95f

internal val TOP_COMPLICATION_RECT = RectF(0.62f, 0.11f, 0.82f, 0.31f)
internal val MIDDLE_COMPLICATION_RECT = RectF(0.72f, 0.40f, 0.92f, 0.60f)
internal val BOTTOM_COMPLICATION_RECT = RectF(0.62f, 0.70f, 0.82f, 0.90f)

internal const val TEXT_ALIGNMENT_FACTOR = 0.025f
internal const val MAX_TEXT = "88"

internal const val A_THOUSAND = 1000f
internal const val SECONDS_NORMALIZE = 1000000
