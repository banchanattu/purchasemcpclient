package com.chat.mcp.client

import org.json.JSONObject

class McpMessageBody {
    private val bodyJson: JSONObject = JSONObject()

    fun put(key: String, value: Any): McpMessageBody {
        bodyJson.put(key, value)
        return this
    }

    fun toJsonString(): String = bodyJson.toString()

    fun get(key: String): Any? = bodyJson.opt(key)

    // Reusable JSON builders using apply for cleaner construction
    private fun clientInfo(): JSONObject = JSONObject().apply {
        put("name", "Purchase-MCP-client")
        put("version", "0.19.0")
    }

    private fun tasks(): JSONObject = JSONObject().apply {
        put("list", JSONObject())
        put("cancel", JSONObject())
        put("requests", JSONObject().apply {
            put("sampling", JSONObject().put("createMessage", JSONObject()))
            put("elicitation", JSONObject().put("create", JSONObject()))
        })
    }

    private fun roots(): JSONObject = JSONObject().apply {
        put("listChanged", true)
    }

    private fun capabilities(): JSONObject = JSONObject().apply {
        put("sampling", JSONObject())
        put("elicitation", JSONObject())
        put("roots", roots())
        put("tasks", tasks())
    }

    private fun params(): JSONObject = JSONObject().apply {
        put("protocolVersion", "2025-11-25")
        put("capabilities", capabilities())
        put("clientInfo", clientInfo())
    }

    private fun toolsCallParams(
        progressToken: Int,
        name: String,
        arguments: List<Pair<String, String>>
    ): JSONObject = JSONObject().apply {
        put("_meta", JSONObject().put("progressToken", progressToken))
        put("name", name)
        put("arguments", JSONObject().apply {
            arguments.forEach { (key, value) ->
                put(key, value.toIntOrNull() ?: value)
            }
        })
    }

    // Overloaded rpc methods for different use cases
    fun rpc(method: String): String {
        return JSONObject().apply {
            put("jsonrpc", "2.0")
            put("method", method)
            put("id", 0)
            put("params", params())
        }.toString()
    }

    fun rpc(
        method: String,
        progressToken: Int,
        name: String,
        arguments: List<Pair<String, String>>
    ): String {
        return JSONObject().apply {
            put("jsonrpc", "2.0")
            put("id", progressToken)
            put("method", method)
            put("params", toolsCallParams(progressToken, name, arguments))
        }.toString()
    }
}
