plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("androidx.navigation.safeargs.kotlin")
    id("com.google.dagger.hilt.android")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

android {
    namespace = "com.anetos.parkme"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.anetos.parkme"
        minSdk = 21
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        //vectorDrawables.useSupportLibrary = true
        vectorDrawables {
            useSupportLibrary = true
        }
        multiDexEnabled = true
    }


//    signingConfigs {
//        release {
//            storeFile file("../keystore/PARKME")
//            storePassword 'Parkme@123'
//            keyAlias "anetos"
//            keyPassword 'Parkme@123'
//        }
//        debug_signing_keys {
//            storeFile file("../keystore/PARKME")
//            storePassword 'Parkme@123'
//            keyAlias "anetos"
//            keyPassword 'Parkme@123'
//        }
//    }
    buildTypes {
        release {
            isShrinkResources = true
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            manifestPlaceholders["app_name"] = "Parkme"
            /*firebaseCrashlytics {
                // If you don't need crash reporting for your debug build,
                // you can speed up your build by disabling mapping file uploading.
                mappingFileUploadEnabled false
            }*/
        }
    }


    flavorDimensions += "default"
    productFlavors {
        create("parkme") {
            //applicationId 'com.anetos.parkme'
            //applicationIdSuffix System.getenv("HOCKEY_BUNDLE_ID") != null ? "" : ".neoDevMob"
            //minSdkVersion 16
            //versionCode project.inCode
            //versionName project.inVersion

            buildConfigField("String", "BASE_URL", "\"https://\"")
            buildConfigField("String", "PAYMENT_URL", "\"https://\"")
            buildConfigField("Boolean", "SHOW_VERSION_TOAST", "true")
            //signingConfig signingConfigs.release
            //Release keys for one signal
            manifestPlaceholders["ENABLE_CLEARTEXT_TRAFFIC"] = "false"

            //resConfigs "ldltr", "en"
        }
        create("uat") {
            //applicationId 'com.anetos.parkme'
            //applicationIdSuffix System.getenv("HOCKEY_BUNDLE_ID") != null ? "" : ".neoDevMob"
            //minSdkVersion 16
            //versionCode project.inCode
            //versionName project.inVersion

            buildConfigField("String", "BASE_URL", "\"https://\"")
            buildConfigField("String", "PAYMENT_URL", "\"https://\"")
            buildConfigField("Boolean", "SHOW_VERSION_TOAST", "true")
            //signingConfig signingConfigs.release
            //Release keys for one signal
            manifestPlaceholders["ENABLE_CLEARTEXT_TRAFFIC"] = "false"

            //resConfigs "ldltr", "en"
        }
    }
    buildFeatures {
        viewBinding = true
        compose = true
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.4"
    }
    lint {
        abortOnError = false
        checkReleaseBuilds = false
    }
    testOptions {
        unitTests {
            //includeAndroidResources = true
            //returnDefaultValues = true
        }
    }
}

dependencies {
    //compose
    // Material Design 3
    implementation("androidx.compose.material3:material3:1.0.1")

    // Android Studio Preview support
    implementation("androidx.compose.ui:ui-tooling-preview:1.4.1")
    debugImplementation("androidx.compose.ui:ui-tooling:1.4.1")

    // UI Tests
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.4.1")
    debugImplementation("androidx.compose.ui:ui-test-manifest:1.4.1")

    // Optional - Included automatically by material, only add when you need
    // the icons but not the material library (e.g. when using Material3 or a
    // custom design system based on Foundation)
    implementation("androidx.compose.material:material-icons-core:1.4.1")
    // Optional - Add full set of material icons
    implementation("androidx.compose.material:material-icons-extended:1.4.1")
    // Optional - Add window size utils
    implementation("androidx.compose.material3:material3-window-size-class:1.0.1")

    // Optional - Integration with activities
    implementation("androidx.activity:activity-compose:1.7.0")
    // Optional - Integration with ViewModels
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.1")
    // Optional - Integration with LiveData

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.8.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.firebase:firebase-messaging-ktx:23.1.2")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("androidx.recyclerview:recyclerview:1.3.0")
    implementation("com.google.firebase:firebase-crashlytics:18.3.6")
    implementation("com.google.firebase:firebase-analytics:21.2.1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // core
    implementation("androidx.multidex:multidex:2.0.1")

    implementation("androidx.vectordrawable:vectordrawable:1.1.0")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")

    //room Database
    implementation("androidx.room:room-runtime:2.5.1")
    kapt("androidx.room:room-compiler:2.5.1")
    // Kotlin Extensions and Coroutines support for Room
    implementation("androidx.room:room-ktx:2.5.1")

    // ViewModel
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")
    implementation("androidx.lifecycle:lifecycle-common:2.6.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")

    // Kotlin Co-routines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.1")


    implementation("com.google.code.gson:gson:2.10.1")
    // Retrofit
    implementation("com.jakewharton.retrofit:retrofit2-kotlin-coroutines-adapter:0.9.2")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:5.0.0-alpha.3")

    //google-Maps
    implementation("com.google.maps.android:android-maps-utils:3.4.0")

    implementation("com.google.android.gms:play-services-maps:18.1.0")
    implementation("com.google.android.gms:play-services-location:21.0.1")
    implementation("com.google.android.libraries.places:places:3.0.0")

    // Import the Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:31.4.0"))
    // Declare the dependency for the Firebase SDK for Google Analytics
    implementation("com.google.firebase:firebase-analytics-ktx")

    // For example, declare the dependencies for Firebase Authentication and Cloud Firestore
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")

    // FirebaseUI for Firebase Auth
    implementation("com.firebaseui:firebase-ui-auth:8.0.2")
    implementation("com.google.firebase:firebase-auth-ktx:21.2.0")

    //animation
    implementation("com.airbnb.android:lottie:6.0.0")
    implementation("jp.wasabeef:recyclerview-animators:4.0.2")

    //App update
    implementation("com.google.android.play:core-ktx:1.8.1")
    //country code
    implementation("com.hbb20:ccp:2.7.0")
    //datetime
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")
    implementation("androidx.navigation:navigation-fragment-ktx:2.5.3")
    implementation("androidx.navigation:navigation-ui-ktx:2.5.3")
    // Feature module Support
    implementation("androidx.navigation:navigation-dynamic-features-fragment:2.5.3")

    //Dagger - Hilt
    implementation("com.google.dagger:hilt-android:2.45")
    kapt("com.google.dagger:hilt-compiler:2.45")

    // For instrumentation tests
    androidTestImplementation("com.google.dagger:hilt-android-testing:2.45")
    kaptAndroidTest("com.google.dagger:hilt-compiler:2.45")

    // For local unit tests
    testImplementation("com.google.dagger:hilt-android-testing:2.45")
    kaptTest("com.google.dagger:hilt-compiler:2.45")
}
// Allow references to generated code
kapt {
    correctErrorTypes = true
}