@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("hyphen.sdk.serialization")
    id("hyphen.sdk.android")
    id("hyphen.sdk.android.kotlin")
    id(libs.plugins.nexusPlugin.get().pluginId)
}

mavenPublishing {
    val artifactId = "android-sdk-core"
    coordinates(
        "at.hyphen",
        artifactId,
        "1.0.0-alpha05"
    )

    pom {
        name.set(artifactId)
        description.set(
            "Hyphen Android SDK Core"
        )
    }
}

android {
    namespace = "at.hyphen.android.sdk.core"
}

dependencies {
    implementation(libs.kotlinSerializationJson)
    implementation(libs.androidxAnnotation)
    implementation(libs.androidxSecurity)
    implementation(libs.androidxBiometric)
    implementation(libs.timber)
    implementation(libs.eventbus)
}
