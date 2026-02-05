package com.chat.openai.client

import com.chat.purchasemcp.BuildConfig
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.engine.cio.endpoint
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.ANDROID
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.json.JSONObject

/**
 * Client for managing OpenAI conversations.
 *
 * This class provides methods to create, retrieve, delete conversations
 * and manage conversation items through the OpenAI API.
 */
class OpenAIClientConversations {

    // Configuration
    private companion object {
        private val OPENAI_API_KEY: String = BuildConfig.OPENAI_API_KEY
        private val PROJECT_ID: String = BuildConfig.OPENAI_PROJECT_ID
        private val OPENAI_API_URL: String = BuildConfig.OPENAI_API_URL

        private const val CONNECT_TIMEOUT = 10_000L
        private const val REQUEST_TIMEOUT = 30_000L
    }

    // State
    private var conversationId: String = ""

    // HTTP Client with improved configuration
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

        // Default request configuration for all requests
        defaultRequest {
            header("Authorization", "Bearer $OPENAI_API_KEY")
            header("OpenAI-Project", PROJECT_ID)
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
     * Retrieves available OpenAI models.
     *
     * @return HttpResponse containing the list of available models
     * @throws Exception if the request fails
     */
    suspend fun getModels(): Result<HttpResponse> = runCatching {
        httpClient.get("$OPENAI_API_URL/models").also { response ->
            logResponse(response)
        }
    }

    /**
     * Creates a new conversation with the OpenAI API.
     *
     * @param userMessage Initial user message
     * @param systemMessage System instructions for the assistant
     * @param developerMessage Optional developer message
     * @return Result containing the HttpResponse or error
     */
    suspend fun createConversation(
        userMessage: String = "Hello!",
        systemMessage: String = "You are a helpful assistant.",
        developerMessage: String = "This is a demo conversation."
    ): Result<HttpResponse> = runCatching {
        val response = httpClient.post("$OPENAI_API_URL/conversations") {
            setBody(
                OpenAIConversationMessageBody().getCreateConversationBody(
                    userMessage = userMessage,
                    systemMessage = systemMessage,
                    developerMessage = developerMessage
                )
            )
        }

        if (response.status == HttpStatusCode.OK || response.status.value in 200..299) {
            extractConversationId(response)
        }

        response
    }

    /**
     * Retrieves the current conversation details.
     *
     * @return Result containing the HttpResponse or error
     * @throws IllegalStateException if no conversation ID is set
     */
    suspend fun retrieveConversation(): Result<HttpResponse> = runCatching {
        requireConversationId()
        httpClient.get("$OPENAI_API_URL/conversations/$conversationId") {

        }
    }

    /**
     * Deletes the current conversation.
     *
     * @return Result containing the HttpResponse or error
     * @throws IllegalStateException if no conversation ID is set
     */
    suspend fun deleteConversation(): Result<HttpResponse> = runCatching {
        requireConversationId()
        httpClient.delete("$OPENAI_API_URL/conversations/$conversationId").also {
            // Clear conversation ID after successful deletion
            if (it.status == HttpStatusCode.OK || it.status.value in 200..299) {
                conversationId = ""
            }
        }
    }

    /**
     * Retrieves items from the current conversation.
     *
     * @return Result containing the HttpResponse or error
     * @throws IllegalStateException if no conversation ID is set
     */
    suspend fun getItems(): Result<HttpResponse> = runCatching {
        requireConversationId()
        httpClient.get("$OPENAI_API_URL/conversations/$conversationId/items")
    }


    /**
     * Create items for  the current conversation.
     *
     * @return Result containing the HttpResponse or error
     * @throws IllegalStateException if no conversation ID is set
     */
    suspend fun createItems( items : List<ConversationItem>) : Result<HttpResponse> = runCatching {
        requireConversationId()
        httpClient.post("$OPENAI_API_URL/conversations/$conversationId/items") {
            contentType(ContentType.Application.Json)
            header("Accept", "application/json")
            setBody(createConversationPayload(items))
        }
    }

    /**
     * Gets the current conversation ID.
     *
     * @return Current conversation ID or empty string if not set
     */
    fun getConversationId(): String = conversationId

    /**
     * Sets the conversation ID manually (useful for resuming conversations).
     *
     * @param id The conversation ID to set
     */
    fun setConversationId(id: String) {
        conversationId = id
    }

    /**
     * Checks if a conversation is currently active.
     *
     * @return true if conversation ID is set, false otherwise
     */
    fun hasActiveConversation(): Boolean = conversationId.isNotEmpty()

    /**
     * Closes the HTTP client and releases resources.
     */
    fun close() {
        httpClient.close()
    }

    // Private helper methods

    private fun requireConversationId() {
        require(conversationId.isNotEmpty()) {
            "No active conversation. Please create a conversation first."
        }
    }

    private suspend fun extractConversationId(response: HttpResponse) {
        try {
            val jsonObject = JSONObject(response.bodyAsText())
            conversationId = jsonObject.optString("id", "")
        } catch (e: Exception) {
            // Log error but don't throw - allow caller to handle response
            println("Failed to extract conversation ID: ${e.message}")
        }
    }

    private suspend fun logResponse(response: HttpResponse) {
        println("Status: ${response.status}")
        println("Response: ${response.bodyAsText()}")
    }
}