object Libraries {

    object AndroidX {
        const val CORE = "androidx.core:core-ktx:${Versions.CORE_KTX}"
    }

    object Watchface {
        const val MAIN = "androidx.wear.watchface:watchface:${Versions.WATCHFACE}"
        const val COMPLICATIONS = "androidx.wear.watchface:watchface-complications-data-source:${Versions.WATCHFACE}"
        const val COMPLICATIONS_KTX = "androidx.wear.watchface:watchface-complications-data-source-ktx:${Versions.WATCHFACE}"
        const val COMPLICATIONS_RENDERER = "androidx.wear.watchface:watchface-complications-rendering:${Versions.WATCHFACE}"
        const val EDITOR = "androidx.wear.watchface:watchface-editor:${Versions.WATCHFACE}"

    }

    object PlayServices {
        const val BASE = "com.google.android.gms:play-services-base:${Versions.PLAY_SERVICES_BASE}"
        const val WEARABLE = "com.google.android.gms:play-services-wearable:${Versions.PLAY_SERVICES_WEARABLE}"
    }
}
