package pt.carvalho.concentricplus.editor

import android.graphics.Bitmap
import android.graphics.Color
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.wear.watchface.DrawMode
import androidx.wear.watchface.RenderParameters
import androidx.wear.watchface.complications.data.ComplicationData
import androidx.wear.watchface.editor.EditorSession
import androidx.wear.watchface.style.UserStyleSetting
import androidx.wear.watchface.style.WatchFaceLayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import pt.carvalho.concentricplus.R
import pt.carvalho.concentricplus.complications.BOTTOM_COMPLICATION_ID
import pt.carvalho.concentricplus.complications.MIDDLE_COMPLICATION_ID
import pt.carvalho.concentricplus.complications.TOP_COMPLICATION_ID
import pt.carvalho.concentricplus.editor.data.ComplicationsOption
import pt.carvalho.concentricplus.editor.data.ConfigurationOption.ColorOption
import pt.carvalho.concentricplus.editor.data.ConfigurationOption.LayoutOption
import pt.carvalho.concentricplus.editor.data.isNotSelected
import pt.carvalho.concentricplus.style.COLOR_STYLE
import pt.carvalho.concentricplus.style.ColorStyleOptions
import pt.carvalho.concentricplus.style.LAYOUT_STYLE
import pt.carvalho.concentricplus.style.LayoutOptions
import pt.carvalho.concentricplus.utilities.color
import pt.carvalho.concentricplus.utilities.isHalfDialLayout
import pt.carvalho.concentricplus.utilities.selectedColorKey
import pt.carvalho.concentricplus.utilities.selectedLayoutKey

internal class ConcentricEditorViewModel(
    private val scope: CoroutineScope,
    private val context: ComponentActivity
) {

    private lateinit var editor: EditorSession

    val state: StateFlow<ViewState> = flow {
        editor = EditorSession.createOnWatchEditorSession(
            activity = context
        )

        emitAll(
            combine(editor.userStyle, editor.complicationsPreviewData) { style, complications ->
                val bitmaps = previews(complications = complications)
                ViewState.Preview(
                    bitmap = bitmaps.first(),
                    complicationsBitmap = bitmaps.last(),
                    hasComplications = !style.isHalfDialLayout(),
                    colors = colors(
                        selectedId = style.selectedColorKey()
                    ),
                    layouts = layout(
                        selectedId = style.selectedLayoutKey()
                    )
                )
            }
        )
    }.stateIn(
        scope = scope + Dispatchers.Main.immediate,
        started = SharingStarted.Eagerly,
        initialValue = ViewState.Loading
    )

    fun pickColor(option: ColorOption) {
        option.isNotSelected {
            Log.v("Andre", "color option: $option")

            updateOption(
                userStyleSetting = UserStyleSetting.Id(COLOR_STYLE),
                userStyleOption = UserStyleSetting.Option.Id(option.id)
            )
        }
    }

    fun pickLayout(option: LayoutOption) {
        option.isNotSelected {
            Log.v("Andre", "layout option: $option")

            updateOption(
                userStyleSetting = UserStyleSetting.Id(LAYOUT_STYLE),
                userStyleOption = UserStyleSetting.Option.Id(option.id)
            )
        }
    }

    fun pickComplication(complication: ComplicationsOption) {
        val id = when (complication) {
            ComplicationsOption.TOP -> TOP_COMPLICATION_ID
            ComplicationsOption.MIDDLE -> MIDDLE_COMPLICATION_ID
            ComplicationsOption.BOTTOM -> BOTTOM_COMPLICATION_ID
        }

        scope.launch(Dispatchers.Main.immediate) {
            editor.openComplicationDataSourceChooser(id)
        }
    }

    private fun updateOption(
        userStyleSetting: UserStyleSetting.Id,
        userStyleOption: UserStyleSetting.Option.Id
    ) {
        val mutableUserStyle = editor.userStyle.value.toMutableUserStyle()
        mutableUserStyle[userStyleSetting] = userStyleOption
        editor.userStyle.value = mutableUserStyle.toUserStyle()
    }

    private fun previews(
        complications: Map<Int, ComplicationData>
    ): List<Bitmap> = listOf(
        generatePreviewBitmap(
            highlightComplications = false,
            editor = editor,
            complications = complications
        ),
        generatePreviewBitmap(
            highlightComplications = true,
            editor = editor,
            complications = complications
        )
    )

    private fun generatePreviewBitmap(
        highlightComplications: Boolean,
        editor: EditorSession,
        complications: Map<Int, ComplicationData>
    ): Bitmap {
        return editor.renderWatchFaceToBitmap(
            RenderParameters(
                DrawMode.INTERACTIVE,
                WatchFaceLayer.ALL_WATCH_FACE_LAYERS,
                RenderParameters.HighlightLayer(
                    RenderParameters.HighlightedElement.AllComplicationSlots,
                    Color.TRANSPARENT,
                    if (highlightComplications) {
                        context.color(R.color.complication_highlight)
                    } else {
                        Color.TRANSPARENT
                    }
                )
            ),
            editor.previewReferenceInstant,
            complications
        )
    }

    private fun colors(
        selectedId: String
    ): List<ColorOption> =
        ColorStyleOptions.colors.map {
            val index = ColorStyleOptions.index(it)

            ColorOption(
                id = index,
                title = it.key,
                color = it.value,
                isSelected = index == selectedId
            )
        }

    private fun layout(
        selectedId: String
    ): List<LayoutOption> =
        LayoutOptions.options.map {
            val index = LayoutOptions.index(it)

            LayoutOption(
                id = index,
                title = it.key,
                icon = it.value,
                isSelected = index == selectedId,
            )
        }

    internal sealed class ViewState {
        data class Preview(
            val colors: List<ColorOption>,
            val layouts: List<LayoutOption>,
            val hasComplications: Boolean,
            val bitmap: Bitmap,
            val complicationsBitmap: Bitmap
        ) : ViewState()

        object Loading : ViewState()
    }
}
