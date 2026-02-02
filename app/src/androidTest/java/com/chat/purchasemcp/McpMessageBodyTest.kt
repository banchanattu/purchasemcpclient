package com.chat.purchasemcp

import com.chat.mcp.client.McpMessageBody
import org.json.JSONObject
import org.json.JSONException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertThrows
import org.junit.Test

class McpMessageBodyTest {

    @Test
    fun put_and_toJsonString_containsValues() {
        val body = McpMessageBody()
        body.put("name", "alice")
        body.put("age", 30)

        val json = JSONObject(body.toJsonString())
        assertEquals("alice", json.getString("name"))
        assertEquals(30, json.getInt("age"))
    }

    @Test
    fun put_nestedJsonObject_and_get_returnsJSONObject() {
        val body = McpMessageBody()
        val nested = JSONObject()
        nested.put("inner", "value")
        body.put("data", nested)

        val result = body.get("data")
        assertNotNull(result)
        assertEquals("value", result!!.getString("inner"))
    }

    @Test
    fun get_missingKey_throwsJSONException() {
        val body = McpMessageBody()
        // accessing a missing key via JSONObject.get(...) should throw JSONException
        assertThrows(JSONException::class.java) {
            body.get("no_such_key")
        }
    }

    @Test
    fun get_nonJsonObjectKey_throwsClassCastException() {
        val body = McpMessageBody()
        body.put("simple", "text")
        // retrieving a non-JSONObject value with the current implementation will throw ClassCastException
        assertThrows(ClassCastException::class.java) {
            body.get("simple")
        }
    }

    @Test
    fun test_rpc() {
        val body = McpMessageBody()
        val rpcJson = body.rpc()
        assertNotNull(rpcJson)
    }
}