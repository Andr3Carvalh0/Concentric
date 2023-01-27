@file:OptIn(ExperimentalPagerApi::class)

package pt.carvalho.concentricplus.editor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.wear.compose.material.CircularProgressIndicator
import androidx.wear.compose.material.HorizontalPageIndicator
import androidx.wear.compose.material.PageIndicatorState
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import pt.carvalho.concentricplus.R
import pt.carvalho.concentricplus.editor.ConcentricEditorViewModel.ViewState.Preview
import pt.carvalho.concentricplus.editor.data.ConfigurationOption.ColorOption
import pt.carvalho.concentricplus.editor.data.ConfigurationOption.LayoutOption
import pt.carvalho.concentricplus.editor.ui.ComplicationsPicker
import pt.carvalho.concentricplus.editor.ui.OptionPicker
import pt.carvalho.concentricplus.editor.ui.WatchFacePreview
import java.lang.Integer.min

private const val COLORS_INDEX = 0
private const val LAYOUT_INDEX = 1
private const val COMPLICATIONS_INDEX = 2
private const val MAX_TABS_WITHOUT_COMPLICATIONS = 2
private const val MAX_TABS_WITH_COMPLICATIONS = 3

class ConcentricEditorActivity : ComponentActivity() {

    private val viewModel: ConcentricEditorViewModel = ConcentricEditorViewModel(
        scope = lifecycleScope,
        context = this
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val viewState = viewModel.state.collectAsState().value
            val hasComplications = (viewState as? Preview)?.hasComplications ?: true
            val pagerState = rememberPagerState()

            val indicatorState = remember {
                derivedStateOf {
                    object : PageIndicatorState {
                        override val pageOffset: Float = 0.0f
                        override val pageCount: Int = if (hasComplications) {
                            MAX_TABS_WITH_COMPLICATIONS
                        } else {
                            MAX_TABS_WITHOUT_COMPLICATIONS
                        }
                        override val selectedPage: Int = min(pageCount, pagerState.currentPage)
                    }
                }
            }

            when (viewState) {
                is Preview -> {
                    ConcentricPreview(
                        value = viewState,
                        indicatorState = indicatorState.value,
                        pagerState = pagerState
                    )
                }
                else -> Loading()
            }
        }
    }

    @Composable
    private fun ConcentricPreview(
        value: Preview,
        indicatorState: PageIndicatorState,
        pagerState: PagerState
    ) {
        WatchFacePreview(
            highlightComplications = pagerState.currentPage == COMPLICATIONS_INDEX,
            normalImage = value.bitmap,
            complicationsImage = value.complicationsBitmap
        )

        HorizontalPager(
            count = indicatorState.pageCount,
            state = pagerState,
            itemSpacing = 96.dp
        ) {
            when (currentPage) {
                COLORS_INDEX -> OptionPicker(
                    modifier = Modifier.fillMaxSize(),
                    items = value.colors,
                    expand = { },
                    onOptionFocused = { viewModel.pickColor(it as ColorOption) }
                )
                LAYOUT_INDEX -> OptionPicker(
                    modifier = Modifier.fillMaxSize(),
                    items = value.layouts,
                    expand = { },
                    onOptionFocused = { viewModel.pickLayout(it as LayoutOption) }
                )
                COMPLICATIONS_INDEX -> ComplicationsPicker(
                    alpha = 1.0f + pagerState.currentPageOffset,
                    onClick = viewModel::pickComplication
                )
            }
        }

        HorizontalPageIndicator(
            pageIndicatorState = indicatorState,
            modifier = Modifier.padding(
                bottom = dimensionResource(R.dimen.page_indicator_bottom_padding)
            )
        )
    }

    @Composable
    private fun Loading() {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircularProgressIndicator()
        }
    }
}
