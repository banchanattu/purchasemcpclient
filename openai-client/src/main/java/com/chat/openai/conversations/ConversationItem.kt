package com.chat.openai.conversations

import kotlinx.serialization.Serializable
import org.json.JSONArray
import org.json.JSONObject

/**
 * Represents the content of a conversation message.
 *
 * @property type The type of content (e.g., "text", "image")
 * @property text The actual text content
 */
@Serializable
data class ConversationContent(
    val type: String,
    val text: String
)

/**
 * Represents a single conversation item/message.
 *
 * @property type The type of conversation item
 * @property role The role of the message sender (e.g., "user", "assistant")
 * @property content The content of the conversation item
 */
@Serializable
data class ConversationItem(
    val type: String,
    val role: String,
    val content: ConversationContent
)

/**
 * Converts a [ConversationItem] to its JSON representation.
 *
 * @param item The conversation item to convert
 * @return JSONObject representation of the conversation item
 */
fun ConversationItem.toJson(): JSONObject {
    return JSONObject().apply {
        put("type", type)
        put("role", role)
        put("content", JSONArray().apply {
            put(JSONObject().apply {

                put("type", content.type)
                put("text", content.text)
            })
        })
    }
}

/**
 * Converts a list of [ConversationItem]s to a JSON object containing an items array.
 *
 * @param items The list of conversation items to convert
 * @return JSONObject with an "items" array containing all conversation items
 */
fun createConversationPayload(items: List<ConversationItem>): String {
    val jsonArray = JSONArray().apply {
        items.forEach { item ->
            put(item.toJson())
        }
    }

    return  JSONObject().apply {
        put("items", jsonArray)
    }.toString()
}