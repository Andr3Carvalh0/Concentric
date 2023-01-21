package pt.carvalho.concentricplus.utilities

import android.content.Context
import android.graphics.Typeface
import androidx.annotation.ColorRes
import androidx.annotation.FontRes

internal fun Context.color(@ColorRes id: Int): Int = getColor(id)

internal fun Context.font(@FontRes id: Int): Typeface = resources.getFont(id)
