plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.feduss.horizontalpagerbug"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.feduss.horizontalpagerbug"
        minSdk = 33
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.2"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation ("androidx.wear.compose:compose-ui-tooling:1.2.0")
    implementation("androidx.wear.compose:compose-foundation:1.2.0")
    implementation("androidx.wear.compose:compose-material:1.2.0")
    implementation("androidx.wear.compose:compose-navigation:1.2.0")
    implementation("com.google.android.horologist:horologist-compose-layout:0.5.7")
}