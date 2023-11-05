plugins {
    id("hyphen.sdk.android")
    id("hyphen.sdk.android.kotlin")
    id("hyphen.sdk.serialization")
}

android {
    namespace = "at.hyphen.android.sdk.flow"
}

dependencies {
    implementation(libs.timber)
    implementation(libs.flow)

    implementation(libs.grpc.okhttp)

    implementation(projects.core)
    implementation(projects.networking)
}
