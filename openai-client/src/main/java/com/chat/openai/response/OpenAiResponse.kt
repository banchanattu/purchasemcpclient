package com.chat.openai.response

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.engine.cio.endpoint
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.ANDROID
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.json.JSONObject

/**
 * Client for managing OpenAI conversations.
 *
 * This class provides methods to create, retrieve, delete conversations
 * and manage conversation items through the OpenAI API.
 */
class OpenAiResponse(
    private val openAiApiKey: String,
    private val projectId: String,
    private val openAiApiUrl: String
) {
    private companion object {
        private const val CONNECT_TIMEOUT = 10_000L
        private const val REQUEST_TIMEOUT = 30_000L
    }

    private var tokenUsage: TokenUsage? = null
    private var responseId: String = ""

    private val httpClient: HttpClient = HttpClient(CIO) {
        expectSuccess = false

        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
                isLenient = true
            })
        }

        install(Logging) {
            logger = Logger.ANDROID
            level = LogLevel.ALL
        }

        defaultRequest {
            header("Authorization", "Bearer $openAiApiKey")
            header("OpenAI-Project", projectId)
            contentType(ContentType.Application.Json)
        }

        engine {
            endpoint {
                connectTimeout = CONNECT_TIMEOUT
                requestTimeout = REQUEST_TIMEOUT
            }
        }
    }

    /**
     * Creates a new response in the OpenAI system.
     *
     * @param message The message content to include in the response
     * @return Result containing the HttpResponse or an exception if the request fails
     */
    suspend fun createResponse(message: String): Result<HttpResponse> {
        return runCatching {
            httpClient.post("$openAiApiUrl/responses") {
                setBody(
                    createChatResponsePayload(
                        ChatMessage(
                            model = "gpt-4.1",
                            messages = message
                        )
                    ).toString()
                )
            }
        }.onSuccess { response ->
            if (response.status.isSuccess()) {
                parseResponseMetadata(response)
            }
        }
    }

    /**
     * Gets the most recently created response.
     */
    suspend fun getResponse(): Result<HttpResponse> {
        require(responseId.isNotEmpty()) { "No response ID available. Create a response first." }
        return getResponse(responseId)
    }

    /**
     * Gets a specific response by ID.
     *
     * @param responseId The ID of the response to retrieve
     */
    suspend fun getResponse(responseId: String): Result<HttpResponse> {
        require(responseId.isNotEmpty()) { "Response ID cannot be empty" }
        return runCatching {
            httpClient.get("$openAiApiUrl/responses/$responseId")
        }
    }

    /**
     * Gets the current accumulated token usage.
     */
    fun getTokenUsage(): TokenUsage? = tokenUsage

    /**
     * Resets the token usage counter.
     */
    fun resetTokenUsage() {
        tokenUsage = null
    }

    private suspend fun parseResponseMetadata(response: HttpResponse) {
        val responseJson = JSONObject(response.bodyAsText())
        responseId = responseJson.optString("id", "")

        responseJson.optJSONObject("usage")?.let { usageJson ->
            updateTokenUsage(usageJson)
        }
    }

    private fun updateTokenUsage(usageJson: JSONObject) {
        val newUsage = parseTokenUsageFromUsage(usageJson)
        tokenUsage = tokenUsage?.let { it + newUsage } ?: newUsage
    }

    /**
     * Closes the HTTP client and releases resources.
     */
    fun close() {
        httpClient.close()
    }
}
