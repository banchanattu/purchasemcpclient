package com.chat.mcp.client

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.http.headers
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.jetbrains.kotlinx.mcp.client.Client
import org.jetbrains.kotlinx.mcp.Implementation
import org.jetbrains.kotlinx.mcp.client.WebSocketClientTransport


class PurchaseMcpClient(private val baseUrl: String = "http://192.168.1.147:8080") {


    private val httpClient: HttpClient = HttpClient(CIO) {
        install(WebSockets)
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
            })
        }
        install(Logging) {
            logger = object : Logger {
                override fun log(message: String) {
                    Log.d("MCP-HTTP", message)
                }
            }
            level = LogLevel.ALL
        }
    }

    /** Define a MCP client **/
    val client = Client(
        clientInfo = Implementation(
            name = "purchase-mcp-client",
            version = "1.0.0"
        )
    )

    private var sessionId: String = ""

    /**
     * Step 1: Create a session and get session ID
     */
    private suspend fun createSession(): String {
        Log.d("MCP", "Creating session...")

        val response: HttpResponse = httpClient.post("$baseUrl/mcp") {
            // 1. Use the dedicated ContentType helper
            contentType(ContentType.Application.Json)
            // 2. Add specific headers
            header(HttpHeaders.Accept, ContentType.Application.Json.toString())
            header(HttpHeaders.Accept, ContentType.Text.EventStream.toString())
            header(HttpHeaders.AcceptEncoding, "gzip, deflate, br")
            header(HttpHeaders.UserAgent, "PurchaseMCPClient/1.0.0")
            header("connection", "keep-alive")
            setBody(McpMessageBody().rpc("initialize"))
        }
        val sessionId = response.headers["mcp-session-id"]
            ?: response.headers["Mcp-Session-Id"]
            ?: throw Exception("No session ID received from server")

        Log.d("MCP", "Session created with ID: $sessionId")
        this.sessionId = sessionId
        return sessionId
    }

    /**
     * Step 1: Create a session and get session ID
     */
    private suspend fun getToolsList(): String {
        Log.d("MCP", "Creating session...")

        val response: HttpResponse = httpClient.post("$baseUrl/mcp") {
            // 1. Use the dedicated ContentType helper
            contentType(ContentType.Application.Json)
            // 2. Add specific headers
            header(HttpHeaders.Accept, ContentType.Application.Json.toString())
            header(HttpHeaders.Accept, ContentType.Text.EventStream.toString())
            header("connection", "keep-alive")
            header(HttpHeaders.AcceptEncoding, "gzip, deflate, br")
            header(HttpHeaders.UserAgent, "PurchaseMCPClient/1.0.0")
            header("mcp-session-id", this@PurchaseMcpClient.sessionId)
            setBody(McpMessageBody().rpc("tools/list"))
        }

        return sessionId
    }

    /**
     * Step 2: Connect to WebSocket with session ID
     */
    suspend fun connect() {
        try {
            // Create session first
//            sessionId = createStreaminSession()
            sessionId = createSession()

            val s2 = getToolsList()

            // Build WebSocket URL
            val wsUrl = baseUrl.replace("http://", "ws://").replace("https://", "wss://") + "/mcp"

            // Create transport with session ID
            val transport = WebSocketClientTransport(
                client = httpClient,
                urlString = wsUrl,
            ) {

                header("mcp-session-id", this@PurchaseMcpClient.sessionId)
                header("Accept", "text/event-stream, application/json")
                header("Sec-WebSocket-Protocol", "mcp")
            }

            Log.d("MCP", "Connecting to WebSocket with session ID: $sessionId")

            // Connect the MCP client
            client.connect(transport)

            Log.d("MCP", "Successfully connected to MCP server!")
        } catch (e: Exception) {
            Log.e("MCP", "Connection failed: ${e.message}", e)
            throw e
        }
    }

    suspend fun disconnect() {
        try {
            client.close()
            httpClient.close()
            Log.d("MCP", "Disconnected from MCP server")
        } catch (e: Exception) {
            Log.e("MCP", "Error during disconnect: ${e.message}", e)
        }
    }
}
