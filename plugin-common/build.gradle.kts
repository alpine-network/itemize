dependencies {
    compileOnly(projects.itemizeApi)
    compileOnly(libs.alpinecore)
    compileOnly(libs.spigotApi)
}

tasks {
    processResources {
        expandProperties("plugin.yml")
    }
}