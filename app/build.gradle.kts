plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.fredrueda.huecoapp"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.fredrueda.huecoapp"
        minSdk = 23
        targetSdk = 36
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    // --- Core Android ---
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // --- Jetpack Compose ---
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material)

    implementation(libs.googleid)
    implementation(libs.androidx.preference.ktx)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // --- Navigation Compose ---
    implementation(libs.androidx.navigation.compose)

    // --- Lifecycle / MVVM ---
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)

    // --- Coroutines ---
    implementation(libs.kotlinx.coroutines.android)

    // --- Hilt (Dependency Injection) ---
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

    // --- Retrofit + OkHttp ---
    implementation(libs.retrofit)
    implementation(libs.logging.interceptor)
    implementation(libs.converter.gson)
    // DataStore
    implementation(libs.androidx.datastore.preferences)

    // --- Coil (imágenes Compose) ---
    implementation(libs.coil.compose)

    // --- Firebase (FCM) ---
    implementation(platform(libs.firebase.bom))
    implementation("com.google.firebase:firebase-messaging-ktx")

    implementation(libs.accompanist.navigation.animation)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.accompanist.systemuicontroller)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.coil.svg)
    implementation(libs.osmdroid.android)

    // --- Facebook Sign-in --
    implementation("com.facebook.android:facebook-login:16.3.0")
    // --- Google Sign-In ---
    implementation(libs.play.services.auth)
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    // --- Firebase Auth ---
    implementation(libs.firebase.auth.ktx)
    // --- Facebook Login ---
    implementation(libs.facebook.android.sdk)

    //Material Icons (Filled, Outlined, Rounded, TwoTone y Sharp)
    implementation(libs.androidx.compose.material.icons.extended)

    // --- Testing ---
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)

    // --- CameraX (Android 6 - 15) ---
    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)
    implementation(libs.androidx.camera.extensions)
}