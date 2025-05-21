plugins {
    id("maven-publish")
}

java {
    withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])

            pom {
                name.set("${rootProject.properties["plugin_name"]}-API")
                description.set("${rootProject.properties["plugin_description"]}")

                groupId = "${rootProject.properties["maven_group"]}"
                artifactId = "${rootProject.properties["maven_artifact"]}-api"
                version = "${rootProject.version}"
                packaging = "jar"
            }
        }
    }
    repositories {
        maven {
            name = "AlpineCloud"
            url = uri("https://lib.alpn.cloud/alpine-public")
            credentials {
                username = System.getenv("ALPINE_MAVEN_NAME")
                password = System.getenv("ALPINE_MAVEN_SECRET")
            }
        }
    }
}