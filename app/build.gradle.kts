import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.android.libraries.mapsplatform.secrets.gradle.plugin)
    id("kotlin-kapt") // Add this for Glide

}

android {
    namespace = "com.example.fbenddemo"
    compileSdk = 35 //updated to mathe the depenciences

    //FOR GOOGLE AUTHEN
    val file = rootProject.file("local.properties")
    val properties = Properties()
    properties.load(file.inputStream())



    defaultConfig {
        applicationId = "com.example.fbenddemo"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField(
            "String",
            "WEB_CLIENT_ID",
            "\"212851287364-401toqbjds0q2aufceds36g3icv93mp6.apps.googleusercontent.com\""
        )    }

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
        viewBinding = true
        buildConfig = true
    }
}

dependencies {
    //google authan
    implementation("androidx.credentials:credentials:1.3.0-rc01")

    // optional - needed for credentials support from play services, for devices running
    // Android 13 and below.
    implementation("androidx.credentials:credentials-play-services-auth:1.3.0")
    implementation(libs.googleid)

    implementation(libs.glide)
    implementation(libs.googleid) // Add Glide
    kapt(libs.glide.compiler)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.play.services.maps)
    implementation(libs.play.services.location)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}