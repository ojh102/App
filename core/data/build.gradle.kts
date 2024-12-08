plugins {
    alias(libs.plugins.ojh.android.library)
    alias(libs.plugins.ojh.hilt)
}

android {
    namespace = "com.ojh.core.data"
}

dependencies {
    implementation(project(":core:coroutine"))
    implementation(project(":core:model"))
}