plugins {
    alias(libs.plugins.ojh.android.library)
    alias(libs.plugins.ojh.android.library.compose)
}

android {
    namespace = "com.ojh.core.compose"
}

dependencies {
    implementation(project(":core:model"))
}