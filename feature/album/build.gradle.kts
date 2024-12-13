plugins {
    alias(libs.plugins.ojh.feature)
}

android {
    namespace = "com.ojh.feature.album"
}

dependencies {
    implementation(project(":core:compose"))
    implementation(project(":core:data"))
    implementation(project(":core:model"))
    implementation(project(":core:media"))
    implementation(project(":core:navigation"))
    implementation(libs.bundles.coil)
}