package pt.carvalho.concentricplus.utilities

import android.graphics.Canvas

internal fun Canvas.restoreToCountAfter(action: (Canvas) -> Unit) {
    save().run {
        action(this@restoreToCountAfter)
        restoreToCount(this)
    }
}
