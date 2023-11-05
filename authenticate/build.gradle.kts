plugins {
    id("hyphen.sdk.android")
    id("hyphen.sdk.android.kotlin")

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
}
