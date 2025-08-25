@file:Suppress("UnstableApiUsage")

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        maven("https://lib.alpn.cloud/releases/")
        maven("https://lib.alpn.cloud/snapshots/")
        maven("https://lib.alpn.cloud/mirrors/")
        mavenLocal()
    }
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
}


pluginManagement {
    includeBuild("build-logic")
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version ("1.0.0")
}

rootProject.name = "Itemize"

sequenceOf(
    "api",
    "common",
).forEach {
    val name = "itemize-$it"
    include(name)
    project(":${name}").projectDir = file("plugin-${it}")
}

include("example")