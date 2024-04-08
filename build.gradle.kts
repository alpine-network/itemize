import java.nio.file.Files
import java.nio.file.StandardCopyOption

plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("io.papermc.paperweight.userdev") version "1.5.10"
}

allprojects {
    apply(plugin = "java")
    apply(plugin = "io.papermc.paperweight.userdev")

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
        maven("https://repo.panda-lang.org/releases")
        maven("https://lib.alpn.cloud/alpine-public/")
        maven("https://repo.codemc.io/repository/maven-public/")
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    }

    configurations.create("shaded")
    dependencies {
        paperweight.paperDevBundle(rootProject.property("paper_version") as String)
        compileOnly(group = "co.crystaldev", name = "alpinecore", version = "0.3.6")

        compileOnly(group = "org.projectlombok", name = "lombok", version = "1.18.30")
        annotationProcessor(group = "org.projectlombok", name = "lombok", version = "1.18.30")
    }

    tasks {
        java {
            sourceCompatibility = JavaVersion.VERSION_17
            targetCompatibility = JavaVersion.VERSION_17
        }

        withType<JavaCompile> {
            options.encoding = "UTF-8"
        }

        register("replaceTokens") {
            doLast {
                // Define the temporary directory where the files will be copied to
                val tempSrcDir = File(project.buildDir, "tempSrc")
                if (tempSrcDir.exists())
                    tempSrcDir.deleteRecursively()

                // Copy all Java files from 'src/main/java' to the temporary directory
                copy {
                    from("src/main/java")
                    into(tempSrcDir)
                }

                val javaFiles = project.fileTree(tempSrcDir) {
                    include("**/*.java")
                }

                javaFiles.forEach { file ->
                    var content = file.readText()
                    props.forEach {
                        val token = "{{ ${it.key} }}"
                        if (content.contains(token)) {
                            content = content.replace(token, it.value)
                            file.writeText(content)
                        }
                    }
                }
            }
        }

        compileJava {
            dependsOn("replaceTokens")

            // Change the Java compilation source to the modified files in the temp directory
            source = fileTree("${buildDir}/tempSrc")
        }

        processResources {
            duplicatesStrategy = DuplicatesStrategy.INCLUDE
            inputs.properties(props)
            filesMatching("plugin.yml") {
                expand(props)
            }
        }

        assemble {
            dependsOn(reobfJar)
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
    // shadow
    shadowJar {
        configurations = listOf(project.configurations["shaded"])
        archiveClassifier.set("dev-shadow")
        archiveFileName.set("${rootProject.property("plugin_name")}-${compileVersion(true)}.jar")

        doLast {
            val input = archiveFile.get()
            val outputDir = File(rootProject.rootDir, "builds")
            val outputFile = File(outputDir, input.asFile.name)
            outputDir.mkdirs()
            Files.copy(input.asFile.toPath(), outputFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
        }
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
    return "${major}.${minor}.${patch}${if (!prerelease || preRelease == "none") "" else preRelease}"
}