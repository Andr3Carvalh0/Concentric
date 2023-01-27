package pt.carvalho.concentricplus.complications

import android.content.Context
import android.graphics.RectF
import androidx.wear.watchface.ComplicationSlot
import androidx.wear.watchface.ComplicationSlotsManager
import androidx.wear.watchface.complications.ComplicationSlotBounds
import androidx.wear.watchface.complications.DefaultComplicationDataSourcePolicy
import androidx.wear.watchface.complications.SystemDataSources
import androidx.wear.watchface.complications.data.ComplicationType
import androidx.wear.watchface.complications.rendering.CanvasComplicationDrawable
import androidx.wear.watchface.complications.rendering.ComplicationDrawable
import androidx.wear.watchface.style.CurrentUserStyleRepository
import pt.carvalho.concentricplus.R
import pt.carvalho.concentricplus.renderer.BOTTOM_COMPLICATION_RECT
import pt.carvalho.concentricplus.renderer.MIDDLE_COMPLICATION_RECT
import pt.carvalho.concentricplus.renderer.TOP_COMPLICATION_RECT

internal const val TOP_COMPLICATION_ID = 0
internal const val MIDDLE_COMPLICATION_ID = 1
internal const val BOTTOM_COMPLICATION_ID = 2

private val COMPLICATIONS_CAPABILITIES = listOf(
    ComplicationType.RANGED_VALUE,
    ComplicationType.SHORT_TEXT,
    ComplicationType.MONOCHROMATIC_IMAGE,
    ComplicationType.SMALL_IMAGE,
    ComplicationType.PHOTO_IMAGE
)

private val COMPLICATIONS: Map<Int, RectF> = mapOf(
    TOP_COMPLICATION_ID to TOP_COMPLICATION_RECT,
    MIDDLE_COMPLICATION_ID to MIDDLE_COMPLICATION_RECT,
    BOTTOM_COMPLICATION_ID to BOTTOM_COMPLICATION_RECT
)

internal object ConcentricComplicationsManager {

    fun create(context: Context, repository: CurrentUserStyleRepository): ComplicationSlotsManager {
        return ComplicationSlotsManager(
            COMPLICATIONS.map { (id, rect) ->
                val drawable = ComplicationDrawable.getDrawable(context, R.drawable.complication_icon_style)
                    ?: throw IllegalStateException("Failed to get complications drawable")

                ComplicationSlot.createRoundRectComplicationSlotBuilder(
                    id = id,
                    canvasComplicationFactory = { watchState, listener ->
                        CanvasComplicationDrawable(drawable, watchState, listener)
                    },
                    supportedTypes = COMPLICATIONS_CAPABILITIES,
                    defaultDataSourcePolicy = DefaultComplicationDataSourcePolicy(
                        SystemDataSources.NO_DATA_SOURCE,
                        ComplicationType.SHORT_TEXT
                    ),
                    bounds = ComplicationSlotBounds(rect)
                ).build()
            },
            repository
        )
    }
}
