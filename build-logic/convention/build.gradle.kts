import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`
}

group = "at.hyphen.android.sdk.buildlogic"

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_11.toString()
    }
}

dependencies {
    implementation(libs.bundles.plugins)
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "hyphen.sdk.android.application"
            implementationClass = "AndroidApplicationPlugin"
        }
        register("android") {
            id = "hyphen.sdk.android"
            implementationClass = "AndroidPlugin"
        }
        register("androidKotlin") {
            id = "hyphen.sdk.android.kotlin"
            implementationClass = "AndroidKotlinPlugin"
        }
        register("androidCompose") {
            id = "hyphen.sdk.android.compose"
            implementationClass = "AndroidComposePlugin"
        }
        register("androidFirebase") {
            id = "hyphen.sdk.android.firebase"
            implementationClass = "AndroidFirebasePlugin"
        }
        register("kotlinMppKotlinSerialization") {
            id = "hyphen.sdk.serialization"
            implementationClass = "KotlinSerializationPlugin"
        }
    }
}
