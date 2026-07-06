plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "eu.konggdev.strikemaps"
    compileSdk = 36

    defaultConfig {
        applicationId = "eu.konggdev.strikemaps"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_15
        targetCompatibility = JavaVersion.VERSION_15
    }

    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
        }
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.viewpager2)

    implementation("org.osmdroid:osmdroid-android:6.1.20")
    implementation("org.maplibre.gl:android-sdk:11.13.0")
    implementation("com.github.mapsforge.vtm:vtm:0.27.0")
    implementation("com.github.mapsforge.vtm:vtm-android:0.27.0")
    implementation("com.github.mapsforge.vtm:vtm-http:0.27.0")
    implementation ("com.github.mapsforge.vtm:vtm-android:0.27.0@jar")
    runtimeOnly ("com.github.mapsforge.vtm:vtm-android:0.27.0:natives-armeabi-v7a@jar")
    runtimeOnly("com.github.mapsforge.vtm:vtm-android:0.27.0:natives-arm64-v8a@jar")
    runtimeOnly("com.github.mapsforge.vtm:vtm-android:0.27.0:natives-x86@jar")
    runtimeOnly("com.github.mapsforge.vtm:vtm-android:0.27.0:natives-x86_64@jar")
    implementation("com.google.guava:guava:33.2.1-android")
    implementation("com.caverock:androidsvg:1.4")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.15.0")
    implementation("org.apache.commons:commons-lang3:3.14.0")
    implementation(libs.core.ktx)

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
