package pt.carvalho.concentricplus.renderer.data

import androidx.annotation.ColorRes
import androidx.wear.watchface.ComplicationSlot
import pt.carvalho.concentricplus.R

internal data class ConcentricConfiguration(
    @ColorRes val hoursTextColorId: Int,
    @ColorRes val minutesTextColorId: Int,
    @ColorRes val minutesDialTextColorId: Int,
    @ColorRes val minutesDialTickColorId: Int,
    @ColorRes val secondsDialTextColorId: Int,
    @ColorRes val secondsDialTickColorId: Int,
    @ColorRes val complicationsTintColorId: Int,
    @ColorRes val borderColorId: Int,
    @ColorRes val backgroundColorId: Int,
    val style: Style,
    val complications: List<ComplicationSlot>
) {
    internal enum class Style {
        DIAL_I,
        DIAL_II,
        HALF_DIAL
    }
}

internal val DEFAULT = ConcentricConfiguration(
    hoursTextColorId = R.color.background_focused_text_color,
    minutesTextColorId = R.color.background_focused_text_color,
    minutesDialTextColorId = R.color.background_unfocused_text_color,
    minutesDialTickColorId = R.color.tick_indicator_color,
    secondsDialTextColorId = R.color.spearmint,
    secondsDialTickColorId = R.color.tick_indicator_color,
    complicationsTintColorId = R.color.spearmint,
    borderColorId = R.color.spearmint,
    backgroundColorId = R.color.background_watch_face_color,
    style = ConcentricConfiguration.Style.HALF_DIAL,
    complications = emptyList()
)
