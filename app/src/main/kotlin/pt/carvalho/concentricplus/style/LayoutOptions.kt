package pt.carvalho.concentricplus.style

import android.content.Context
import androidx.wear.watchface.style.UserStyleSetting
import androidx.wear.watchface.style.UserStyleSetting.ListUserStyleSetting.ListOption
import pt.carvalho.concentricplus.R

internal object LayoutOptions {

    internal val options = mapOf(
        R.string.layout_dial_i to R.drawable.preview,
        R.string.layout_dial_ii to R.drawable.preview,
        R.string.layout_half_dial to R.drawable.preview
    )

    internal fun index(entry: Map.Entry<Int, Int>): String =
        "${options.keys.indexOf(entry.key)}"

    fun options(context: Context): List<ListOption> =
        options.map { entry ->
            ListOption(
                id = UserStyleSetting.Option.Id(index(entry)),
                resources = context.resources,
                displayNameResourceId = entry.key,
                icon = null
            )
        }
}
