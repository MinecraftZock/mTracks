import org.gradle.internal.jvm.Jvm

buildscript {
    repositories {
        google()
        maven { url = uri("https://plugins.gradle.org/m2/") }
    }

    dependencies {
        classpath("com.android.tools.build:gradle:8.13.1")
        classpath("com.github.triplet.gradle:play-publisher:3.13.0")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:2.3.0")
        classpath("com.google.gms:google-services:4.4.4")
        classpath("com.google.firebase:firebase-crashlytics-gradle:3.0.6")
        classpath("org.jlleitschuh.gradle:ktlint-gradle:14.0.1")
    }
}

plugins {
    id("com.google.devtools.ksp") version "2.3.4" apply false
}

println("Gradle uses Java ${Jvm.current()}")

subprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint")

    // Optionally configure plugin
    configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
        debug.set(false)
        android.set(true)
        verbose.set(true)
        outputColorName.set("RED")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }

    // show detailed warnings in gradle output
    tasks {
        withType<JavaCompile> {
            options.compilerArgs.add("-Xlint:deprecation")
            options.compilerArgs.add("-Xlint:unchecked")
        }
    }
}

tasks.register<Delete>("clean") {
    delete(rootProject.file("build"))
}
