pluginManagement {
    includeBuild("build-logic")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "App"

include(":app")
include(":feature:library")
include(":feature:album")
include(":feature:player")
include(":core:data")
include(":core:model")
include(":core:network")
include(":core:database")
include(":core:common")
