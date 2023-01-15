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
}

dependencies {
    implementation(Libraries.AndroidX.CORE)
    implementation(Libraries.Watchface.MAIN)
    implementation(Libraries.Watchface.COMPLICATIONS)
    implementation(Libraries.Watchface.COMPLICATIONS_KTX)
    implementation(Libraries.Watchface.COMPLICATIONS_RENDERER)
    implementation(Libraries.Watchface.EDITOR)
    implementation(Libraries.PlayServices.WEARABLE)
    implementation(Libraries.PlayServices.BASE)
}