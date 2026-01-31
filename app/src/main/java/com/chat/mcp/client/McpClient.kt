package com.chat.mcp.client

import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.request.header
import org.jetbrains.kotlinx.mcp.client.Client
import org.jetbrains.kotlinx.mcp.Implementation
import org.jetbrains.kotlinx.mcp.client.WebSocketClientTransport


class PurchaseMcpClient(url: String = "ws://localhost:8080/mcp") {

    private val httpClient : HttpClient = HttpClient {
        install(WebSockets)
    }
    /** Define a transport using WebSocket **/
    private val transport : WebSocketClientTransport = WebSocketClientTransport(
        client = httpClient,
        urlString = url,
    ) {
        // You can add custom headers or configurations here if needed
    }



    /** Define a MCP client which is going to use the Transport defined above **/
    val client = Client(
        clientInfo = Implementation(
            name = "purchase-mcp-client",
            version = "1.0.0"
        )
    )


    suspend fun connect() {
        client.connect(transport)
    }



}



