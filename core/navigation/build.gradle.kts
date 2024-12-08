plugins {
    alias(libs.plugins.ojh.android.library)
}

android {
    namespace = "com.ojh.core.navigation"
}

dependencies {
    implementation(libs.androidx.navigation.runtime.ktx)
}