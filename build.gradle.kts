plugins {
    kotlin("jvm") version "1.3.72"
}

group = "net.solvetheriddle"
version = "0.1"

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    // Local (copied from Processing4)
//    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(files("/libs/processing"))
    implementation(files("/libs/contributed"))

    // Installed in local maven
//    implementation(group = "org.processing", name = "core", version = "3.5.4")
    implementation(group = "org.processing", name = "core", version = "4.0a2")
    implementation(group = "org.processing", name = "sound", version = "2.2.3")
    implementation(group = "org.processing", name = "sound", version = "2.2.3", classifier = "jsyn")
    implementation(group = "org.processing", name = "sound", version = "2.2.3", classifier = "javamp3")

    // Public maven (outdated version)
//    implementation(group = "org.processing", name = "core", version = "3.3.7")
//    implementation(group = "org.processing", name = "video", version = "3.3.7")
//    implementation(group = "org.processing", name = "pdf", version = "3.3.7")
//    implementation(group = "org.processing", name = "net", version = "3.3.7")
//    implementation(group = "org.processing", name = "serial", version = "3.3.7")

//    // 3rd party
//    implementation("net.compartmental.code:minim:2.2.2")
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}