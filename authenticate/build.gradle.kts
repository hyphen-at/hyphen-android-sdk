@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("hyphen.sdk.android")
    id("hyphen.sdk.android.kotlin")
    id(libs.plugins.nexusPlugin.get().pluginId)
}

mavenPublishing {
    val artifactId = "android-sdk-authenticate"
    coordinates(
        "at.hyphen",
        artifactId,
        "1.0.0-alpha06"
    )

    pom {
        name.set(artifactId)
        description.set(
            "Authenticate for Hyphen Android SDK"
        )
    }
}

android {
    namespace = "at.hyphen.android.sdk.authenticate"
}

dependencies {
    implementation(projects.core)
    implementation(projects.deviceinfo)
    implementation(projects.networking)

    implementation(libs.timber)
    implementation(libs.androidxActivity)

    implementation(platform(libs.firebaseBom))
    implementation(libs.firebaseAuth)
    implementation(libs.firebaseMessaging)
    implementation(libs.googleAuth)
    implementation(libs.eventbus)
    implementation(libs.flow)
}
