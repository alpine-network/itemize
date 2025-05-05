plugins {
    id("java")
    id("com.gradleup.shadow") version "9.0.0-beta13"
}

allprojects {
    apply(plugin = "java")

    group = compileGroup()
    version = compileVersion(true)

    val props = mapOf(
        "mavenArtifact" to rootProject.property("maven_artifact") as String,
        "pluginName" to rootProject.property("plugin_name") as String,
        "pluginDescription" to rootProject.property("plugin_description") as String,
        "pluginVersion" to compileVersion(true),
        "pluginVersionRaw" to compileVersion(false),
        "pluginGroup" to compileGroup(),
    )

    repositories {
        mavenCentral()
        maven("https://lib.alpn.cloud/snapshots/")
        maven("https://lib.alpn.cloud/alpine-public/")
        maven("https://repo.papermc.io/repository/maven-public/")
    }

    configurations.create("shaded")
    dependencies {
        compileOnly(group = "org.spigotmc", name = "spigot-api", version = "1.12.2-R0.1-SNAPSHOT")
        compileOnly(group = "co.crystaldev", name = "alpinecore", version = "0.4.10-SNAPSHOT")

        compileOnly(group = "org.projectlombok", name = "lombok", version = "1.18.38")
        annotationProcessor(group = "org.projectlombok", name = "lombok", version = "1.18.38")
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
        "shaded"(it) { isTransitive = false }
    }
}

sourceSets {
    main {
        resources.srcDirs += project(":plugin-common").file("src/main/resources")
    }
}

tasks {
    shadowJar {
        configurations = listOf(project.configurations["shaded"])
        archiveClassifier.set("dev-shadow")
        archiveFileName.set("${rootProject.property("plugin_name")}-${compileVersion(true)}.jar")
    }

    build {
        dependsOn(shadowJar)
    }

    jar {
        archiveClassifier.set("dev")
    }
}

fun compileGroup(): String {
    return "${rootProject.properties["maven_group"]}.${rootProject.properties["maven_artifact"]}"
}

fun compileVersion(prerelease: Boolean): String {
    val major = rootProject.properties["version_major"]
    val minor = rootProject.properties["version_minor"]
    val patch = rootProject.properties["version_patch"]
    val preRelease = rootProject.properties["version_pre_release"]
    return "${major}.${minor}.${patch}${if (!prerelease || preRelease == "none") "" else "-${preRelease}"}"
}
