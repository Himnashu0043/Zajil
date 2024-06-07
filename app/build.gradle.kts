plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.zajil"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.zajil"
        minSdk = 24
        targetSdk = 34
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
    buildFeatures {
        viewBinding = true
    }
    bundle{
        language {
            enableSplit = false
        }
    }
}

dependencies {

    implementation("androidx.browser:browser:1.6.0")
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("io.github.chaosleung:pinview:1.4.4")
    //noinspection UseOfBundledGooglePlayServices
    implementation("com.google.android.gms:play-services:12.0.1")
    implementation("com.google.maps.android:android-maps-utils:3.4.0")

    implementation("com.github.bumptech.glide:glide:4.16.0")

    //Retrofit and GSON
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.google.code.gson:gson:2.9.0")

    implementation("com.squareup.okhttp3:logging-interceptor:3.9.1")
    implementation("com.squareup.okhttp3:okhttp:3.14.9")

    // this for coroutine and mvvm
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")

//    implementation("com.google.android.gms:play-services-vision:20.1.3")

    implementation("com.google.android.gms:play-services-maps:18.2.0")
    implementation("com.google.android.gms:play-services-location:21.0.1")

    implementation("com.google.firebase:firebase-auth:22.2.0")
//    implementation(platform("com.google.firebase:firebase-bom:32.3.1"))
    implementation("com.google.firebase:firebase-iid:21.1.0")
    implementation("com.google.firebase:firebase-messaging:23.3.1")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.2.0-alpha01")

//    implementation("com.google.firebase:firebase-auth-ktx")

}