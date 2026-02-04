package com.chat.openai.client

import com.chat.purchasemcp.BuildConfig
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.engine.cio.endpoint
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.ANDROID
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

private val OPENAI_API_KEY: String = BuildConfig.OPENAI_API_KEY
private val ORGANIZATION_ID: String = BuildConfig.OPENAI_ORGANIZATION_ID
private val PROJECT_ID: String = BuildConfig.OPENAI_PROJECT_ID
private val OPENAI_API_URL: String = BuildConfig.OPENAI_API_URL


class OpenAIClient(


) {

    var httpClient: HttpClient = HttpClient(CIO) {
        expectSuccess = false
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
            })
        }

        install(Logging) {
            logger = Logger.ANDROID
            level = LogLevel.ALL
        }

        engine {
            endpoint {
                connectTimeout = 10_000
                requestTimeout = 30_000
            }
        }
    }

    /**
     * curl https://api.openai.com/v1/models \
     *   -H "Authorization: Bearer $OPENAI_API_KEY" \
     *   -H "OpenAI-Organization: $ORGANIZATION_ID" \
     *   -H "OpenAI-Project: $PROJECT_ID"
     */
    suspend fun getModels(): HttpResponse {

        val response: HttpResponse = httpClient.get(OPENAI_API_URL) {
            header("Authorization", "Bearer $OPENAI_API_KEY")
//            header("OpenAI-Organization", ORGANIZATION_ID)
            header("OpenAI-Project", PROJECT_ID)
        }

        println(response.status)
        println(response.bodyAsText())

        return response
    }

}