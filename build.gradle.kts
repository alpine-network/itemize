plugins {
    id("java")
    alias(libs.plugins.shadow)
}

group = "${project.properties["maven_group"]}.${project.properties["maven_artifact"]}"
project.version = "${project.properties["version"]}"

allprojects {
    apply(plugin = "java")

    val props = mapOf(
        "mavenArtifact" to rootProject.properties["maven_artifact"],
        "pluginName" to rootProject.properties["plugin_name"],
        "pluginDescription" to rootProject.properties["plugin_description"],
        "pluginGroup" to rootProject.group,
        "pluginVersion" to rootProject.version,
    )

    repositories {
        mavenCentral()
        maven("https://lib.alpn.cloud/snapshots/")
        maven("https://lib.alpn.cloud/alpine-public/")
        maven("https://repo.papermc.io/repository/maven-public/")
    }

    val libs = rootProject.libs
    dependencies {
        compileOnly(libs.spigot.api)
        compileOnly(libs.alpinecore)

        compileOnly(libs.lombok)
        annotationProcessor(libs.lombok)
    }

    tasks {
        java {
            sourceCompatibility = JavaVersion.VERSION_1_8
            targetCompatibility = JavaVersion.VERSION_1_8
        }

        withType<JavaCompile> {
            options.encoding = "UTF-8"
        }

        processResources {
            duplicatesStrategy = DuplicatesStrategy.INCLUDE
            inputs.properties(props)
            filesMatching("plugin.yml") {
                expand(props)
            }
        }
    }
}

dependencies {
    listOf(project(":plugin-common"), project(":plugin-api")).forEach {
        shadow(it) { isTransitive = false }
    }
}

sourceSets {
    main {
        resources.srcDirs += project(":plugin-common").file("src/main/resources")
    }
}

tasks {
    shadowJar {
        configurations = listOf(project.configurations["shadow"])
        archiveClassifier.set("dev-shadow")
        archiveFileName.set("${rootProject.property("plugin_name")}-${project.version}.jar")
    }

    build {
        dependsOn(shadowJar)
    }

    jar {
        archiveClassifier.set("dev")
    }
}
