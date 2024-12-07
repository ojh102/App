import com.android.build.api.dsl.ApplicationExtension
import com.ojh.convention.configureKotlinAndroid
import com.ojh.convention.libs
import com.ojh.convention.targetSdkVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class ApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.application")
                apply("org.jetbrains.kotlin.android")
                apply("org.jetbrains.kotlin.plugin.serialization")
            }

            extensions.configure<ApplicationExtension> {
                configureKotlinAndroid(this)
                defaultConfig.targetSdk = targetSdkVersion
                @Suppress("UnstableApiUsage")
                testOptions.animationsDisabled = true
            }

            dependencies {
                add("implementation", libs.findLibrary("kotlinx.serialization.json").get())
            }
        }
    }

}
