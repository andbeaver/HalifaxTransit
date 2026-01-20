plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    //id("com.google.devtools.ksp") version "2.0.21-1.0.27" apply false
    //id("com.google.devtools.ksp")
    alias(libs.plugins.google.ksp)

    //id("com.google.gms.google-services") version "4.4.4" apply false
}

android {
    namespace = "com.example.halifaxtransit"
    compileSdk = 36


    defaultConfig {
        applicationId = "com.example.halifaxtransit"
        minSdk = 24
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

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.compose.ui.text.google.fonts)
    //implementation(libs.firebase.firestore.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // Mapbox
    implementation("com.mapbox.maps:android-ndk27:11.16.2")
    implementation("com.mapbox.extension:maps-compose-ndk27:11.16.2")

    // GTFS transit feed - https://gtfs.org/realtime/language-bindings/java/
    implementation(group = "org.mobilitydata", name= "gtfs-realtime-bindings", version= "0.0.8")

    implementation("androidx.core:core-splashscreen:1.0.0")
    implementation("androidx.compose.material3:material3:1.1.3")
    implementation("androidx.compose.material:material-icons-extended:1.6.8")

    //implementation(libs.androidx.compose.material.icons.extended)

 //--------- Room -------
    val room_version = "2.8.4"

    implementation("androidx.room:room-runtime:${room_version}")

    // If this project uses any Kotlin source, use Kotlin Symbol Processing (KSP)
    // See Add the KSP plugin to your project
    ksp("androidx.room:room-compiler:$room_version")

}