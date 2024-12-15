import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("org.jetbrains.kotlin.plugin.serialization") version "2.0.20"
//    id("com.google.secrets_gradle_plugin") version "0.6"
}

android {
    namespace = "com.example.atob"
    compileSdk = 35
    buildFeatures {
        buildConfig = true // Enable BuildConfig
        compose = true
    }
    defaultConfig {
        applicationId = "com.example.atob"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        val localProperties = Properties()
        val localPropertiesFile = rootProject.file("local.properties")
        if (localPropertiesFile.exists()) {
            localProperties.load(localPropertiesFile.inputStream())
        }
        buildConfigField("String", "LocalHost", "\"${localProperties.getProperty("LocalHost")}\"")
        manifestPlaceholders["API_KEY"] = localProperties.getProperty("API_KEY")



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

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

}

dependencies {

     implementation(libs.androidx.lifecycle.viewmodel.compose)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.espresso.core)
    implementation(libs.androidx.constraintlayout.compose)

    // Retrofit
    implementation(libs.retrofit)

    //Efficient image loading
    implementation(libs.coil.compose)
     //navigation
    implementation(libs.androidx.navigation.compose)
    //Calendar
    // The view calendar library for Android
    implementation(libs.calendar.view)

    // The compose calendar library for Android
    implementation(libs.compose.v260)
    // OkHttp dependency
    implementation(libs.okhttp)
   // implementation (libs.converter.scalars)
    implementation(libs.converter.scalars)
    implementation(libs.retrofit2.kotlinx.serialization.converter)
    implementation(libs.kotlinx.serialization.json.v151)

    //   implementation(libs.retrofit2.kotlinx.serialization.converter.v080) // Kotlinx Serialization
    implementation(libs.material)
    implementation(libs.androidx.tools.core) // Kotlinx Serialization JSON library
    //http logging intercept okhttp
    implementation(libs.logging.interceptor)
    implementation(libs.spring.security.crypto)
    implementation(libs.commons.logging)

    implementation(libs.androidx.room.runtime.android)
    implementation(libs.androidx.room.ktx)
    implementation(libs.play.services.location)
    implementation (libs.play.services.maps)
    implementation (libs.maps.compose)
    implementation(libs.androidx.ui.test.android)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}