plugins {
    id("com.android.application") version Versions.GRADLE_TOOLS apply false
    id("com.android.library") version Versions.GRADLE_TOOLS apply false
    id("org.jetbrains.kotlin.android") version Versions.KOTLIN_GRADLE apply false
    id("io.gitlab.arturbosch.detekt") version Versions.DETEKT
}

tasks.withType<io.gitlab.arturbosch.detekt.Detekt> { jvmTarget = "11" }

detekt {
    config = files("${rootProject.projectDir}/detekt/detekt-config.yml")
    buildUponDefaultConfig = true
}

dependencies {
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:${Versions.DETEKT}")
}