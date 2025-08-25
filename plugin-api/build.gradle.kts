plugins {
    id("itemize.maven-conventions")
}

dependencies {
    compileOnly(libs.alpinecore)
    compileOnly(libs.spigotApi)
}

tasks {
    named("build") {
        dependsOn("javadoc")
    }
    withType<Jar>().configureEach {
        includeLicenseFile()
    }
    withType<Javadoc>().configureEach {
        enabled = true
        val v = libs.versions
        val acv = v.alpinecore.get()
        val repo = if (acv.contains("-")) "snapshots" else "releases"
        applyLinks(
            "https://docs.oracle.com/en/java/javase/11/docs/api/",
            "https://hub.spigotmc.org/javadocs/spigot/",
            "https://lib.alpn.cloud/javadoc/${repo}/co/crystaldev/alpinecore/${acv}/raw/",
            "https://jd.advntr.dev/api/${v.adventure.get()}",
            "https://javadoc.io/doc/org.jetbrains/annotations/${v.annotations.get()}/",
            "https://javadoc.io/doc/com.github.cryptomorin/XSeries/${v.xseries.get()}/",
        )
    }
}