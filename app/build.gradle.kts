plugins {
    alias(libs.plugins.android.application)
}

// Plugin para google services (aplica el archivo google-services.json)
plugins.apply("com.google.gms.google-services")

android {
    namespace = "com.example.appplication"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.appplication"
        minSdk = 31
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
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    // Firebase (BOM) y librerías necesarias
    implementation(platform("com.google.firebase:firebase-bom:32.4.1"))
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")
    // Firebase UI para Auth (Email y Google)
    implementation("com.firebaseui:firebase-ui-auth:8.0.2")
    // ZXing core para generación de QR
    implementation("com.google.zxing:core:3.5.0")
    // MPAndroidChart para gráficos (resumen)
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
}