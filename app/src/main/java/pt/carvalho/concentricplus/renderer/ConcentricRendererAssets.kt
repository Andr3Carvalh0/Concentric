package pt.carvalho.concentricplus.renderer

import android.content.Context
import android.graphics.Typeface
import androidx.wear.watchface.Renderer

internal class ConcentricRendererAssets(
    private val context: Context
) : Renderer.SharedAssets {

    var typeface: Typeface? = null

    override fun onDestroy() {
        typeface = null
    }
}
