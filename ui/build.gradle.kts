plugins {
    id("hyphen.sdk.android")
    id("hyphen.sdk.android.kotlin")
    id("hyphen.sdk.android.compose")
    id("hyphen.sdk.serialization")
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
