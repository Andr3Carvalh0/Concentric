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
import pt.carvalho.concentricplus.renderer.BOTTOM_COMPLICATION_BOTTOM_BOUND
import pt.carvalho.concentricplus.renderer.BOTTOM_COMPLICATION_LEFT_BOUND
import pt.carvalho.concentricplus.renderer.BOTTOM_COMPLICATION_RIGHT_BOUND
import pt.carvalho.concentricplus.renderer.BOTTOM_COMPLICATION_TOP_BOUND
import pt.carvalho.concentricplus.renderer.MIDDLE_COMPLICATION_BOTTOM_BOUND
import pt.carvalho.concentricplus.renderer.MIDDLE_COMPLICATION_LEFT_BOUND
import pt.carvalho.concentricplus.renderer.MIDDLE_COMPLICATION_RIGHT_BOUND
import pt.carvalho.concentricplus.renderer.MIDDLE_COMPLICATION_TOP_BOUND
import pt.carvalho.concentricplus.renderer.TOP_COMPLICATION_BOTTOM_BOUND
import pt.carvalho.concentricplus.renderer.TOP_COMPLICATION_LEFT_BOUND
import pt.carvalho.concentricplus.renderer.TOP_COMPLICATION_RIGHT_BOUND
import pt.carvalho.concentricplus.renderer.TOP_COMPLICATION_TOP_BOUND

private const val TOP_COMPLICATION_ID = 0
private const val MIDDLE_COMPLICATION_ID = 1
private const val BOTTOM_COMPLICATION_ID = 2

private val COMPLICATIONS_CAPABILITIES = listOf(
    ComplicationType.RANGED_VALUE,
    ComplicationType.SHORT_TEXT,
    ComplicationType.MONOCHROMATIC_IMAGE,
    ComplicationType.SMALL_IMAGE,
    ComplicationType.PHOTO_IMAGE
)

private val COMPLICATIONS: Map<Int, RectF> = mapOf(
    TOP_COMPLICATION_ID to RectF(
        TOP_COMPLICATION_LEFT_BOUND,
        TOP_COMPLICATION_TOP_BOUND,
        TOP_COMPLICATION_RIGHT_BOUND,
        TOP_COMPLICATION_BOTTOM_BOUND
    ),
    MIDDLE_COMPLICATION_ID to RectF(
        MIDDLE_COMPLICATION_LEFT_BOUND,
        MIDDLE_COMPLICATION_TOP_BOUND,
        MIDDLE_COMPLICATION_RIGHT_BOUND,
        MIDDLE_COMPLICATION_BOTTOM_BOUND
    ),
    BOTTOM_COMPLICATION_ID to RectF(
        BOTTOM_COMPLICATION_LEFT_BOUND,
        BOTTOM_COMPLICATION_TOP_BOUND,
        BOTTOM_COMPLICATION_RIGHT_BOUND,
        BOTTOM_COMPLICATION_BOTTOM_BOUND
    )
)

internal object ConcentricComplicationsManager {

    fun create(context: Context, repository: CurrentUserStyleRepository): ComplicationSlotsManager {
        val drawable = ComplicationDrawable.getDrawable(context, R.drawable.complication_icon_style)
            ?: throw IllegalStateException("Failed to get complications drawable")

        return ComplicationSlotsManager(
            COMPLICATIONS.map { (id, rect) ->
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
