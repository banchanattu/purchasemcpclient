package com.chat.openai.client

import org.json.JSONArray
import org.json.JSONObject

class OpenAIConversationMessageBody {

    fun prepareMessageItem(message: String, role: String, type: String) : JSONObject {
       return  JSONObject().apply {
            put("type", type)
            put("role", role)
            put("content", message)
        }

    }

    fun getCreateConversationBody(userMessage: String, systemMessage: String, developerMessage: String) : String {
        val items = JSONArray().apply {
            put(prepareMessageItem(type = "message", role = "user", message = userMessage))
            // Uncomment these when needed:
             put(prepareMessageItem(type = "message", role = "developer", message = developerMessage))
             put(prepareMessageItem(type = "message", role = "system", message = systemMessage))
        }
      val x =  JSONObject().apply {
            put("metadata", JSONObject().apply {put("topic", "demo") } )
            put("items", items)
        }.toString()

        return x
    }
}
