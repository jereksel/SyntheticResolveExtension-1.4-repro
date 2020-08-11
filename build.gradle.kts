plugins {
    kotlin("jvm") version "1.3.72"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    maven("https://dl.bintray.com/kotlin/kotlin-eap")
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(kotlin("compiler-embeddable"))

    testImplementation("com.github.tschuchortdev:kotlin-compile-testing:1.2.9")
    testImplementation(kotlin("test-junit"))
}
