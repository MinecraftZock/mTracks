plugins {
    id("com.android.library")
}

android {
    namespace = "info.mx.rxcommdata"
    defaultConfig {
        compileSdk = 36
        minSdk = 24
        consumerProguardFiles.addAll(
            listOf(
                file("proguard-RxCommLib.pro")
            )
        )
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin {
        jvmToolchain(17)
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles.addAll(
                listOf(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    file("proguard-RxCommLib.pro"),
                ),
            )
        }
    }
}

dependencies {
    api("com.google.code.gson:gson:2.13.2")
}
