package pt.carvalho.concentricplus.renderer

import android.graphics.Bitmap
import androidx.wear.watchface.Renderer

internal class ConcentricRendererAssets : Renderer.SharedAssets {

    var minutesDialTextMask: Bitmap? = null
    var minutesDialTicksMask: Bitmap? = null
    var secondsDialTicksMask: Bitmap? = null

    override fun onDestroy() {
        minutesDialTextMask?.recycle()
        minutesDialTicksMask?.recycle()
        secondsDialTicksMask?.recycle()

        minutesDialTextMask = null
        minutesDialTicksMask = null
        secondsDialTicksMask = null
    }
}
