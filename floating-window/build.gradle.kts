import org.gradle.kotlin.dsl.implementation
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
        commonMain.dependencies {
            implementation(compose.material3)
            implementation(libs.lifecycle.viewmodel)
            implementation(libs.lifecycle.viewmodel.compose)
            api(project(":common"))
            api(project(":shared-container"))
        }
        androidMain.dependencies {
            implementation(libs.accompanist.systemuicontroller)
            implementation(libs.androidx.activity.compose)
        }
        jvmMain.dependencies {

        }
        iosMain.dependencies {

        }
    }
}

android {
    namespace = "com.xah.floating"
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
group = libs.versions.libraryPackageName.get()
version = libs.versions.libraryVersionName.get()
