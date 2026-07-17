plugins {
    alias(libs.plugins.android.application)

}

android {
    namespace = "com.bbd.tttclient"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

//    compileSdk = 37

    ndkVersion = "27.0.12077973"

    defaultConfig {
        applicationId = "com.bbd.tttclient"
        minSdk = 26
        //noinspection OldTargetApi
        multiDexKeepProguard = file("multi-dex.pro")
        targetSdk = 36 //36
        versionCode = 1
        versionName = "1.0"

        externalNativeBuild {
            cmake {
                arguments += "-DANDROID_STL=c++_shared"
                arguments += "-DANDROID_CPP_FEATURES=exceptions rtti"
                cppFlags += "-std=c++20"
                cppFlags += "-stdlib=libc++"
            }
        }

        // Указываем все архитектуры, для которых собрали Boost
        ndk {
            abiFilters += listOf(
                "arm64-v8a",    // Современные телефоны
                //"armeabi-v7a",  // Старые 32-битные телефоны
                "x86_64",       // Эмулятор
                //"x86"           // Старый эмулятор
            )
        }
    }

    buildTypes {
        release {
            optimization { enable = false }
            isMinifyEnabled = false
        }
        debug {
            isJniDebuggable = true
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
            version = "3.22.1"
        }
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.gridlayout)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.material)
}