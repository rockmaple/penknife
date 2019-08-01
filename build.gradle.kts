import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("java")
    kotlin("jvm") version "1.3.41"
}

group = "penknife"
version = "1.0"
description = "penknife"
java.sourceCompatibility = JavaVersion.VERSION_1_8

repositories {
    mavenCentral()
}

dependencies {
    compile("org.jetbrains.kotlin:kotlin-stdlib")
    compile("com.google.guava:guava:28.0-jre")
    compile("org.apache.commons:commons-math3:3.6.1")
    testCompile("junit:junit:4.12")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "1.8"
    }
}

