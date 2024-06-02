plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.org.jetbrains.kotlin.android)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.ksp)
    alias(libs.plugins.androidx.navigation.safeargs)
    alias(libs.plugins.dagger.hilt.android)
    alias(libs.plugins.google.gms.google.services)
    alias(libs.plugins.google.firebase.crashlytics)
}

android {
    namespace = "com.anetos.parkme"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.anetos.parkme"
        minSdk = 29
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        //vectorDrawables.useSupportLibrary = true
        vectorDrawables {
            useSupportLibrary = true
        }
        multiDexEnabled = true
    }

    signingConfigs {
        create("release") {
            storeFile = file("../keystore/PARKME")
            storePassword = "Parkme@123"
            keyAlias = "anetos"
            keyPassword = "Parkme@123"
        }
    }
    buildTypes {
        release {
            isShrinkResources = true
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            manifestPlaceholders["app_name"] = "Parkme"
        }
        debug {
            isDebuggable = true
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
            manifestPlaceholders["app_name"] = "Parkme"
        }
    }

    flavorDimensions += "version"
    productFlavors {
        create("dev") {
            buildConfigField("Boolean", "SHOW_VERSION_TOAST", "true")
            //signingConfig signingConfigs.release
            //Release keys for one signal
            manifestPlaceholders["ENABLE_CLEARTEXT_TRAFFIC"] = "false"

            //resConfigs "ldltr", "en"
        }
        create("prod") {
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
    composeCompiler {
        enableStrongSkippingMode = true

//        reportsDestination = layout.buildDirectory.dir("compose_compiler")
//        stabilityConfigurationFile = rootProject.layout.projectDirectory.file("stability_config.conf")
    }
//    lint {
//        abortOnError = false
//        checkReleaseBuilds = false
//    }
    testOptions {
        unitTests {
            //includeAndroidResources = true
            //returnDefaultValues = true
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.material3)

    //compose BOM
    implementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(platform(libs.androidx.compose.bom))

    // Material Design 3
    implementation(libs.androidx.material3)

    // Android Studio Preview support
    implementation(libs.androidx.ui.tooling.preview)
    debugImplementation(libs.androidx.ui.tooling)

    // UI Tests
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Optional - Included automatically by material, only add when you need
    // the icons but not the material library (e.g. when using Material3 or a
    // custom design system based on Foundation)
    implementation(libs.androidx.material.icons.core)
    // Optional - Add full set of material icons
    implementation(libs.androidx.material.icons.extended)
    // Optional - Add window size utils
    implementation(libs.androidx.material3.window.size)

    // Optional - Integration with activities
    implementation(libs.androidx.activity.compose)
    // Optional - Integration with ViewModels
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    // Optional - Integration with LiveData

    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.legacy.support.v4)
    implementation(libs.androidx.recyclerview)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // core
    implementation(libs.androidx.multidex)
    implementation(libs.androidx.play.core.ktx)

    implementation(libs.androidx.vectordrawable)
    implementation(libs.androidx.swiperefreshlayout)

    //room Database
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
    // Kotlin Extensions and Coroutines support for Room
    implementation(libs.androidx.room.ktx)

    // ViewModel
    implementation(libs.androidx.lifecycle.extensions)
    implementation(libs.androidx.lifecycle.common)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // Kotlin Co-routines
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)


    implementation(libs.gson)
    // Retrofit
    implementation(libs.retrofit2.kotlin.coroutines.adapter)
    implementation(libs.converter.gson)
    implementation(libs.logging.interceptor)

    //google-Maps
    implementation(libs.android.maps.utils)

    implementation(libs.play.services.maps)
    implementation(libs.play.services.location)
    implementation(libs.places)

    // Import the Firebase BoM
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.crashlytics)
    // for Google Analytics
    implementation(libs.firebase.analytics.ktx)
    // Firebase Cloud Firestore
    implementation(libs.firebase.firestore.ktx)
    // FirebaseUI for Firebase Auth
    implementation(libs.firebaseui.firebase.ui.auth)
    implementation(libs.firebase.auth.ktx)
    implementation(libs.firebase.messaging.ktx)

    //animation
    implementation(libs.lottie)
    implementation(libs.recyclerview.animators)

    //App update
    implementation(libs.core.ktx)
    //country code
    implementation(libs.ccp)
    //datetime
    implementation(libs.kotlinx.datetime)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    // Feature module Support
    implementation(libs.androidx.navigation.dynamic.features.fragment)

    //Dagger - Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // For instrumentation tests
    androidTestImplementation(libs.hilt.android.testing)

    // For local unit tests
    testImplementation(libs.hilt.android.testing)
}
// Allow references to generated code
ksp {
    arg("correctErrorTypes", "true")
}