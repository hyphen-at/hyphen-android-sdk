@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("hyphen.sdk.android")
    id("hyphen.sdk.android.kotlin")
    id(libs.plugins.nexusPlugin.get().pluginId)
}

mavenPublishing {
    val artifactId = "android-sdk-deviceinfo"
    coordinates(
        "at.hyphen",
        artifactId,
        "1.0.0-alpha05"
    )

    pom {
        name.set(artifactId)
        description.set(
            "Device information library for Hyphen Android SDK"
        )
    }
}

android {
    namespace = "at.hyphen.android.sdk.deviceinfo"
}

dependencies {
    implementation(libs.androidxAnnotation)
}
