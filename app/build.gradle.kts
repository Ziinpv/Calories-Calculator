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
        buildConfigField("String", "OPENAI_API_KEY", "\"${project.properties["OPENAI_API_KEY"]}\"")
    }
    buildFeatures {
        buildConfig = true
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
    implementation(libs.material)
    implementation(libs.firebase.auth)  // If using version catalog
    // OR if not using version catalog:
    implementation("com.google.firebase:firebase-auth:22.3.1")  // Use latest version

    // Also make sure you have the core Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:32.7.2"))
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(libs.glide)
    annotationProcessor(libs.glide.compiler)
    implementation(libs.imagepicker)
    implementation(libs.firebase.storage)
    implementation("com.github.dhaval2404:imagepicker:2.1")
    implementation(libs.cloudinary.android)
    implementation(libs.cloudinary.core)
    implementation(libs.okhttp)
    // Retrofit + GSON
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

}