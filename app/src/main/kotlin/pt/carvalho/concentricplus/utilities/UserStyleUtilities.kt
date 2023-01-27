package pt.carvalho.concentricplus.utilities

import androidx.wear.watchface.style.UserStyle
import androidx.wear.watchface.style.UserStyleSetting
import androidx.wear.watchface.style.UserStyleSetting.ListUserStyleSetting.ListOption
import pt.carvalho.concentricplus.renderer.data.ConcentricConfiguration.Style
import pt.carvalho.concentricplus.style.COLOR_STYLE
import pt.carvalho.concentricplus.style.ColorStyleOptions
import pt.carvalho.concentricplus.style.LAYOUT_STYLE

internal fun UserStyle.selectedColorKey(): String {
    return (this[UserStyleSetting.Id(COLOR_STYLE)] as ListOption).id.toString()
}

internal fun UserStyle.selectedLayoutKey(): String {
    return (this[UserStyleSetting.Id(LAYOUT_STYLE)] as ListOption).id.toString()
}

internal fun UserStyle.selectedColorValue(): Int {
    return ColorStyleOptions.colors.values
        .toList()[selectedColorKey().toInt()]
}

internal fun UserStyle.selectedLayoutValue(): Style {
    return when (selectedLayoutKey().toInt()) {
        0 -> Style.DIAL_I
        1 -> Style.DIAL_II
        else -> Style.HALF_DIAL
    }
}

internal fun UserStyle.isHalfDialLayout(): Boolean =
    selectedLayoutValue() == Style.HALF_DIAL
