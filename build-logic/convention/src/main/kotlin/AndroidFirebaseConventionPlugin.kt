import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

class AndroidFirebaseConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

            dependencies {
                "implementation"(platform(libs.findLibrary("firebase.bom").get()))

                "implementation"(libs.findLibrary("firebase.messaging").get())
                "implementation"(libs.findLibrary("firebase.authenticate").get())
                "implementation"(libs.findLibrary("play.services.auth").get())
            }
        }
    }
}
