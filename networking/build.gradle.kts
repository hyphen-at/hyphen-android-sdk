plugins {
    id("hyphen.sdk.android")
    id("hyphen.sdk.android.kotlin")
    id("hyphen.sdk.serialization")
}

android {
    namespace = "at.hyphen.android.sdk.networking"
}

dependencies {
    implementation(projects.core)

    implementation(libs.bundles.networking)
    api(libs.sandwich)
}
