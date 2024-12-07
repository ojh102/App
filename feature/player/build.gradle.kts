plugins {
    alias(libs.plugins.ojh.feature)
}

android {
    namespace = "com.ojh.feature.player"
}

dependencies {
    implementation(project(":core:compose"))
    implementation(project(":core:data"))
    implementation(project(":core:model"))
    implementation(project(":core:media"))
    implementation(libs.bundles.coil)
    implementation(libs.bundles.media3)
}