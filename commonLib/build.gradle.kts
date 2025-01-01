plugins {
    id("com.android.library")
    id("kotlin-android")
}

android {
    namespace = "info.hannes.commonlib"
    defaultConfig {
        minSdk = 23
        compileSdk = 35
    }
    lint {
        abortOnError = false
        disable += "ObsoleteLintCustomCheck"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation("com.google.android.material:material:1.12.0")

    api("com.github.matomo-org:matomo-sdk-android:4.3")
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:2.1.0")
}
