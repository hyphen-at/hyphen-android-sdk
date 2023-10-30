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
    //implementation(libs.androidx.activity)

    platform(libs.firebaseBom)
    implementation(libs.firebaseAuth)
}
