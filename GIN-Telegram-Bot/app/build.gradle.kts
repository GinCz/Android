plugins {
    id("com.android.application")
}

android {
    namespace = "com.gincz.telegrambot"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.gincz.telegrambot"
        minSdk = 21
        targetSdk = 33
        versionCode = 6
        versionName = "v006"
    }

    signingConfigs {
        create("release") {
            val keystoreFile = file("keystore/gin_telegram_bot.keystore")
            if (keystoreFile.exists()) {
                storeFile = keystoreFile
                storePassword = "android"
                keyAlias = "androiddebugkey"
                keyPassword = "android"
                enableV1Signing = true
                enableV2Signing = true
            }
        }
    }

    packaging {
        resources {
            excludes += "/META-INF/version-control-info.textproto"
            excludes += "/META-INF/com/android/build/gradle/app-metadata.properties"
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
