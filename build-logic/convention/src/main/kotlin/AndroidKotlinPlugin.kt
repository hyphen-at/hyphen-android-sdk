import at.hyphen.android.sdk.android
import at.hyphen.android.sdk.implementation
import at.hyphen.android.sdk.library
import at.hyphen.android.sdk.libs
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

@Suppress("unused")
class AndroidKotlinPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("org.jetbrains.kotlin.android")
            }
            tasks.withType(org.jetbrains.kotlin.gradle.tasks.KotlinCompile::class.java) {
                kotlinOptions.jvmTarget = "11"
            }

            android {
                kotlinOptions {
                    allWarningsAsErrors = properties["warningsAsErrors"] as? Boolean ?: false

                    freeCompilerArgs = freeCompilerArgs + listOf(
                        "-Xcontext-receivers"
                    )

                    jvmTarget = JavaVersion.VERSION_11.toString()
                }
            }
            dependencies {
                implementation(libs.library("kotlinxCoroutinesCore"))
                implementation(libs.library("kotlinxAtomicfu"))
            }
        }
    }
}
