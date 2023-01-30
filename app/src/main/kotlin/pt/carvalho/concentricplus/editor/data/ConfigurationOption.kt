package pt.carvalho.concentricplus.editor.data

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

internal sealed class ConfigurationOption {

    abstract val id: String
    @get:StringRes
    abstract val title: Int
    abstract val isSelected: Boolean

    data class ColorOption(
        override val id: String,
        @ColorRes val color: Int,
        @StringRes override val title: Int,
        override val isSelected: Boolean
    ) : ConfigurationOption()

    data class LayoutOption(
        override val id: String,
        @DrawableRes val icon: Int,
        @StringRes override val title: Int,
        override val isSelected: Boolean
    ) : ConfigurationOption()
}
