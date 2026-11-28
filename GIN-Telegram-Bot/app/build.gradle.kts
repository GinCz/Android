plugins {
    id("com.android.application")
}

android {
    namespace = "com.gincz.telegrambot"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.gincz.telegrambot"
        minSdk = 21
        targetSdk = 35
        versionCode = 3
        versionName = "v003"
    }

    signingConfigs {
        create("release") {
            val keystoreFile = file("keystore/gin_telegram_bot.keystore")
            if (keystoreFile.exists()) {
                storeFile = keystoreFile
                storePassword = "android"
                keyAlias = "androiddebugkey"
                keyPassword = "android"
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
        debug {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("release")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    // Pure native Android SDK - Zero external bloatware
}
