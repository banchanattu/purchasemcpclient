package com.chat.openai.response

import kotlinx.serialization.Serializable
import org.json.JSONObject

@Serializable
data class ChatMessage(
    val model: String,
    val messages: String
)

fun createChatResponsePayload(message: ChatMessage): JSONObject {
    return JSONObject().apply {
        put("model", message.model)
        put("input", message.messages)
    }
}
