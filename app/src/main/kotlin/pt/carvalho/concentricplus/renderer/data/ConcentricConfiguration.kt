package pt.carvalho.concentricplus.renderer.data

import androidx.annotation.ColorRes
import pt.carvalho.concentricplus.R

internal data class ConcentricConfiguration(
    @ColorRes val hoursTextColorId: Int,
    @ColorRes val minutesLargeTextColorId: Int,
    @ColorRes val minutesTextColorId: Int,
    @ColorRes val minutesTickColorId: Int,
    @ColorRes val secondsTextColorId: Int,
    @ColorRes val secondsTickColorId: Int,
    @ColorRes val borderColorId: Int,
    @ColorRes val backgroundColorId: Int,
    val style: Style
) {
    internal enum class Style {
        DIAL_I,
        DIAL_II,
        HALF_DIAL
    }
}

internal val DEFAULT = ConcentricConfiguration(
    hoursTextColorId = R.color.background_focused_text_color,
    minutesLargeTextColorId = R.color.background_focused_text_color,
    minutesTextColorId = R.color.background_unfocused_text_color,
    minutesTickColorId = R.color.tick_indicator_color,
    secondsTextColorId = R.color.spearmint,
    secondsTickColorId = R.color.tick_indicator_color,
    borderColorId = R.color.spearmint,
    backgroundColorId = R.color.background_watch_face_color,
    style = ConcentricConfiguration.Style.DIAL_I
)
