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
        versionCode = 7
        versionName = "v007"
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
            isMinifyEnabled = false
            isShrinkResources = false
            signingConfig = signingConfigs.getByName("release")
        }
        debug {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("release")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    // Pure native Android SDK - Zero external bloatware
}
