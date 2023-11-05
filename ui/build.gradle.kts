@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("hyphen.sdk.android")
    id("hyphen.sdk.android.kotlin")
    id("hyphen.sdk.android.compose")
    id("hyphen.sdk.serialization")
    id(libs.plugins.nexusPlugin.get().pluginId)
}

mavenPublishing {
    val artifactId = "android-sdk-ui"
    coordinates(
        "at.hyphen",
        artifactId,
        "1.0.0-alpha05"
    )

    pom {
        name.set(artifactId)
        description.set(
            "UI for Hyphen Android SDK"
        )
    }
}

android {
    namespace = "at.hyphen.android.sdk.ui"
}

dependencies {
    implementation(projects.core)
    implementation(projects.networking)
    implementation(projects.flow)

    implementation(platform(libs.firebaseBom))
    implementation(libs.firebaseMessaging)

    implementation(libs.timber)
    implementation(libs.androidxAppCompat)
    implementation(libs.kotlinSerializationJson)
    implementation(libs.eventbus)
}
