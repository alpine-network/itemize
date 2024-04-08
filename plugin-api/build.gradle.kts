plugins {
    id("maven-publish")
}

tasks.jar {
    doFirst {
        archiveClassifier.set("")
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            // Set the unshaded JAR as the main artifact
            artifact(tasks["jar"])

            pom {
                name.set("${rootProject.properties["plugin_name"]}-API")
                description.set(rootProject.properties["plugin_description"] as String)

                groupId = rootProject.properties["maven_group"] as String
                artifactId = "${rootProject.properties["maven_artifact"]}-api"
                version = rootProject.version as String
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