package pt.carvalho.concentricplus.renderer

import android.graphics.Bitmap
import androidx.wear.watchface.Renderer

internal class ConcentricRendererAssets : Renderer.SharedAssets {

    var minutesTextMask: Bitmap? = null
    var minutesTicksMask: Bitmap? = null
    var secondsTicksMask: Bitmap? = null

    override fun onDestroy() {
        minutesTextMask?.recycle()
        minutesTicksMask?.recycle()
        secondsTicksMask?.recycle()

        minutesTextMask = null
        minutesTicksMask = null
        secondsTicksMask = null
    }
}
