import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`
}

group = "at.hyphen.android.sdk.buildlogic"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }
}

dependencies {
    compileOnly(libs.plugin.androidBuildTools)
    compileOnly(libs.plugin.kotlin)
}

gradlePlugin {
    plugins {
        register("androidLibrary") {
            id = "hyphen.sdk.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register("androidKotlinSerialization") {
            id = "hyphen.sdk.kotlin.serialization"
            implementationClass = "AndroidSerializationConventionPlugin"
        }
        register("androidFirebase") {
            id = "hyphen.sdk.firebase"
            implementationClass = "AndroidFirebaseConventionPlugin"
        }
    }
}
