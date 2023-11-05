import org.gradle.api.tasks.Delete

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.androidGradlePlugin) apply false
    alias(libs.plugins.androidGradleLibraryPlugin) apply false
    alias(libs.plugins.kotlinGradlePlugin) apply false
}

tasks.register("clean", Delete::class) {
    delete(rootProject.layout.buildDirectory)
}
