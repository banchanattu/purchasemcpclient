package com.client.purchasemcp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.rememberCoroutineScope
import com.chat.mcp.client.PurchaseMcpClient
import com.chat.openai.conversations.ConversationContent
import com.chat.openai.conversations.ConversationItem
import com.chat.openai.conversations.OpenAIClientConversations
import com.chat.openai.response.OpenAiResponse
import com.chat.purchasemcp.BuildConfig
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.launch



class MainActivity : AppCompatActivity() {

    private val purchaseMCPClient: PurchaseMcpClient = PurchaseMcpClient()
    private val openaiOrganizationId = BuildConfig.OPENAI_ORGANIZATION_ID
    private val projectID = BuildConfig.OPENAI_PROJECT_ID
    private val openAiChat : OpenAIClientConversations = OpenAIClientConversations(
        OPENAI_API_KEY = BuildConfig.OPENAI_API_KEY,
        PROJECT_ID = projectID,
        OPENAI_API_URL = "https://api.openai.com/v1/"
    )
    private val openAiResponse : OpenAiResponse = OpenAiResponse(
        openAiApiKey = BuildConfig.OPENAI_API_KEY,
        projectId = projectID,
        openAiApiUrl = "https://api.openai.com/v1"
    )






    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val scope = rememberCoroutineScope()
            MaterialTheme {
                MainScreen( onConnect = {
                    // Connect to the MCP server
                    scope.launch {
                        purchaseMCPClient.connect()
                    }

                }
                , onChat = {
                    // Chat with OpenAI
                    scope.launch {
                        val response = openAiChat.getModels()
                        openAiChat.createConversation()
                        // Handle the response as needed
                    }
                },
                    retrieveChat = {
                    // Retrieve chat history
                            scope.launch {
                                val response = openAiChat.retrieveConversation()
                                // Implement chat history retrieval
                            }
                    },
                    deleteChat = {
                        scope.launch {
                            val response = openAiChat.deleteConversation()
                        }
                    },
                    getItems = {
                        scope.launch {
                            val response = openAiChat.getItems()
                        }
                    },
                    createItems = {
                        scope.launch {
                            val l : List<ConversationItem> = listOf(
                                ConversationItem(
                                    type = "message",
                                    role = "user",
                                    content = ConversationContent(
                                        type = "input_text",
                                        text = "Hello"
                                    )
                                )
                            )
                            val response = openAiChat.createItems(l)
                            response.onSuccess {
                                println(it.bodyAsText())
                            }
                        }
                    },
                    onCreateResponse = {
                        scope.launch {
                            val l : List<ConversationItem> = listOf(
                                ConversationItem(
                                    type = "message",
                                    role = "user",
                                    content = ConversationContent(
                                        type = "input_text",
                                        text = "Hello"
                                    )
                                )
                            )
                            val response = openAiResponse.createResponse("How high is everest?")
                            response.onSuccess {
                                println(it.bodyAsText())
                            }
                        }
                    },
                    onResponse = {
                        scope.launch {
                            val l : List<ConversationItem> = listOf(
                                ConversationItem(
                                    type = "message",
                                    role = "user",
                                    content = ConversationContent(
                                        type = "input_text",
                                        text = "Hello"
                                    )
                                )
                            )
                            val response = openAiResponse.getResponse()
                            response.onSuccess {
                                println(it.bodyAsText())
                            }
                        }
                    }
                )
            }
        }



    }
}
