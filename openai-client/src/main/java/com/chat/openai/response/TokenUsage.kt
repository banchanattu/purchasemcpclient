package com.chat.openai.response

import kotlinx.serialization.Serializable
import org.json.JSONObject

@Serializable
data class TokenUsage(
    val inputTokens: Int = 0,
    val inputCachedTokens: Int = 0,
    val outputTokens: Int = 0,
    val outputReasoningTokens: Int = 0,
    val totalTokens: Int = 0
) {
    operator fun plus(other: TokenUsage): TokenUsage {
        return TokenUsage(
            inputTokens = this.inputTokens + other.inputTokens,
            inputCachedTokens = this.inputCachedTokens + other.inputCachedTokens,
            outputTokens = this.outputTokens + other.outputTokens,
            outputReasoningTokens = this.outputReasoningTokens + other.outputReasoningTokens,
            totalTokens = this.totalTokens + other.totalTokens
        )
    }
}

fun parseTokenUsageFromUsage(usage: JSONObject): TokenUsage {
    return TokenUsage(
        inputTokens = usage.optInt("input_tokens", 0),
        inputCachedTokens = usage.optJSONObject("input_tokens_details")
            ?.optInt("cached_tokens", 0) ?: 0,
        outputTokens = usage.optInt("output_tokens", 0),
        outputReasoningTokens = usage.optJSONObject("output_tokens_details")
            ?.optInt("reasoning_tokens", 0) ?: 0,
        totalTokens = usage.optInt("total_tokens", 0)
    )
}