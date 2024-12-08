plugins {
    alias(libs.plugins.ojh.application)
    alias(libs.plugins.ojh.application.compose)
    alias(libs.plugins.ojh.flavors)
    alias(libs.plugins.ojh.hilt)
}

android {
    namespace = "com.ojh.app"

    defaultConfig {
        applicationId = "com.ojh.app"
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    
    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_19
        targetCompatibility = JavaVersion.VERSION_19
    }
}

dependencies {
    implementation(project(":core:compose"))
    implementation(project(":core:navigation"))
    implementation(project(":feature:library"))
    implementation(project(":feature:album"))
    implementation(project(":feature:player"))
    implementation(libs.timber)
}