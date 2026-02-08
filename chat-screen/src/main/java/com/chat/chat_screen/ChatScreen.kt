package com.chat.chat_screen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.chat.mcp.appsetting.LocalAppSettings
import com.chat.mcp.client.PurchaseMcpClient
import com.chat.mcp.client.ToolsListResponse
import com.chat.openai.response.OpenAiResponse
import kotlinx.coroutines.launch


private var mcpClient : PurchaseMcpClient = PurchaseMcpClient()

suspend fun  getToolList() : Result<ToolsListResponse> {
    return runCatching {
        if (!mcpClient.isInitialized()) {
            mcpClient.initialize()
        }
        mcpClient.getToolsAsList()
    }
}


@Composable
fun ChatScreen() {
    var userInput by remember { mutableStateOf("") }
    var chatMessages by remember { mutableStateOf(listOf<String>()) }
    var toolList : ToolsListResponse by remember { mutableStateOf(ToolsListResponse(emptyList())) }
    val openAiResponse : OpenAiResponse = OpenAiResponse(openAiApiKey = LocalAppSettings.current.OPENAI_API_KEY,
        projectId = LocalAppSettings.current.PROJECT_ID,
        openAiApiUrl = LocalAppSettings.current.OPENAI_API_URL
    )
    val scope = rememberCoroutineScope()


    LaunchedEffect(Unit) {
        getToolList().onSuccess {
            toolList = it
        }.onFailure {
            Log.e("MCP", "Error getting tool list: ${it.message}")
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Chat messages display area
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = chatMessages.joinToString("\n\n"),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Divider()

        // Input field with send button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TextField(
                value = userInput,
                onValueChange = { userInput = it },
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 48.dp),
                placeholder = { Text("Type your message...") },
                singleLine = false,
                maxLines = 3
            )

            IconButton(
                onClick = {
                    if (userInput.isNotBlank()) {
                        chatMessages = chatMessages + userInput
                        scope.launch {
                            openAiResponse.createResponse (userInput)
                        }
                        userInput = ""
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send message",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
