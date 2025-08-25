plugins {
    id("itemize.shadow-conventions")
}

subprojects {
    apply {
        plugin("itemize.base-conventions")
        if (project.name != "example") {
            plugin("itemize.spotless-conventions")
        }
    }
}

dependencies {
    implementation(projects.itemizeApi)
    implementation(projects.itemizeCommon)
}