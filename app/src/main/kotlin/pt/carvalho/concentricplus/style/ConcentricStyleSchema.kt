package pt.carvalho.concentricplus.style

import android.content.Context
import androidx.wear.watchface.style.UserStyleSchema
import androidx.wear.watchface.style.UserStyleSetting
import androidx.wear.watchface.style.WatchFaceLayer
import pt.carvalho.concentricplus.R

private const val COLOR_STYLE = "colors"
private const val LAYOUT_STYLE = "layout"

private val DEFAULT_LAYERS = listOf(
    WatchFaceLayer.BASE,
    WatchFaceLayer.COMPLICATIONS,
    WatchFaceLayer.COMPLICATIONS_OVERLAY
)

internal object ConcentricStyleSchema {

    fun create(context: Context): UserStyleSchema {
        return UserStyleSchema(
            listOf(
                colorOptions(context),
                layoutOptions(context)
            )
        )
    }

    private fun colorOptions(context: Context): UserStyleSetting =
        UserStyleSetting.ListUserStyleSetting(
            id = UserStyleSetting.Id(COLOR_STYLE),
            resources = context.resources,
            displayNameResourceId = R.string.color_option_name,
            descriptionResourceId = R.string.color_option_description,
            icon = null,
            options = ColorStyleOptions.options(context),
            affectsWatchFaceLayers = DEFAULT_LAYERS
        )

    private fun layoutOptions(context: Context): UserStyleSetting =
        UserStyleSetting.ListUserStyleSetting(
            id = UserStyleSetting.Id(LAYOUT_STYLE),
            resources = context.resources,
            displayNameResourceId = R.string.layout_option_name,
            descriptionResourceId = R.string.layout_option_description,
            icon = null,
            options = LayoutOptions.options(context),
            affectsWatchFaceLayers = DEFAULT_LAYERS
        )
}
