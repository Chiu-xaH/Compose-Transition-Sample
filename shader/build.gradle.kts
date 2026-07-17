import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.compose)
    id("maven-publish")
}

configurations.all {
    resolutionStrategy {
        force(
            "org.jetbrains.compose.foundation:foundation:${libs.versions.composeMultiplatform.get()}",
            "org.jetbrains.compose.foundation:foundation-desktop:${libs.versions.composeMultiplatform.get()}"
        )
    }
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
        publishLibraryVariants("release")
    }

    jvm()

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        val commonMain by getting {
            dependencies {
                dependencies {
                    implementation(compose.ui)
                    implementation(compose.foundation)
                }
            }
        }

        val skiaMain by creating {
            dependsOn(commonMain)
        }

        val jvmMain by getting {
            dependsOn(skiaMain)
        }

        val iosX64Main by getting {
            dependsOn(skiaMain)
        }

        val iosArm64Main by getting {
            dependsOn(skiaMain)
        }
        
        val iosSimulatorArm64Main by getting {
            dependsOn(skiaMain)
        }

        androidMain.dependencies {

        }
        skiaMain.dependencies {

        }
    }
}

android {
    namespace = "com.xah.shader"
    compileSdk = Integer.parseInt(libs.versions.maxAndroidVersion.get())

    defaultConfig {
        minSdk = Integer.parseInt(libs.versions.minAndroidVersion.get())

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    buildFeatures {
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

group = project.findProperty("group") as? String ?: libs.versions.libraryPackageName.get()
version = project.findProperty("version") as? String ?: libs.versions.libraryVersionName.get()