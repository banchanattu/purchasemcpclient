import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)  // ADD THIS
}

android {
    namespace = "com.chat.mcp.client"
    compileSdk = 35

    defaultConfig {
        minSdk = 24
        // ADD THIS LINE - crucial for instrumented tests
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
}

dependencies {
    // Add your mcp-client specific dependencies here
    // Add these Ktor dependencies
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.client.logging)
    implementation(libs.ktor.client.cio)
    // Kotlinx serialization
//    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.junit.ktx)


    // CORRECT test dependencies
    testImplementation(libs.junit.v4132)
    androidTestImplementation(libs.androidx.junit.v115)
    androidTestImplementation(libs.androidx.runner)
    androidTestImplementation(libs.androidx.rules)
    androidTestImplementation(libs.androidx.espresso.core.v351)

    implementation(libs.kotlinx.serialization.json.v163)
}
