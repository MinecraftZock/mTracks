import info.git.versionHelper.getGitCommitCount
import info.git.versionHelper.getLatestCommitText
import info.git.versionHelper.getLatestGitHash
import info.git.versionHelper.getTagGroupedGitlog
import info.git.versionHelper.getVersionText
import info.shell.getDate
import info.shell.getUnixCreateTime
import info.shell.runCommand
import org.gradle.internal.extensions.stdlib.toDefaultLowerCase
import org.gradle.internal.logging.text.StyledTextOutput.Style
import org.gradle.kotlin.dsl.support.serviceOf
import java.io.FileInputStream
import java.util.Properties

plugins {
    id("com.android.application")
    id("com.github.triplet.play")
    id("kotlin-android")
    id("com.google.devtools.ksp")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

val versionOffset = 2109
val keystorePropertiesFile = rootProject.file("signing/keystore.properties")
val keystoreProperties = Properties()
if (keystorePropertiesFile.exists()) {
    keystoreProperties.load(FileInputStream(keystorePropertiesFile))
}

// TODO redundant code, same like RxCommLib
val mapKey = if (keystoreProperties.containsKey("mapKey"))
    keystoreProperties.getProperty("mapKey")
else
    "dummy"
val backendKey = "BACKEND_PROD_URL"
val backendUrl = if (keystoreProperties.containsKey(backendKey))
    keystoreProperties.getProperty(backendKey)!!
else
    "http://backend.org"

val httpLoggingLevel = "HTTP_LOGGING_LEVEL"
val RUN_CI = "RUN_CI"
val SHOW_WEATHER = "SHOW_WEATHER"
val commitSHA1 = "COMMIT_SHA1"
val pushServerSleep = "PUSH_SERVER_SLEEP"
val unixTime = "UNIX_TIME_CREATED"

android {
    namespace = "info.mx.tracks"
    defaultConfig {
        compileSdk = 36
        targetSdk { version = release(36) }

        versionCode = getGitCommitCount(versionOffset)
        minSdk = 23
        versionName = getVersionText()

        println("versionName=${versionName} versionCode=$versionCode")

        resValue("string", backendKey, backendUrl)
        buildConfigField("Boolean", SHOW_WEATHER, "true")
        buildConfigField("String", httpLoggingLevel, "\"NONE\"")  //NONE, BASIC, HEADERS, BODY
        buildConfigField("String", commitSHA1, "\"" + getLatestGitHash() + "\"")
        buildConfigField("long", unixTime, "${getUnixCreateTime()}L")
        multiDexEnabled = true

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        testInstrumentationRunnerArguments.putAll(
            mapOf(
                "useTestStorageService" to "true",
            ),
        )

        sourceSets {
            getByName("androidTest") {
                assets.srcDirs(files("$projectDir/schemas"))
            }
        }
//        sourceSets {
//            androidTest.assets.srcDirs += files("$projectDir/schemas".toString())
//        }
    }

    ksp {
        arg("room.schemaLocation", "$projectDir/schemas")
        arg("room.incremental", "true")
    }

    signingConfigs {
        create("debugCI") {
            storeFile = file("../signing/debug.keystore")
            storePassword = "android"
            keyPassword = "android"
            keyAlias = "androiddebugkey"
        }
        create("release") {
            storeFile = file("../signing/release.keystore")
            storePassword = keystoreProperties.getProperty("storePassword")
            keyAlias = keystoreProperties.getProperty("keyAlias")
            keyPassword = keystoreProperties.getProperty("keyPassword")
        }
    }
    packaging {
        resources {
            excludes += listOf(
                "META-INF/NOTICE.txt",
                "META-INF/LICENSE.txt",
                "META-INF/rxjava.properties",
                "META-INF/DEPENDENCIES"
            )
            pickFirsts += listOf("META-INF/atomicfu.kotlin_module")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin {
        jvmToolchain {
            languageVersion.set(JavaLanguageVersion.of(17))
        }
    }

    buildTypes {
        debug {
            if (System.getenv("CI") == "true") { // Github action
                signingConfig = signingConfigs.getByName("debugCI")
                println("I run on Github and use for 'debug' the DEBUG signing")
                buildConfigField("boolean", RUN_CI, "true")
                resValue("string", "GOOGLE_MAP_API_KEY", "dummy")
            } else {
                signingConfig = signingConfigs.getByName("release")
                println("I use for 'debug' the RELEASE signing. Reason is to run it locally")
                buildConfigField("boolean", RUN_CI, "false")
                resValue("string", "GOOGLE_MAP_API_KEY", mapKey.toString())
            }

            //applicationIdSuffix ".debug"
            versionNameSuffix = ".debug"
            buildConfigField("boolean", "FILE_LOGGING", "true")
            buildConfigField("String", httpLoggingLevel, "\"BODY\"")  //NONE, BASIC, HEADERS, BODY
            buildConfigField("int", pushServerSleep, "1000")
        }
        release {
            println("I use for 'release' the RELEASE signing")
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = false
            //define whether log output gets logged into log file
            buildConfigField("boolean", RUN_CI, "false")
            buildConfigField("boolean", "FILE_LOGGING", "false")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard.cfg",
                "proguard_admin.cfg",
                "proguard_admin_mechanoid.cfg",
                "proguard-core.txt",
                "proguard-RxCommAdminLib"
            )
            testProguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "$projectDir/proguard.cfg",
                "$projectDir/proguard_admin.cfg",
                "$projectDir/proguard_admin_mechanoid.cfg",
                "$projectDir/proguard-test-rules.pro",
                "$projectDir/proguard-RxCommAdminLib"
            )
            buildConfigField("int", pushServerSleep, "1500")
            resValue("string", "GOOGLE_MAP_API_KEY", mapKey.toString())
        }
    }

    flavorDimensions += "kind"
    productFlavors {
        create("paid") {
            dimension = "kind"
            applicationId = "info.mx.tracks"
        }
        create("admin") {
            dimension = "kind"
            applicationId = "info.hannes.mxadmin"
            //versionNameSuffix = "-Admin"
            buildConfigField("Boolean", SHOW_WEATHER, "false")
        }
        create("free") {
            dimension = "kind"
            applicationId = "info.mx.free"
            //versionNameSuffix = "-Free"
        }
    }

    sourceSets {
        named("main") {
            java.srcDirs("src/main/java", "../core/src", "../core/src-gen")
            res.srcDirs("src/main/res", "../core/res")
            assets.srcDirs("assets", "../core/assets")
            //disable automatic ndk-build
        }
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
    lint {
        checkReleaseBuilds = false
        disable += listOf("MissingTranslation", "UnusedResources", "InvalidPackage", "Range")
    }
    // https://stackoverflow.com/a/67635863/1079990
    testOptions {
        animationsDisabled = true
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar", "*.aar"))))

    //implementation(project(path: ":access"))
    implementation(project(":commonLib"))

    implementation("com.github.hannesa2:FloatingActionButton:2.4.0")

    implementation("com.google.firebase:firebase-core:21.1.1")
    implementation(platform("com.google.firebase:firebase-bom:33.16.0"))
    implementation("com.google.firebase:firebase-analytics:22.5.0") // (Recommended) Add Analytics
    implementation("com.google.firebase:firebase-crashlytics:20.0.1")

    implementation("androidx.constraintlayout:constraintlayout:2.2.1")
    implementation("androidx.test.espresso:espresso-idling-resource:3.6.1")
    "freeImplementation"("com.google.android.gms:play-services-ads:24.5.0")

    val koin_version = "4.1.1"
    implementation("io.insert-koin:koin-android:$koin_version")

    implementation("io.insert-koin:koin-android-compat:$koin_version")
    implementation("io.insert-koin:koin-androidx-workmanager:$koin_version")
    implementation("io.insert-koin:koin-androidx-navigation:$koin_version")
    implementation("io.insert-koin:koin-androidx-compose:$koin_version")

    val work_version = "2.10.5"
    implementation("androidx.work:work-runtime:$work_version")
    implementation("androidx.work:work-runtime-ktx:$work_version")

    implementation("androidx.multidex:multidex:2.0.1")

    "adminImplementation"("org.jsoup:jsoup:1.21.2")
    implementation("com.github.hannesa2:android-maps-extensions:3.2.1")
    implementation("com.github.hannesa2:AndroidSlidingUpPanel:4.7.1")
    implementation("com.github.MikeOrtiz:TouchImageView:3.7.1")

    implementation("androidx.browser:browser:1.9.0")
    implementation("androidx.vectordrawable:vectordrawable-animated:1.2.0")

    val room = "2.8.2"
    implementation("androidx.room:room-runtime:$room")
    ksp("androidx.room:room-compiler:$room")
    implementation("androidx.room:room-rxjava2:2.8.1")
    implementation("androidx.room:room-testing:$room")

    implementation("com.github.AppDevNext:ChangeLog:3.7.1")
    implementation("com.github.AppDevNext.Logcat:LogcatCrashlyticLib:3.4")

    // core
    implementation("com.github.hannesa2:mechanoid:4.5")
    implementation(project(":RxCommLib"))
    implementation("com.google.code.gson:gson:2.13.2")
    implementation("net.lingala.zip4j:zip4j:2.11.5")
    implementation("com.github.hannesa2:android-emulator-detector:1.5")
    implementation("com.google.android.gms:play-services-maps:19.2.0")
    implementation("com.google.android.gms:play-services-location:21.3.0")
    implementation("com.google.android.libraries.places:places:4.4.1")
    implementation("com.google.maps.android:android-maps-utils:3.19.0")

    "androidTestImplementation"("androidx.test.uiautomator:uiautomator:2.3.0")
    "androidTestImplementation"("org.hamcrest:hamcrest-integration:1.3")

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:2.2.20")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.9.2")

    "androidTestImplementation"("androidx.test.ext:junit-ktx:1.2.1")
    androidTestUtil("androidx.test.services:test-services:1.6.0")
    "androidTestImplementation"("androidx.test.espresso:espresso-core:3.6.1")
    "androidTestImplementation"("androidx.test.espresso:espresso-intents:3.6.1")
    "androidTestImplementation"("androidx.test:rules:1.6.1") //    GrantPermissionRule
}

tasks.register("disableAnimation") {
    doLast {
        println("doLast for disableAnimation for emulator")
        // no blinking cursor in EditView
        val adbOutput = "adb shell settings put secure show_ime_with_hard_keyboard 0".runCommand()
        if (adbOutput.isNotBlank())
            println("enableMock output='$adbOutput'")
    }
}

tasks.whenTaskAdded {
    if (this.name == "connectedPaidDebugAndroidTest") {
        dependsOn("disableAnimation")
    }
}

tasks.register("newVersion") {
    if (gradle.startParameter.taskNames.contains("staticAnalysis")) {
        println("Running static analysis: disabling release builds!")
        // config.params.disableReleaseBuilds = true TODO kts issue
    }
    group = "verification"
    description = "Run all the static analysis and Unit tests needed for CI"
    //    dependsOn("detekt")
    //    dependsOn("checkstyle")
    //    dependsOn(getTasksByName("lint", true).findAll { task -> !task.path.contains(":test-") })
    //    dependsOn(getTasksByName("test", true).findAll { task -> !task.path.contains(":test-") })
    //dependsOn "connectedAndroidTest"
    dependsOn("tagNewVersion")
}

tasks.named("clean") {
    doLast {
        getTagGroupedGitlog(
            //filter = "PROD-",
            filename = "app/src/admin/res/raw/gitlog.json"
        )
    }
}

tasks.register("tagNewVersion") {
    group = "Release"
    description = "Before we test, then a tag is crated"

    doLast {
        addNewTag()
        pushTag()
    }
}

play {
    serviceAccountCredentials = file("../signing/Surveilance-playstore.json")
    track = "alpha"
}

fun addNewTag() {
    val tag = getDate(onlyMonth = true) + ".${getGitCommitCount() + versionOffset}"
    val out = project.serviceOf<org.gradle.internal.logging.text.StyledTextOutputFactory>()
        .create("an-output")
    out.style(Style.Normal).text("Create new tag ")
        .style(Style.SuccessHeader).text(tag)
    out.style(Style.Normal).text("\nlast commit message -> ")
        .style(Style.SuccessHeader).text(getLatestCommitText())
        .style(Style.Info).println(" ")
    val tagCommand = "git tag -a $tag -m \"${getLatestCommitText()}\""
    println(tagCommand)
    val tagResult = tagCommand.runCommand()
    if (tagResult.isNotBlank())
        println("addNewTag output=$tagResult")
    else
        println("addNewTag done")
}

fun pushTag() {
    val command = "git push --tags"
    val tagResult = command.runCommand()
    val out = project.serviceOf<org.gradle.internal.logging.text.StyledTextOutputFactory>()
        .create("an-output")
    if (tagResult.isNotBlank())
        println("push output=$tagResult")
    else {
        out.style(Style.Normal).text("$command ")
            .style(Style.Info).println(" SUCCESSFULLY PUSHED")
    }
    println("")
}

tasks.configureEach {
    if (name.startsWith("generate") &&
        name.endsWith("ResValues") &&
        !name.contains("AndroidTest")
    ) {
        val flavorName = name.removePrefix("generate").removeSuffix("ResValues")
            .removeSuffix("Debug").removeSuffix("Release")
        val generateChangelogTask = "changelog${flavorName}"
        println("Configuring task: $this $flavorName $generateChangelogTask")
        doLast {
            val filterFlavor = if (flavorName.toDefaultLowerCase() == "admin") null else "PROD-"
            println("$name getTagGroupedGitlog on do Last $flavorName filter=$filterFlavor")
            getTagGroupedGitlog(
                filter = filterFlavor,
                filename = "app/src/${flavorName.toDefaultLowerCase()}/res/raw/gitlog.json"
            )
        }
    }
}
