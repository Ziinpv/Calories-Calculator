plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.calories_caculator"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.calories_caculator"
        minSdk = 27
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables.useSupportLibrary = true // Thêm dòng này để hỗ trợ vector
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

    // Thêm block này để tối ưu hiệu suất
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.firestore)
    implementation(libs.picasso)
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.androidyoutubeplayer)

    // Thêm thư viện GIF
    implementation ("pl.droidsonroids.gif:android-gif-drawable:1.2.28")

    // Giảm kích thước APK
    implementation("androidx.multidex:multidex:2.0.1")


    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

}