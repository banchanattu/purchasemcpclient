package com.chat.mcp.appsetting

import androidx.compose.runtime.compositionLocalOf


data class AppSettings(
    val OPENAI_API_KEY: String = BuildConfig.OPENAI_API_KEY,
    val PROJECT_ID: String = BuildConfig.OPENAI_PROJECT_ID,
    val OPENAI_API_URL: String = "https://api.openai.com/v1/"
)

val LocalAppSettings = compositionLocalOf { AppSettings() }