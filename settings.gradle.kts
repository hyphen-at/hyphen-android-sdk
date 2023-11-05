@file:Suppress("UnstableApiUsage")

import java.io.FileInputStream
import java.util.Properties


pluginManagement {
    includeBuild("build-logic")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_PROJECT)

    val localProperties = Properties()
    localProperties.load(FileInputStream(rootProject.projectDir.absolutePath + "/local.properties"))

    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
rootProject.name = "hyphen-android-sdk"
include(
    ":authenticate",
    ":core",
    ":deviceinfo",
    ":flow",
    ":networking",
    ":ui",

    ":sample",
)
