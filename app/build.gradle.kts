plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = AppConfig.APP_ID
    compileSdk = AppConfig.TARGET_SDK

    defaultConfig {
        applicationId = AppConfig.APP_ID
        minSdk = AppConfig.MINIMUM_SDK
        targetSdk = AppConfig.TARGET_SDK
        versionCode = AppConfig.VERSION_CODE
        versionName = AppConfig.VERSION_NAME
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.3.2"
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation(Libraries.AndroidX.CORE)
    implementation(Libraries.Watchface.MAIN)
    implementation(Libraries.Watchface.COMPLICATIONS)
    implementation(Libraries.Watchface.COMPLICATIONS_KTX)
    implementation(Libraries.Watchface.COMPLICATIONS_RENDERER)
    implementation(Libraries.Watchface.EDITOR)
    implementation(Libraries.Compose.ACTIVITY)
    implementation(Libraries.Compose.PREVIEW)
    implementation(Libraries.Compose.MATERIAL)
    implementation("androidx.wear.compose:compose-foundation:1.1.1")
    implementation(Libraries.PlayServices.WEARABLE)
    implementation(Libraries.PlayServices.BASE)
}