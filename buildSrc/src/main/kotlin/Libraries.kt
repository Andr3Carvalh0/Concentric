object Libraries {

    object AndroidX {
        const val CORE = "androidx.core:core-ktx:${Versions.CORE_KTX}"
    }

    object Watchface {
        const val MAIN = "androidx.wear.watchface:watchface:${Versions.WATCHFACE}"
        const val COMPLICATIONS = "androidx.wear.watchface:watchface-complications-data-source:${Versions.WATCHFACE}"
        const val COMPLICATIONS_KTX = "androidx.wear.watchface:watchface-complications-data-source-ktx:${Versions.WATCHFACE}"
        const val EDITOR = "androidx.wear.watchface:watchface-editor:${Versions.WATCHFACE}"
    }

    object Compose {
        const val ACTIVITY = "androidx.activity:activity-compose:${Versions.COMPOSE_ACTIVITY}"
        const val PREVIEW = "androidx.compose.ui:ui-tooling-preview:${Versions.COMPOSE_PREVIEW}"
        const val MATERIAL = "androidx.wear.compose:compose-material:${Versions.COMPOSE}"
        const val FOUNDATION = "androidx.wear.compose:compose-foundation:${Versions.COMPOSE}"
    }

    object PlayServices {
        const val BASE = "com.google.android.gms:play-services-base:${Versions.PLAY_SERVICES_BASE}"
        const val WEARABLE = "com.google.android.gms:play-services-wearable:${Versions.PLAY_SERVICES_WEARABLE}"
    }
}
