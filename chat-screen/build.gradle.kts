import org.jetbrains.kotlin.gradle.dsl.JvmTarget
plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "com.chat.chat_screen"
    compileSdk = 36

    defaultConfig {
        minSdk = 29

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
    kotlin {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
            // If you have other args, they move here too:
            // freeCompilerArgs.add("-Xjsr305=strict")
        }
    }

    buildFeatures {
        compose = true
    }

    dependencies {
        implementation(libs.androidx.core.ktx)
        implementation(libs.androidx.appcompat)
        implementation(libs.material)
        testImplementation(libs.junit)
        androidTestImplementation(libs.androidx.junit)
        androidTestImplementation(libs.androidx.espresso.core)

        // Compose BOM (Bill of Materials) - recommended approach
        implementation(platform(libs.androidx.compose.bom))
        // Material Icons
        implementation(libs.androidx.compose.material.icons.extended)
        implementation(libs.androidx.material.icons.extended)

        // Compose Foundation
        implementation(libs.androidx.compose.foundation)

        // Other common Compose dependencies
        implementation(libs.androidx.compose.ui)
        implementation(libs.androidx.compose.material3)
        implementation(libs.androidx.compose.ui.tooling.preview)
    }
}
