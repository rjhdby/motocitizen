import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.android.application") version "8.10.1"
    id("org.jetbrains.kotlin.android") version "2.1.21"
    id("com.google.gms.google-services") version "4.4.2"
}

val kotlin_version = "2.1.21"
val okhttp = "4.12.0"
val coroutines_version = "1.10.2"

android {
    namespace = "motocitizen.main"
    compileSdk = 35

    defaultConfig {
        applicationId = "motocitizen.main"
        minSdk = 28
        targetSdk = 35
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                    getDefaultProguardFile("proguard-android.txt"),
                    "proguard-rules.pro"
            )
        }
        getByName("debug") {
            isMinifyEnabled = false
        }
    }

    lint {
        checkReleaseBuilds = false
        abortOnError = false
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    sourceSets {
        getByName("main") {
            manifest.srcFile("AndroidManifest.xml")
            java.setSrcDirs(listOf("Motocitizen/src", "Motocitizen/assets"))
            resources.setSrcDirs(listOf("Motocitizen/src", "Motocitizen/assets"))
            aidl.setSrcDirs(listOf("Motocitizen/src", "Motocitizen/assets"))
            renderscript.setSrcDirs(listOf("Motocitizen/src", "Motocitizen/assets"))
            res.setSrcDirs(listOf("Motocitizen/res", "Motocitizen/res/tablerows"))
            assets.setSrcDirs(listOf("Motocitizen/assets"))
        }
        getByName("debug").setRoot("build-types/debug")
        getByName("release").setRoot("build-types/release")
    }

    packagingOptions {
        resources {
            excludes += listOf(
                    "META-INF/DEPENDENCIES",
                    "META-INF/LICENSE",
                    "META-INF/LICENSE.txt",
                    "META-INF/license.txt",
                    "META-INF/NOTICE",
                    "META-INF/NOTICE.txt",
                    "META-INF/notice.txt",
                    "META-INF/ASL2.0",
                    "META-INF/motocitizen_debug.kotlin_module"
            )
        }
    }
}

dependencies {
    implementation("com.google.android.gms:play-services-location:21.3.0")
    implementation("com.google.android.gms:play-services-maps:19.2.0")
    implementation("com.google.firebase:firebase-core:21.1.1")
    implementation("com.google.firebase:firebase-messaging:24.1.1")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:$kotlin_version")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutines_version")
    implementation("com.squareup.okhttp3:okhttp:$okhttp")
    implementation("com.squareup.okhttp3:logging-interceptor:$okhttp")
    implementation("com.karumi:dexter:5.0.0")
    implementation("com.vk:androidsdk:2.1.1")
    implementation("com.google.android.gms:play-services-auth:21.3.0")
    implementation("androidx.preference:preference-ktx:1.2.1")

    // Jetpack Compose (если нужно)
    implementation("androidx.compose.ui:ui:1.8.2")
    implementation("androidx.compose.material3:material3:1.3.2")
    implementation("androidx.activity:activity-compose:1.10.1")
}

repositories {
    google()
    mavenCentral()
}
