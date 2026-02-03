package com.chat.mcp.client

import org.json.JSONObject

class McpMessageBody {

    private val bodyJson: JSONObject = JSONObject()

    fun put(key: String, value: Any): McpMessageBody {
        bodyJson.put(key, value)
        return this
    }
    fun toJsonString(): String {
        return bodyJson.toString()
    }

    fun get(key: String): JSONObject? {
        return bodyJson.get(key) as JSONObject?
    }

    fun clientInfo() : JSONObject {
        val clientInfoJsonObject = JSONObject()
        clientInfoJsonObject.put("name", "Purchase-MCP-client")
        clientInfoJsonObject.put("version", "0.19.0")
        return clientInfoJsonObject
    }

    fun tasks(): JSONObject {
        val tasksJsonObject = JSONObject()
        tasksJsonObject.put("list", JSONObject())
        tasksJsonObject.put("cancel", JSONObject())

        val requestsJsonObject = JSONObject()
            requestsJsonObject.put("sampling", JSONObject().put("createMessage", JSONObject()))
        val elicitationJsonObject = JSONObject()
            elicitationJsonObject.put("create", JSONObject())
            requestsJsonObject.put("elicitation", elicitationJsonObject)
        tasksJsonObject.put("requests", requestsJsonObject)
        return tasksJsonObject
    }
    fun roots(): JSONObject {
        val rootJsonObject = JSONObject()
        rootJsonObject.put("listChanged", true)
        return rootJsonObject
    }

//    fun elicitation(): JSONObject {
//        val elicitationJsonObject = JSONObject()
//        return elicitationJsonObject
//    }
//    fun sampling(): JSONObject {
//        val samplingJsonObject = JSONObject()
//        return samplingJsonObject
//
//    }
    fun capabilities(): JSONObject {
        val capabilitiesJsonObject = JSONObject()
        capabilitiesJsonObject.put("sampling", JSONObject())
        capabilitiesJsonObject.put("elicitation", JSONObject())
        capabilitiesJsonObject.put("roots", roots())
        capabilitiesJsonObject.put("tasks", tasks())
        // Add capabilities as needed
        return capabilitiesJsonObject
    }

    fun params(): JSONObject {
        val paramJsonObject = JSONObject()
        paramJsonObject.put("protocolVersion", "2025-11-25")
        paramJsonObject.put("capabilities", capabilities())
        paramJsonObject.put("clientInfo", clientInfo())
        return paramJsonObject
    }

    fun toolsCallParms(progressToken: Int, name: String, arguments: List<Pair<String, Any>>): JSONObject {
        val paramsJsonObject = JSONObject()
        val metaJsonObject = JSONObject()
        metaJsonObject.put("progressToken", progressToken)
        paramsJsonObject.put("_meta", metaJsonObject)
        paramsJsonObject.put("name", name)
        val argumentsJsonObject = JSONObject()
        for (i in arguments.indices) {
            argumentsJsonObject.put( arguments[i].first, arguments[i].second)
        }
        paramsJsonObject.put("arguments", argumentsJsonObject)
//        paramsJsonObject.put("params", paramsJsonObject)
        return paramsJsonObject

//        {"progressToken":5},"name":"UpdateStore","arguments":{"id":402,"storeName":"Bobby Store","storeDesc":"Local Purchase"}}
    }
    fun rpc(method: String) : String {
        put("jsonrpc", "2.0")
        put("method", method )
        put("id", 0)
        put("params", params())

       return this.bodyJson.toString()
    }
    fun rpc(method: String, progressToken: Int, name: String, arguments: List<Pair<String, Any>>) : String {
        put("jsonrpc", "2.0")
        put("id", progressToken)
        put("method", method )
        put("params", toolsCallParms(name = name, progressToken = progressToken, arguments = arguments))
        return this.bodyJson.toString()
    }


}

private fun JSONObject.add(string: String, put: JSONObject) {}

//        {
//            "jsonrpc": "2.0",
//            "id": 0,
//            "method": "initialize",
//            "params": {
//                "protocolVersion": "2025-11-25",
//                "capabilities": {
//                    "sampling": {},
//                    "elicitation": {},
//                    "roots": { "listChanged": true },
//                    "tasks": {
//                        "list": {},
//                        "cancel": {},
//                        "requests": {
//                            "sampling": { "createMessage": {} },
//                            "elicitation": { "create": {} }
//                        }
//                    }
//                },
//                "clientInfo": {
//                    "name": "inspector-client",
//                    "version": "0.19.0"
//                }
//            }
//        }
//    """.trimIndent())