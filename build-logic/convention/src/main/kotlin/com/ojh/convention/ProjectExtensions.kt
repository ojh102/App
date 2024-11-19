package com.ojh.convention

import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType

val Project.libs
    get(): VersionCatalog = extensions.getByType<VersionCatalogsExtension>().named("libs")

val Project.targetSdkVersion
    get() = findProperty("targetSdk")?.toString()?.toInt() ?: 35

val Project.compileSdkVersion
    get() = findProperty("targetSdk")?.toString()?.toInt() ?: 35

val Project.minSdkVersion
    get() = findProperty("minSdk")?.toString()?.toInt() ?: 24