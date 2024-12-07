plugins {
    alias(libs.plugins.ojh.android.library)
    alias(libs.plugins.ojh.hilt)
}

android {
    namespace = "com.ojh.core.common"
}

dependencies {
    implementation(project(":core:model"))
}