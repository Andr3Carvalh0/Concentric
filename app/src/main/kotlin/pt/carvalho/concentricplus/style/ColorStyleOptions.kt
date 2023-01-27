package pt.carvalho.concentricplus.style

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Icon
import androidx.annotation.ColorRes
import androidx.wear.watchface.style.UserStyleSetting
import androidx.wear.watchface.style.UserStyleSetting.ListUserStyleSetting.ListOption
import pt.carvalho.concentricplus.R
import pt.carvalho.concentricplus.utilities.color

private const val ICON_PREVIEW_SIZE = 192

internal object ColorStyleOptions {

    internal val colors = mapOf(
        R.string.color_graphite_name to R.color.graphite, R.string.color_cloud_name to R.color.cloud,
        R.string.color_almond_name to R.color.almond, R.string.color_watermelon_name to R.color.watermelon,
        R.string.color_coral_name to R.color.coral, R.string.color_pomelo_name to R.color.pomelo,
        R.string.color_guava_name to R.color.guava, R.string.color_peach_name to R.color.peach,
        R.string.color_champagne_name to R.color.champagne, R.string.color_chai_name to R.color.chai,
        R.string.color_sand_name to R.color.sand, R.string.color_honey_name to R.color.honey,
        R.string.color_melon_name to R.color.melon, R.string.color_wheat_name to R.color.wheat,
        R.string.color_dandelion_name to R.color.dandelion, R.string.color_limoncello_name to R.color.limoncello,
        R.string.color_lemongrass_name to R.color.lemongrass, R.string.color_lime_name to R.color.lime,
        R.string.color_pear_name to R.color.pear, R.string.color_spearmint_name to R.color.spearmint,
        R.string.color_fern_name to R.color.fern, R.string.color_forest_name to R.color.forest,
        R.string.color_mint_name to R.color.mint, R.string.color_jade_name to R.color.jade,
        R.string.color_sage_name to R.color.sage, R.string.color_stream_name to R.color.stream,
        R.string.color_aqua_name to R.color.aqua, R.string.color_sky_name to R.color.sky,
        R.string.color_ocean_name to R.color.ocean, R.string.color_sapphire_name to R.color.sapphire,
        R.string.color_amethyst_name to R.color.amethyst, R.string.color_lilac_name to R.color.lilac,
        R.string.color_lavender_name to R.color.lavender, R.string.color_flamingo_name to R.color.flamingo,
        R.string.color_bubble_gum_name to R.color.bubble_gum
    )

    private fun iconPreview(
        context: Context,
        @ColorRes color: Int
    ): Icon {
        val bitmap = Bitmap.createBitmap(ICON_PREVIEW_SIZE, ICON_PREVIEW_SIZE, Bitmap.Config.ARGB_8888)

        Canvas(bitmap).also {
            it.drawColor(context.color(color))
        }

        return Icon.createWithBitmap(bitmap)
    }

    internal fun index(entry: Map.Entry<Int, Int>): String =
        "${colors.keys.indexOf(entry.key)}"

    fun options(context: Context): List<ListOption> {
        return colors.map { entry ->
            ListOption(
                id = UserStyleSetting.Option.Id(index(entry)),
                resources = context.resources,
                displayNameResourceId = entry.key,
                icon = iconPreview(context, entry.value)
            )
        }
    }
}
