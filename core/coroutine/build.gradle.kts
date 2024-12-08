plugins {
    alias(libs.plugins.ojh.android.library)
    alias(libs.plugins.ojh.hilt)
}

android {
    namespace = "com.ojh.core.coroutine"
}

dependencies {
    implementation(project(":core:model"))
}