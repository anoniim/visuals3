plugins {
    kotlin("jvm") version "1.3.72"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    // installed in local maven
//    implementation(group = "org.processing", name = "core", version = "3.5.4")
    implementation(group = "org.processing", name = "core", version = "4.0a2")

    // public maven (outdated version)
//    implementation(group = "org.processing", name = "core", version = "3.3.7")
//    implementation(group = "org.processing", name = "video", version = "3.3.7")
//    implementation(group = "org.processing", name = "pdf", version = "3.3.7")
//    implementation(group = "org.processing", name = "net", version = "3.3.7")
//    implementation(group = "org.processing", name = "serial", version = "3.3.7")
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}