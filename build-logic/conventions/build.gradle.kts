plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(libs.mavenPublish.plugin)
    implementation(libs.shadow.plugin)
    implementation(libs.spotless.plugin)

    // https://github.com/gradle/gradle/issues/15383#issuecomment-779893192
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
}