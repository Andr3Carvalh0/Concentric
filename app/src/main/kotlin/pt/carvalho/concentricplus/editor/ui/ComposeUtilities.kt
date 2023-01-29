package pt.carvalho.concentricplus.editor.ui

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.wear.compose.material.CompactChip
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.ScalingLazyListAnchorType
import androidx.wear.compose.material.ScalingLazyListState
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.rememberScalingLazyListState
import pt.carvalho.concentricplus.R
import pt.carvalho.concentricplus.editor.data.ComplicationsOption
import pt.carvalho.concentricplus.editor.data.ConfigurationOption
import pt.carvalho.concentricplus.renderer.BOTTOM_COMPLICATION_RECT
import pt.carvalho.concentricplus.renderer.MIDDLE_COMPLICATION_RECT
import pt.carvalho.concentricplus.renderer.TOP_COMPLICATION_RECT

@Composable
internal fun WatchFacePreview(
    modifier: Modifier = Modifier,
    highlightComplications: Boolean,
    normalImage: Bitmap,
    complicationsImage: Bitmap
) {
    Image(
        modifier = modifier.fillMaxSize(),
        bitmap = if (highlightComplications) {
            complicationsImage.asImageBitmap()
        } else {
            normalImage.asImageBitmap()
        },
        contentDescription = null
    )
}

@Composable
internal fun OptionPicker(
    modifier: Modifier = Modifier,
    items: List<ConfigurationOption>,
    expand: () -> Unit,
    onOptionFocused: (ConfigurationOption) -> Unit
) {
    val selectedItem = items.findLast { it.isSelected }
    val index = remember { mutableStateOf(0) }
    val listState = rememberScalingLazyListState(items.indexOf(selectedItem))

    OptionsList(
        modifier = modifier.fillMaxSize(),
        items = items,
        state = listState,
        onItem = { position, item ->
            index.value = position
            onOptionFocused(item)
        }
    )

    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = dimensionResource(id = R.dimen.chip_bottom_padding)),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.Bottom
    ) {
        CompactChip(
            label = {
                Text(text = stringResource(id = items[index.value].title))
            },
            onClick = expand
        )
    }
}

@Composable
internal fun ComplicationsPicker(
    modifier: Modifier = Modifier,
    alpha: Float,
    border: Dp = dimensionResource(id = R.dimen.complications_border_highlight),
    borderColor: Color = colorResource(id = R.color.graphite),
    onClick: (ComplicationsOption) -> Unit
) {
    val borderRadius = border / 2.0f

    Spacer(modifier = modifier.fillMaxSize())

    Box(modifier = modifier) {
        BoxWithConstraints {
            mapOf(
                ComplicationsOption.TOP to TOP_COMPLICATION_RECT,
                ComplicationsOption.MIDDLE to MIDDLE_COMPLICATION_RECT,
                ComplicationsOption.BOTTOM to BOTTOM_COMPLICATION_RECT
            ).forEach { (id, rect) ->
                Spacer(
                    modifier = Modifier
                        .padding(
                            start = (maxWidth * rect.left) - borderRadius,
                            top = (maxHeight * rect.top) - borderRadius
                        )
                        .width((maxWidth * rect.width()) + borderRadius)
                        .height((maxHeight * rect.height()) + borderRadius)
                        .alpha(alpha)
                        .clip(CircleShape)
                        .border(border, borderColor, CircleShape)
                        .clickable { onClick(id) },
                )
            }
        }
    }
}

@Composable
private fun OptionsList(
    modifier: Modifier = Modifier,
    items: List<ConfigurationOption>,
    state: ScalingLazyListState,
    onItem: (Int, ConfigurationOption) -> Unit
) {
    LaunchedEffect(state) {
        snapshotFlow { state.centerItemIndex }
            .collect { index ->
                onItem(index, items[index])
            }
    }

    BoxWithConstraints {
        ScalingLazyColumn(
            modifier = modifier,
            state = state,
            anchorType = ScalingLazyListAnchorType.ItemCenter
        ) {
            items(
                count = items.size
            ) {
                Spacer(
                    modifier = Modifier
                        .width(maxWidth)
                        .height(maxHeight)
                )
            }
        }

        PositionIndicator(
            scalingLazyListState = state
        )
    }
}
