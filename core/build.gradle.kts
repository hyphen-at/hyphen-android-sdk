plugins {
    id("hyphen.sdk.serialization")
    id("hyphen.sdk.android")
    id("hyphen.sdk.android.kotlin")
}

android {
    namespace = "at.hyphen.android.sdk.core"
}

dependencies {
    implementation(libs.kotlinSerializationJson)
    implementation(libs.androidxAnnotation)
    implementation(libs.androidxSecurity)
    implementation(libs.androidxBiometric)
    implementation(libs.timber)
    implementation(libs.eventbus)
}
