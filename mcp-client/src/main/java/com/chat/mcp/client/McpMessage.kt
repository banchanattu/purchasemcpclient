package com.chat.mcp.client

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject


class McpMessage {
    // Configure JSON encoder
    private val json = Json {
        encodeDefaults = true  // Include fields with default values
        prettyPrint = false    // Compact output
    }

    // Empty object types for capabilities
    @Serializable
    data class EmptyObject(val dummy: String? = null) {
        // Hack to serialize as {}
    }

    @Serializable
    data class Sampling(
        val createMessage: JsonObject = JsonObject(emptyMap())
    )

    @Serializable
    data class Elicitation(
        val create: JsonObject = JsonObject(emptyMap())
    )

    @Serializable
    data class Requests(
        val sampling: Sampling = Sampling(),
        val elicitation: Elicitation = Elicitation()
    )

    @Serializable
    data class Tasks(
        val list: JsonObject = JsonObject(emptyMap()),
        val cancel: JsonObject = JsonObject(emptyMap()),
        val requests: Requests = Requests()
    )

    @Serializable
    data class Roots(
        val listChanged: Boolean = true  // Changed to true to match your expected output
    )

    @Serializable
    data class Capabilities(
        val sampling: JsonObject = JsonObject(emptyMap()),
        val elicitation: JsonObject = JsonObject(emptyMap()),
        val roots: Roots = Roots(),
        val tasks: Tasks = Tasks()
    )

    @Serializable
    data class Params(
        val protocolVersion: String = "2025-11-25",
        val capabilities: Capabilities = Capabilities(),
        val clientInfo: ClientInfo = ClientInfo()
    )

    @Serializable
    data class ClientInfo(
        val name: String = "Purchase-MCP-client",
        val version: String = "0.19.0"
    )

    @Serializable
    data class McpMessageRpc(
        val jsonrpc: String = "2.0",
        val method: String = "",
        val id: Int = 0,
        val params: Params = Params()
    )

    fun prepareMcpMessageRpc(
        method: String,
        progressToken: Int,
        params: Params = Params()
    ): String {
        return json.encodeToString(
            McpMessageRpc.serializer(),
            McpMessageRpc(
                jsonrpc = "2.0",
                method = method,  // USE THE PARAMETER, not hardcoded "method"
                id = progressToken,
                params = params   // INCLUDE THIS
            )
        )
    }
}