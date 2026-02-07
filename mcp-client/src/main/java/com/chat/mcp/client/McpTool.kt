package com.chat.mcp.client

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.Json
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.JsonObject
import org.json.JSONObject

// Main response wrapper
@Serializable
data class ToolsListResponse(
    val tools: List<Tool>
)

// Tool definition
@Serializable
data class Tool(
    val name: String,
    val description: String? = null,
    val inputSchema: InputSchema
)

// Input schema following JSON Schema specification
@Serializable
data class InputSchema(
    val type: String, // Usually "object"
    val properties: Map<String, PropertySchema>? = null,
    val required: List<String>? = null,
    val additionalProperties: Boolean? = null
)

// Property schema for each parameter
@Serializable
data class PropertySchema(
    val type: String? = null, // "string", "number", "boolean", "array", "object", etc.
    val description: String? = null,
    val enum: List<String>? = null,
    val items: PropertySchema? = null, // For arrays
    val properties: Map<String, PropertySchema>? = null, // For nested objects
    val default: JsonElement? = null,
    val minimum: Double? = null,
    val maximum: Double? = null,
    val minLength: Int? = null,
    val maxLength: Int? = null,
    val pattern: String? = null,
    val format: String? = null
)



/**
 * Parses MCP tools/list response JSON string into ToolsListResponse
 *
 * @param jsonString The JSON string from tools/list response
 * @return ToolsListResponse containing the list of tools
 * @throws McpParseException if parsing fails
 */
fun parseToolsList(jsonString: String): ToolsListResponse {
    return try {
        json.decodeFromString<ToolsListResponse>(jsonString)
    } catch (e: SerializationException) {
        throw McpParseException("Failed to parse tools list: ${e.message}", e)
    } catch (e: IllegalArgumentException) {
        throw McpParseException("Invalid JSON format: ${e.message}", e)
    }
}

// Configure JSON parser
private val json = Json {
    ignoreUnknownKeys = true  // Ignore fields not in our data classes
    isLenient = true           // Be lenient with JSON format
    prettyPrint = false
    encodeDefaults = true
}

// Custom exception for MCP parsing errors
class McpParseException(message: String, cause: Throwable? = null) : Exception(message, cause)

/**
 * Safe version that returns null on parsing failure
 */
fun parseToolsListOrNull(jsonString: String): ToolsListResponse? {
    return try {
        json.decodeFromString<ToolsListResponse>(jsonString)
    } catch (e: Exception) {
        null
    }

}

/**
 * Version with Result type for better error handling
 */
fun parseToolsListResult(jsonString: String): ToolsListResponse {
    //return runCatching {
      return   json.decodeFromString<ToolsListResponse>(jsonString)
   // }
}