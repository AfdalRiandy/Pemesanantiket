plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.example.pemesanantiket"
    compileSdk = 34 // Pastikan ini adalah versi terbaru

    defaultConfig {
        applicationId = "com.example.pemesanantiket"
        minSdk = 26 // Disarankan untuk diatur ke 26 atau lebih tinggi
        targetSdk = 34 // Pastikan ini adalah versi terbaru
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0") // Update with the latest version if needed
    implementation("androidx.appcompat:appcompat:1.6.1") // Update with the latest version if needed
    implementation("com.google.android.material:material:1.9.0") // Update with the latest version if needed
    implementation("androidx.activity:activity-ktx:1.7.2") // Update with the latest version if needed
    implementation("androidx.constraintlayout:constraintlayout:2.1.4") // Update with the latest version if needed
    testImplementation("junit:junit:4.13.2") // Update with the latest version if needed
    androidTestImplementation("androidx.test.ext:junit:1.1.5") // Update with the latest version if needed
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1") // Update with the latest version if needed
}
