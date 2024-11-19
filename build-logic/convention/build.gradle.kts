import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `kotlin-dsl`
}

group = "com.ojh.buildlogic"

java {
    sourceCompatibility = JavaVersion.VERSION_19
    targetCompatibility = JavaVersion.VERSION_19
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_19
    }
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.android.tools.common)
    compileOnly(libs.compose.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.ksp.gradlePlugin)
    compileOnly(libs.room.gradlePlugin)
}

tasks {
    validatePlugins {
        enableStricterValidation = true
        failOnWarning = true
    }
}

gradlePlugin {
    plugins {
        register("androidLibraryCompose") {
            id = "ojh.android.library.compose"
            implementationClass = "AndroidLibraryComposeConventionPlugin"
        }
        register("androidLibrary") {
            id = "ojh.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register("applicationCompose") {
            id = "ojh.application.compose"
            implementationClass = "ApplicationComposeConventionPlugin"
        }
        register("application") {
            id = "ojh.application"
            implementationClass = "ApplicationConventionPlugin"
        }
        register("feature") {
            id = "ojh.feature"
            implementationClass = "FeatureConventionPlugin"
        }
        register("flavors") {
            id = "ojh.flavors"
            implementationClass = "FlavorsConventionPlugin"
        }
        register("hilt") {
            id = "ojh.hilt"
            implementationClass = "HiltConventionPlugin"
        }
        register("jvmLibrary") {
            id = "ojh.jvm.library"
            implementationClass = "JvmLibraryConventionPlugin"
        }
        register("test") {
            id = "ojh.test"
            implementationClass = "TestConventionPlugin"
        }
    }
}