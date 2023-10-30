import at.hyphen.android.sdk.android
import at.hyphen.android.sdk.implementation
import at.hyphen.android.sdk.implementationPlatform
import at.hyphen.android.sdk.library
import at.hyphen.android.sdk.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

@Suppress("unused")
class AndroidFirebasePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.google.gms.google-services")
            }

            android {
                packagingOptions {
                    resources {
                        excludes += "META-INF/gradle/incremental.annotation.processors"
                    }
                }
            }
            dependencies {
                implementationPlatform(libs.library("firebaseBom"))
                implementation(libs.library("firebaseAuth"))
                implementation(libs.library("firebaseCommon"))
                implementation(libs.library("firebaseMessaging"))
            }
        }
    }
}
