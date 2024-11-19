import com.android.build.gradle.TestExtension
import com.ojh.convention.configureKotlinAndroid
import com.ojh.convention.targetSdkVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class TestConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.test")
                apply("org.jetbrains.kotlin.android")
            }

            extensions.configure<TestExtension> {
                defaultConfig.targetSdk = targetSdkVersion
                configureKotlinAndroid(this)
            }
        }
    }
}
