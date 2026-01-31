package com.client.purchasemcp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import com.chat.mcp.client.PurchaseMcpClient
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    private val purchaseMCPClient: PurchaseMcpClient = PurchaseMcpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val scope = rememberCoroutineScope()
            MaterialTheme {
                MainScreen( onConnect = {
                    // Connect to the MCP server
                    scope.launch {
                        purchaseMCPClient.connect()
                    }

                }  )
            }
        }



    }
}
