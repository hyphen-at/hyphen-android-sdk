plugins {
    id("hyphen.sdk.android.library")
    id("hyphen.sdk.kotlin.serialization")
}

android {
    namespace = "at.hyphen.android.sdk.networking"
}

dependencies {
    implementation(projects.core)

    implementation(libs.bundles.networking)
    api(libs.sandwich)
}
