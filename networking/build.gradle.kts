@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("hyphen.sdk.android")
    id("hyphen.sdk.android.kotlin")
    id("hyphen.sdk.serialization")
    id(libs.plugins.nexusPlugin.get().pluginId)
}

mavenPublishing {
    val artifactId = "android-sdk-networking"
    coordinates(
        "at.hyphen",
        artifactId,
        "1.0.0-alpha05"
    )

    pom {
        name.set(artifactId)
        description.set(
            "Networking for Hyphen Android SDK"
        )
    }
}

android {
    namespace = "at.hyphen.android.sdk.networking"
}

dependencies {
    implementation(projects.core)

    implementation(libs.bundles.networking)
    api(libs.sandwich)
}
