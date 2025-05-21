rootProject.name = "Itemize"

pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://repo.papermc.io/repository/maven-public/")
    }
}

include("plugin-api")
include("plugin-common")
include("example")