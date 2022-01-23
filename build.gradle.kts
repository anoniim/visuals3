import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.20"
}

group = "net.solvetheriddle"
version = "0.1"

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
//    implementation(kotlin("stdlib-jdk8")) // Probably included automatically
//    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.0")

    // Local (copied from Processing4)
    implementation(jarsFrom("libs/processing"))
    implementation(jarsFrom("libs/contributed/sound"))
    implementation(jarsFrom("libs/contributed/VideoExport"))
    implementation(jarsFrom("libs/contributed/themidibus"))
    implementation(jarsFrom("libs/contributed/peasycam"))
    implementation(jarsFrom("libs/contributed/gicentreUtils"))

    // Installed in local maven
//    implementation(group = "org.processing", name = "core", version = "3.5.4")
//    implementation(group = "org.processing", name = "core", version = "4.0a2")

    // Public maven (outdated version)
//    implementation(group = "org.processing", name = "core", version = "3.3.7")
//    implementation(group = "org.processing", name = "video", version = "3.3.7")
//    implementation(group = "org.processing", name = "pdf", version = "3.3.7")
//    implementation(group = "org.processing", name = "net", version = "3.3.7")
//    implementation(group = "org.processing", name = "serial", version = "3.3.7")

//    // 3rd party
//    implementation("net.compartmental.code:minim:2.2.2")
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions.jvmTarget = "11"

fun jarsFrom(folder: String): ConfigurableFileTree {
    return fileTree(
            mapOf(
                "dir" to folder,
                "include" to listOf("*.jar")
            )
        )
}