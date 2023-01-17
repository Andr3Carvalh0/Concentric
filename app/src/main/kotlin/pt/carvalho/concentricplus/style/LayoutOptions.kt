package pt.carvalho.concentricplus.style

import android.content.Context
import androidx.wear.watchface.style.UserStyleSetting
import androidx.wear.watchface.style.UserStyleSetting.ListUserStyleSetting.ListOption
import pt.carvalho.concentricplus.R

internal object LayoutOptions {

    private val options = listOf(
        R.string.layout_dial_i,
        R.string.layout_dial_ii,
        R.string.layout_half_dial
    )

    fun options(context: Context): List<ListOption> =
        options.map { resId ->
            ListOption(
                id = UserStyleSetting.Option.Id("$resId"),
                resources = context.resources,
                displayNameResourceId = resId,
                icon = null
            )
        }
}
