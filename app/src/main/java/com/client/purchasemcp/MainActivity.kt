package com.client.purchasemcp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.rememberCoroutineScope
import com.chat.mcp.client.PurchaseMcpClient
import com.chat.openai.client.OpenAIClientConversations
import com.chat.purchasemcp.BuildConfig
import kotlinx.coroutines.launch



class MainActivity : AppCompatActivity() {

    private val purchaseMCPClient: PurchaseMcpClient = PurchaseMcpClient()
    private val openAiChat : OpenAIClientConversations = OpenAIClientConversations()

    private val openaiApiKey = BuildConfig.OPENAI_API_KEY
    private val openaiOrganizationId = BuildConfig.OPENAI_ORGANIZATION_ID
    private val openaiProjectId = BuildConfig.OPENAI_PROJECT_ID


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
                    }
                )
            }
        }



    }
}
