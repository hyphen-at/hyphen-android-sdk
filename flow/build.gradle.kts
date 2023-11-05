@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("hyphen.sdk.android")
    id("hyphen.sdk.android.kotlin")
    id("hyphen.sdk.serialization")
    id(libs.plugins.nexusPlugin.get().pluginId)
}

mavenPublishing {
    val artifactId = "android-sdk-flow"
    coordinates(
        "at.hyphen",
        artifactId,
        "1.0.0-alpha05"
    )

    pom {
        name.set(artifactId)
        description.set(
            "Flow blockchain extension for Hyphen Android SDK"
        )
    }
}

android {
    namespace = "at.hyphen.android.sdk.flow"
}

dependencies {
    implementation(libs.timber)
    implementation(libs.flow)
    implementation(libs.kotlinSerializationJson)

    implementation(libs.grpc.okhttp)

    implementation(projects.core)
    implementation(projects.networking)
}
