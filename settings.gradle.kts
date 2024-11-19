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
include(":feature:list")
include(":feature:detail")
include(":core:data")
include(":core:model")
include(":core:network")
include(":core:database")
include(":core:common")
