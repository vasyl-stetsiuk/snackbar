@file:OptIn(ExperimentalKotlinGradlePluginApi::class, ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.android.library)
    alias(libs.plugins.mavenPublish)
}

kotlin {
    androidTarget {
        publishLibraryVariants("release")
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    jvm("desktop") {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    js(IR) {
        browser()
    }

    wasmJs {
        browser()
    }

    iosArm64()
    iosX64()
    iosSimulatorArm64()

    sourceSets {
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
        }
        val desktopMain by getting {
            dependencies {}
        }
        val jsMain by getting {
            dependencies {}
        }
        val wasmJsMain by getting {
            dependencies {}
        }
        iosMain.dependencies {}
        commonMain.dependencies {

            // Compose
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.compose.backhandler)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

android {
    namespace = "dev.stetsiuk.compose.snackbar"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
}

mavenPublishing {
    publishToMavenCentral()

    signAllPublications()

    coordinates("dev.stetsiuk", "compose-snackbar", "1.0.0")

    pom {
        name = "Snackbar"
        description = "A flexible and customizable library for Compose Multiplatform with support for Android, iOS, Desktop, Web (JS), and WebAssembly"
        url = "https://github.com/vasyl-stetsiuk/snackbar"
        inceptionYear = "2025"
        licenses {
            license {
                name = "The Apache License, Version 2.0"
                url = "http://www.apache.org/licenses/LICENSE-2.0.txt"
            }
        }
        developers {
            developer {
                id = "vasyl-stetsiuk"
                name = "Vasyl Stetsiuk"
                email = "stecyuk.vasil@gmail.com"
            }
        }
        scm {
            url = "https://github.com/vasyl-stetsiuk/snackbar"
            connection = "scm:git:git://github.com/vasyl-stetsiuk/snackbar.git"
            developerConnection = "scm:git:ssh://git@github.com/vasyl-stetsiuk/snackbar.git"
        }
    }
}