plugins {
    alias(libs.plugins.ojh.feature)
}

android {
    namespace = "com.ojh.feature.player"
}

dependencies {
    implementation(project(":core:data"))
    implementation(project(":core:model"))
    implementation(libs.timber)
    implementation(libs.bundles.media3)
}