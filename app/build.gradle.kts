import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("org.jetbrains.kotlin.plugin.compose") version "2.3.0"
//    alias(libs.plugins.kotlin.compose)
}


// Function to load properties from local.properties
fun getLocalProperties(): Properties {
    val properties = Properties()
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        properties.load(FileInputStream(localPropertiesFile))
    }
    return properties
}

val properties = getLocalProperties()

android {
    namespace = "com.chat.purchasemcp"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.chat.purchasemcp"
        minSdk = 29
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField("String", "OPENAI_API_KEY", "\"${properties.getProperty("OPENAI_API_KEY")}\"")
            buildConfigField("String", "OPENAI_ORGANIZATION_ID", "\"${properties.getProperty("OPENAI_ORGANIZATION_ID")}\"")
            buildConfigField("String", "OPENAI_PROJECT_ID", "\"${properties.getProperty("OPENAI_PROJECT_ID")}\"")
            buildConfigField("String", "OPENAI_API_URL", "\"${properties.getProperty("OPENAI_API_URL")}\"")
        }

        debug {
            buildConfigField("String", "OPENAI_API_KEY", "\"${properties.getProperty("OPENAI_API_KEY")}\"")
            buildConfigField("String", "OPENAI_ORGANIZATION_ID", "\"${properties.getProperty("OPENAI_ORGANIZATION_ID")}\"")
            buildConfigField("String", "OPENAI_PROJECT_ID", "\"${properties.getProperty("OPENAI_PROJECT_ID")}\"")
            buildConfigField("String", "OPENAI_API_URL", "\"${properties.getProperty("OPENAI_API_URL")}\"")

        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        viewBinding = true
        compose = true
    }

    // ADD THIS BLOCK - Required for Compose
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"  // Compatible with Kotlin 1.9.x
    }
}

kotlin {
    jvmToolchain(11)
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)


    implementation(libs.kotlinx.mcp.sdk)

    // ADD COMPOSE DEPENDENCIES
    val composeBom = platform("androidx.compose:compose-bom:2024.12.01")
    implementation(composeBom)







    // Add these Ktor dependencies
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.client.logging)
    implementation(libs.ktor.client.cio)

    // Kotlinx serialization
    implementation(libs.kotlinx.serialization.json)

    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.activity.compose)

    // Optional but useful
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)


    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)


    testImplementation(libs.androidx.junit)
    testImplementation(libs.androidx.espresso.core)
    testImplementation(kotlin("test"))
    testImplementation(libs.kotlin.test)
    testImplementation(kotlin("test-junit"))

    testImplementation(libs.androidx.junit)
    testImplementation(kotlin("test"))
    testImplementation(libs.kotlin.test.junit)
    testImplementation(libs.junit)
//    testImplementation(libs.junit.v4132)
    testImplementation(libs.junit)

    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

}
