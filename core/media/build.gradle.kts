plugins {
    alias(libs.plugins.ojh.android.library)
    alias(libs.plugins.ojh.hilt)
}

android {
    namespace = "com.ojh.core.media"
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:model"))
    implementation(project(":core:data"))
    implementation(libs.timber)
    implementation(libs.bundles.media3)
}