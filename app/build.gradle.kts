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

    signingConfigs {
        create(AppConfig.Signing.ADHOC) {
            keyAlias = System.getenv("CONCENTRIC_SIGNING_KEY")
            keyPassword = System.getenv("CONCENTRIC_SIGNING_PASSWORD")
            storePassword = System.getenv("CONCENTRIC_SIGNING_PASSWORD")
            storeFile = rootProject.file("./secrets/release.jks")
        }
    }

    buildTypes {
        getByName(BuildTypes.DEBUG) {
            isDebuggable = true
            isMinifyEnabled = false
            isShrinkResources = false
            signingConfig = signingConfigs.getByName(AppConfig.Signing.ADHOC)
            applicationIdSuffix = ".${BuildTypes.DEBUG}"
        }
        getByName(BuildTypes.RELEASE) {
            isDebuggable = false
            isMinifyEnabled = true
            isShrinkResources = true
            matchingFallbacks.add(BuildTypes.RELEASE)
            signingConfig = signingConfigs.getByName(AppConfig.Signing.ADHOC)
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
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
    implementation(Libraries.Watchface.EDITOR)
    implementation(Libraries.Compose.ACTIVITY)
    implementation(Libraries.Compose.PREVIEW)
    implementation(Libraries.Compose.MATERIAL)
    implementation(Libraries.Accompanist.PAGER)
    implementation(Libraries.Compose.FOUNDATION)
    implementation(Libraries.PlayServices.WEARABLE)
    implementation(Libraries.PlayServices.BASE)
}
