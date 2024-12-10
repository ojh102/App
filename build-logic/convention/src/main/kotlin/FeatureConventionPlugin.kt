import com.android.build.gradle.LibraryExtension
import com.ojh.convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class FeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply {
                apply("ojh.android.library")
                apply("ojh.android.library.compose")
                apply("ojh.hilt")
            }
            extensions.configure<LibraryExtension> {
                testOptions.animationsDisabled = true
            }

            dependencies {
                add("debugImplementation", libs.findBundle("test").get())
            }
        }
    }
}
