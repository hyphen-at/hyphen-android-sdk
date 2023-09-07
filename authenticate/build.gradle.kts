plugins {
    id("hyphen.sdk.android.library")
    id("hyphen.sdk.firebase")
}

android {
    namespace = "at.hyphen.android.sdk.authenticate"
}

dependencies {
    implementation(projects.core)
    implementation(projects.deviceinfo)
    implementation(projects.networking)
    implementation(libs.androidx.activity)
}
