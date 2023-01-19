plugins {
    id("com.android.application") version Versions.GRADLE_TOOLS apply false
    id("com.android.library") version Versions.GRADLE_TOOLS apply false
    id("org.jetbrains.kotlin.android") version Versions.KOTLIN_GRADLE apply false
    id("io.gitlab.arturbosch.detekt") version Versions.DETEKT
}

tasks.withType<io.gitlab.arturbosch.detekt.Detekt> { jvmTarget = "11" }

detekt {
    source = files(getAllSrcDirs())
    config = files("${rootProject.projectDir}/detekt/detekt-config.yml")
    buildUponDefaultConfig = true
}

dependencies {
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:${Versions.DETEKT}")
}

fun getAllSrcDirs(): List<File> {
    val sourceDirs = mutableListOf<File>()
    subprojects.forEach {
        sourceDirs += file("${it.projectDir}/src/main/kotlin")
        sourceDirs += file("${it.projectDir}/src/test/kotlin")
        sourceDirs += file("${it.projectDir}/src/androidTest/kotlin")
    }
    return sourceDirs.filter { it.exists() }
}
