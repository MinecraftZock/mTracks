import java.io.FileInputStream
import java.util.Properties

plugins {
    id("com.android.library")
    id("kotlin-android")
}

// TODO redundant code, same like MxApp
val keystorePropertiesFile = rootProject.file("signing/keystore.properties")
val keystoreProperties = Properties()
if (keystorePropertiesFile.exists()) {
    keystoreProperties.load(FileInputStream(keystorePropertiesFile))
}

val backendKey = "BACKEND_PROD_URL"
val backendUrl: String = keystoreProperties.getProperty(backendKey).ifEmpty { "http://backend.org" }

android {
    namespace = "info.mx.rxcommlibrary"
    defaultConfig {
        minSdk = 23
        compileSdk = 35
        consumerProguardFiles.addAll(
            listOf(
                file("proguard-RxCommLib.pro")
            )
        )

        // TODO rework it
        buildConfigField("String", backendKey, "\"$backendUrl\"")
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
    lint {
        abortOnError = false
    }
    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(project(":commonLib"))
    implementation("androidx.appcompat:appcompat:1.7.0")
    api(project(":RxCommData"))
    api("com.google.code.gson:gson:2.11.0")

    api("io.reactivex.rxjava2:rxandroid:2.1.1")
    api("io.reactivex.rxjava2:rxjava:2.2.21")

    val retrofitVersion = "2.11.0"
    api("com.squareup.retrofit2:retrofit:${retrofitVersion}")
    api("com.squareup.retrofit2:converter-gson:${retrofitVersion}")
    api("com.squareup.retrofit2:adapter-rxjava2:${retrofitVersion}")
    api("com.squareup.retrofit2:converter-scalars:${retrofitVersion}")

    api("com.squareup.okhttp3:logging-interceptor:4.12.0")
    api("commons-io:commons-io:2.17.0")

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:2.1.0")
}
