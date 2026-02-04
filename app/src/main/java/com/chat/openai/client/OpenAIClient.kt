package com.chat.openai.client

import com.chat.purchasemcp.BuildConfig
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.engine.cio.endpoint
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
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
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.json.JSONObject

private val OPENAI_API_KEY: String = BuildConfig.OPENAI_API_KEY
private val ORGANIZATION_ID: String = BuildConfig.OPENAI_ORGANIZATION_ID
private val PROJECT_ID: String = BuildConfig.OPENAI_PROJECT_ID
private val OPENAI_API_URL: String = BuildConfig.OPENAI_API_URL


class OpenAIClient(


) {


    private var conversationId: String = ""


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

        val response: HttpResponse = httpClient.get("$OPENAI_API_URL/models") {
            header("Authorization", "Bearer $OPENAI_API_KEY")
//            header("OpenAI-Organization", ORGANIZATION_ID)
            header("OpenAI-Project", PROJECT_ID)
        }

        println(response.status)
        println(response.bodyAsText())

        return response
    }


    /**
     * curl https://api.openai.com/v1/conversations \
     *   -H "Content-Type: application/json" \
     *   -H "Authorization: Bearer $OPENAI_API_KEY" \
     *   -d '{
     *     "metadata": {"topic": "demo"},
     *     "items": [
     *       {
     *         "type": "message",
     *         "role": "user",
     *         "content": "Hello!"
     *       }
     *     ]
     *   }'
     */
    suspend fun createConversation() : HttpResponse {
        val response = httpClient.post("$OPENAI_API_URL/conversations") {
            header("Authorization", "Bearer $OPENAI_API_KEY")
            header("Content-Type", "application/json" )
            setBody(OpenAIMessageBody().getCreateConversastionBody( userMessage = "Hello!",
                systemMessage = "You are a helpful assistant.",
                developerMessage = "This is a demo conversation."))
     }

        val o = JSONObject(response.bodyAsText())
        this.conversationId = o.get("id").toString()
        return response
    }


    suspend fun retrieveConversation() : HttpResponse {
        val response = httpClient.get("$OPENAI_API_URL/conversations/$conversationId") {
            header("Authorization", "Bearer $OPENAI_API_KEY")
//            header("Content-Type", "application/json" )
//            setBody(OpenAIMessageBody().getCreateConversastionBody( userMessage = "Hello!",
//                systemMessage = "You are a helpful assistant.",
//                developerMessage = "This is a demo conversation."))
        }

//        val o = JSONObject(response.bodyAsText())
//        this.conversationId = o.get("id").toString()
        return response
    }

    suspend fun deleteConversation() : HttpResponse {
        val response = httpClient.delete("$OPENAI_API_URL/conversations/$conversationId") {
            header("Authorization", "Bearer $OPENAI_API_KEY")
//            header("Content-Type", "application/json" )
//            setBody(OpenAIMessageBody().getCreateConversastionBody( userMessage = "Hello!",
//                systemMessage = "You are a helpful assistant.",
//                developerMessage = "This is a demo conversation."))
        }

//        val o = JSONObject(response.bodyAsText())
//        this.conversationId = o.get("id").toString()
        return response
    }

    suspend fun getItems() : HttpResponse {
        val response = httpClient.get("$OPENAI_API_URL/conversations/$conversationId/items") {
            header("Authorization", "Bearer $OPENAI_API_KEY")
//            header("Content-Type", "application/json" )
//            setBody(OpenAIMessageBody().getCreateConversastionBody( userMessage = "Hello!",
//                systemMessage = "You are a helpful assistant.",
//                developerMessage = "This is a demo conversation."))
        }

//        val o = JSONObject(response.bodyAsText())
//        this.conversationId = o.get("id").toString()
        return response
    }

}