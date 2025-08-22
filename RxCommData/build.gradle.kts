plugins {
    id("com.android.library")
    id("kotlin-android")
}

android {
    namespace = "info.mx.rxcommdata"
    defaultConfig {
        compileSdk =  36
        minSdk = 23
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
    kotlinOptions {
        jvmTarget = "17"
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles.addAll(
                listOf(
                    getDefaultProguardFile("proguard-android.txt"),
                    file("proguard-RxCommLib.pro"),
                ),
            )
        }
    }
}

dependencies {
    api("com.google.code.gson:gson:2.11.0")
}
