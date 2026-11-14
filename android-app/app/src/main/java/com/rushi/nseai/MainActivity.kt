
package com.rushi.nseai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.util.Locale

private const val BASE_URL =
    "https://jubilant-space-cod-7q79qpjg46w3pjvg-8000.app.github.dev"

    class MainActivity : ComponentActivity() {
            override fun onCreate(savedInstanceState: Bundle?) {
                        super.onCreate(savedInstanceState)

                                setContent {
                                                MaterialTheme {
                                                                    StockAnalysisScreen()
                                                }
                                }
            }
    }

    suspend fun fetchSignal(symbol: String, interval: String): JSONObject {
            return withContext(Dispatchers.IO) {
                        val safeSymbol = symbol.trim().uppercase(Locale.ROOT)

                                require(safeSymbol.matches(Regex("[A-Z0-9&_-]+"))) {
                                                "Enter a valid NSE stock symbol"
                                }

                                        val url = URL(
                                                        "$BASE_URL/signal/$safeSymbol?interval=$interval"
                                        )

                                                val connection = url.openConnection() as HttpURLConnection

                                                        try {
                                                                        connection.requestMethod = "GET"
                                                                                    connection.connectTimeout = 20000
                                                                                                connection.readTimeout = 60000
                                                                                                            connection.setRequestProperty(
                                                                                                                                "Accept",
                                                                                                                                                "application/json"
                                                                                                            )

                                                                                                                        val responseCode = connection.responseCode

                                                                                                                                    val stream = if (responseCode in 200..299) {
                                                                                                                                                        connection.inputStream
                                                                                                                                    } else {
                                                                                                                                                        connection.errorStream
                                                                                                                                    }

                                                                                                                                                val response = stream.bufferedReader().use {
                                                                                                                                                                    it.readText()
                                                                                                                                                }

                                                                                                                                                            if (responseCode !in 200..299) {
                                                                                                                                                                                val detail = try {
                                                                                                                                                                                                        JSONObject(response).optString(
                                                                                                                                                                                                                                    "detail",
                                                                                                                                                                                                                                                            response
                                                                                                                                                                                                        )
                                                                                                                                                                                } catch (_: Exception) {
                                                                                                                                                                                                        response
                                                                                                                                                                                }

                                                                                                                                                                                                throw Exception("API error $responseCode: $detail")
                                                                                                                                                            }

                                                                                                                                                                        JSONObject(response)
                                                        } finally {
                                                                        connection.disconnect()
                                                        }
            }
    }

    @Composable
    fun StockAnalysisScreen() {

            var symbol by remember { mutableStateOf("RELIANCE") }
                var interval by remember { mutableStateOf("5m") }
                    var signalData by remember { mutableStateOf<JSONObject?>(null) }
                        var loading by remember { mutableStateOf(false) }
                            var errorMessage by remember { mutableStateOf("") }

                                val scope = rememberCoroutineScope()

                                    fun loadSignal() {
                                                scope.launch {
                                                                loading = true
                                                                            errorMessage = ""
                                                                                        signalData = null

                                                                                                    try {
                                                                                                                        signalData = fetchSignal(symbol, interval)
                                                                                                    } catch (e: Exception) {
                                                                                                                        errorMessage = e.message ?: "Unable to connect to backend"
                                                                                                    } finally {
                                                                                                                        loading = false
                                                                                                    }
                                                }
                                    }

                                        val risk = signalData?.optJSONObject("risk_management")
                                            val indicators = signalData?.optJSONObject("technical_indicators")
                                                val scores = signalData?.optJSONObject("model_scores_percent")

                                                    val signal = signalData?.optString("signal", "HOLD") ?: "HOLD"

                                                        val signalColor = when (signal) {
                                                                    "BUY" -> Color(0xFF16834A)
                                                                            "SELL" -> Color(0xFFD13B3B)
                                                                                    else -> Color(0xFF52647A)
                                                        }

                                                            Column(
                                                                        modifier = Modifier
                                                                                    .fillMaxSize()
                                                                                                .verticalScroll(rememberScrollState())
                                                                                                            .padding(20.dp)
                                                            ) {

                                                                        Text(
                                                                                        "NSE INTRADAY AI",
                                                                                                    color = Color(0xFF52647A),
                                                                                                                fontSize = 13.sp,
                                                                                                                            fontWeight = FontWeight.Bold
                                                                        )

                                                                                Spacer(Modifier.height(8.dp))

                                                                                        Text(
                                                                                                        "Stock Analysis",
                                                                                                                    fontSize = 28.sp,
                                                                                                                                fontWeight = FontWeight.Bold,
                                                                                                                                            color = Color(0xFF142238)
                                                                                        )

                                                                                                Text(
                                                                                                                "AI-powered market analysis for NSE stocks",
                                                                                                                            color = Color(0xFF65758B)
                                                                                                )

                                                                                                        Spacer(Modifier.height(24.dp))

                                                                                                                Card(
                                                                                                                                modifier = Modifier.fillMaxWidth(),
                                                                                                                                            colors = CardDefaults.cardColors(
                                                                                                                                                                containerColor = Color.White
                                                                                                                                            )
                                                                                                                ) {
                                                                                                                                Column(Modifier.padding(18.dp)) {

                                                                                                                                                    Text("Stock symbol")

                                                                                                                                                                    OutlinedTextField(
                                                                                                                                                                                            value = symbol,
                                                                                                                                                                                                                onValueChange = {
                                                                                                                                                                                                                                            symbol = it.uppercase(Locale.ROOT)
                                                                                                                                                                                                                },
                                                                                                                                                                                                                                    modifier = Modifier.fillMaxWidth(),
                                                                                                                                                                                                                                                        singleLine = true,
                                                                                                                                                                                                                                                                            placeholder = {
                                                                                                                                                                                                                                                                                                        Text("Example: RELIANCE")
                                                                                                                                                                                                                                                                            }
                                                                                                                                                                    )

                                                                                                                                                                                    Spacer(Modifier.height(12.dp))

                                                                                                                                                                                                    Text("Timeframe")

                                                                                                                                                                                                                    Row(
                                                                                                                                                                                                                                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                                                                                                                                                                                                                    ) {
                                                                                                                                                                                                                                            FilterChip(
                                                                                                                                                                                                                                                                        selected = interval == "5m",
                                                                                                                                                                                                                                                                                                onClick = { interval = "5m" },
                                                                                                                                                                                                                                                                                                                        label = { Text("5 minutes") }
                                                                                                                                                                                                                                            )

                                                                                                                                                                                                                                                                FilterChip(
                                                                                                                                                                                                                                                                                            selected = interval == "15m",
                                                                                                                                                                                                                                                                                                                    onClick = { interval = "15m" },
                                                                                                                                                                                                                                                                                                                                            label = { Text("15 minutes") }
                                                                                                                                                                                                                                                                )
                                                                                                                                                                                                                    }

                                                                                                                                                                                                                                    Spacer(Modifier.height(12.dp))

                                                                                                                                                                                                                                                    Button(
                                                                                                                                                                                                                                                                            onClick = { loadSignal() },
                                                                                                                                                                                                                                                                                                modifier = Modifier.fillMaxWidth(),
                                                                                                                                                                                                                                                                                                                    enabled = !loading && symbol.isNotBlank()
                                                                                                                                                                                                                                                    ) {
                                                                                                                                                                                                                                                                            Text(
                                                                                                                                                                                                                                                                                                        if (loading) "Analysing..." else "Get AI Signal"
                                                                                                                                                                                                                                                                            )
                                                                                                                                                                                                                                                    }
                                                                                                                                }
                                                                                                                }

                                                                                                                        Spacer(Modifier.height(18.dp))

                                                                                                                                if (loading) {
                                                                                                                                                CircularProgressIndicator()
                                                                                                                                                            Spacer(Modifier.height(8.dp))
                                                                                                                                                                        Text("Fetching market data and generating signal...")
                                                                                                                                }

                                                                                                                                        if (errorMessage.isNotBlank()) {
                                                                                                                                                        Card(
                                                                                                                                                                            colors = CardDefaults.cardColors(
                                                                                                                                                                                                    containerColor = Color(0xFFFFE8E8)
                                                                                                                                                                            ),
                                                                                                                                                                                            modifier = Modifier.fillMaxWidth()
                                                                                                                                                        ) {
                                                                                                                                                                            Text(
                                                                                                                                                                                                    errorMessage,
                                                                                                                                                                                                                        modifier = Modifier.padding(16.dp),
                                                                                                                                                                                                                                            color = Color(0xFFB3261E)
                                                                                                                                                                            )
                                                                                                                                                        }
                                                                                                                                        }

                                                                                                                                                signalData?.let { data ->

                                                                                                                                                            Card(
                                                                                                                                                                                modifier = Modifier.fillMaxWidth(),
                                                                                                                                                                                                colors = CardDefaults.cardColors(
                                                                                                                                                                                                                        containerColor = Color(0xFFE8EFF8)
                                                                                                                                                                                                )
                                                                                                                                                            ) {
                                                                                                                                                                                Column(Modifier.padding(18.dp)) {

                                                                                                                                                                                                        Text(
                                                                                                                                                                                                                                    "AI TRADING SIGNAL",
                                                                                                                                                                                                                                                            fontWeight = FontWeight.Bold,
                                                                                                                                                                                                                                                                                    color = Color(0xFF52647A)
                                                                                                                                                                                                        )

                                                                                                                                                                                                                            Spacer(Modifier.height(12.dp))

                                                                                                                                                                                                                                                Text(
                                                                                                                                                                                                                                                                            signal,
                                                                                                                                                                                                                                                                                                    fontSize = 32.sp,
                                                                                                                                                                                                                                                                                                                            fontWeight = FontWeight.Bold,
                                                                                                                                                                                                                                                                                                                                                    color = signalColor
                                                                                                                                                                                                                                                )

                                                                                                                                                                                                                                                                    Text("Stock: ${data.optString("symbol")}")
                                                                                                                                                                                                                                                                                        Text("Prediction: ${data.optString("prediction")}")
                                                                                                                                                                                                                                                                                                            Text(
                                                                                                                                                                                                                                                                                                                                        "Model score: ${
                                                                                                                                                                                                                                                                                                                                                                        data.optDouble("confidence_percent", 0.0)
                                                                                                                                                                                                                                                                                                                                        }%"
                                                                                                                                                                                                                                                                                                            )

                                                                                                                                                                                                                                                                                                                                Text("Timeframe: $interval")
                                                                                                                                                                                                                                                                                                                                                    Text("Price: ₹${data.optDouble("price", 0.0)}")
                                                                                                                                                                                }
                                                                                                                                                            }

                                                                                                                                                                        Spacer(Modifier.height(18.dp))

                                                                                                                                                                                    Card(
                                                                                                                                                                                                        modifier = Modifier.fillMaxWidth(),
                                                                                                                                                                                                                        colors = CardDefaults.cardColors(
                                                                                                                                                                                                                                                containerColor = Color.White
                                                                                                                                                                                                                        )
                                                                                                                                                                                    ) {
                                                                                                                                                                                                        Column(Modifier.padding(18.dp)) {

                                                                                                                                                                                                                                Text(
                                                                                                                                                                                                                                                            "MODEL SCORES",
                                                                                                                                                                                                                                                                                    fontWeight = FontWeight.Bold
                                                                                                                                                                                                                                )

                                                                                                                                                                                                                                                    Text("UP: ${scores?.optDouble("UP", 0.0) ?: 0.0}%")
                                                                                                                                                                                                                                                                        Text("DOWN: ${scores?.optDouble("DOWN", 0.0) ?: 0.0}%")
                                                                                                                                                                                                                                                                                            Text("SIDEWAYS: ${scores?.optDouble("SIDEWAYS", 0.0) ?: 0.0}%")
                                                                                                                                                                                                        }
                                                                                                                                                                                    }

                                                                                                                                                                                                Spacer(Modifier.height(18.dp))

                                                                                                                                                                                                            Card(
                                                                                                                                                                                                                                modifier = Modifier.fillMaxWidth(),
                                                                                                                                                                                                                                                colors = CardDefaults.cardColors(
                                                                                                                                                                                                                                                                        containerColor = Color.White
                                                                                                                                                                                                                                                )
                                                                                                                                                                                                            ) {
                                                                                                                                                                                                                                Column(Modifier.padding(18.dp)) {

                                                                                                                                                                                                                                                        Text(
                                                                                                                                                                                                                                                                                    "RISK MANAGEMENT",
                                                                                                                                                                                                                                                                                                            fontWeight = FontWeight.Bold,
                                                                                                                                                                                                                                                                                                                                    fontSize = 18.sp
                                                                                                                                                                                                                                                        )

                                                                                                                                                                                                                                                                            Text("Entry price: ₹${risk?.optDouble("suggested_entry", 0.0) ?: 0.0}")
                                                                                                                                                                                                                                                                                                Text("Stop loss: ₹${risk?.optDouble("stop_loss", 0.0) ?: 0.0}")
                                                                                                                                                                                                                                                                                                                    Text("Target: ₹${risk?.optDouble("target_price", 0.0) ?: 0.0}")
                                                                                                                                                                                                                                                                                                                                        Text("Suggested quantity: ${risk?.optInt("suggested_quantity", 0) ?: 0}")
                                                                                                                                                                                                                                                                                                                                                            Text("Risk/reward: ${risk?.optDouble("risk_reward_ratio", 0.0) ?: 0.0}")
                                                                                                                                                                                                                                }
                                                                                                                                                                                                            }

                                                                                                                                                                                                                        Spacer(Modifier.height(18.dp))

                                                                                                                                                                                                                                    Card(
                                                                                                                                                                                                                                                        modifier = Modifier.fillMaxWidth(),
                                                                                                                                                                                                                                                                        colors = CardDefaults.cardColors(
                                                                                                                                                                                                                                                                                                containerColor = Color.White
                                                                                                                                                                                                                                                                        )
                                                                                                                                                                                                                                    ) {
                                                                                                                                                                                                                                                        Column(Modifier.padding(18.dp)) {

                                                                                                                                                                                                                                                                                Text(
                                                                                                                                                                                                                                                                                                            "TECHNICAL INDICATORS",
                                                                                                                                                                                                                                                                                                                                    fontWeight = FontWeight.Bold
                                                                                                                                                                                                                                                                                )

                                                                                                                                                                                                                                                                                                    Text("EMA 9: ${indicators?.optDouble("ema_9", 0.0)}")
                                                                                                                                                                                                                                                                                                                        Text("EMA 21: ${indicators?.optDouble("ema_21", 0.0)}")
                                                                                                                                                                                                                                                                                                                                            Text("RSI: ${indicators?.optDouble("rsi_14", 0.0)}")
                                                                                                                                                                                                                                                                                                                                                                Text("VWAP: ${indicators?.optDouble("vwap", 0.0)}")
                                                                                                                                                                                                                                                                                                                                                                                    Text("MACD histogram: ${indicators?.optDouble("macd_histogram", 0.0)}")
                                                                                                                                                                                                                                                                                                                                                                                                        Text("ATR: ${indicators?.optDouble("atr_14", 0.0)}")
                                                                                                                                                                                                                                                        }
                                                                                                                                                                                                                                    }

                                                                                                                                                                                                                                                Spacer(Modifier.height(18.dp))

                                                                                                                                                                                                                                                            Text(
                                                                                                                                                                                                                                                                                "Reasons:",
                                                                                                                                                                                                                                                                                                fontWeight = FontWeight.Bold
                                                                                                                                                                                                                                                            )

                                                                                                                                                                                                                                                                        val reasons = data.optJSONArray("reasons")

                                                                                                                                                                                                                                                                                    if (reasons != null) {
                                                                                                                                                                                                                                                                                                        for (i in 0 until reasons.length()) {
                                                                                                                                                                                                                                                                                                                                Text("• ${reasons.optString(i)}")
                                                                                                                                                                                                                                                                                                        }
                                                                                                                                                                                                                                                                                    }

                                                                                                                                                                                                                                                                                                Spacer(Modifier.height(12.dp))

                                                                                                                                                                                                                                                                                                            Text(
                                                                                                                                                                                                                                                                                                                                data.optString("note"),
                                                                                                                                                                                                                                                                                                                                                fontSize = 12.sp,
                                                                                                                                                                                                                                                                                                                                                                color = Color(0xFF65758B)
                                                                                                                                                                                                                                                                                                            )
                                                                                                                                                }

                                                                                                                                                        Spacer(Modifier.height(20.dp))

                                                                                                                                                                Text(
                                                                                                                                                                                "PAPER TRADING ONLY • No real orders",
                                                                                                                                                                                            fontWeight = FontWeight.Bold,
                                                                                                                                                                                                        fontSize = 11.sp,
                                                                                                                                                                                                                    color = Color(0xFF65758B)
                                                                                                                                                                )
                                                            }
    }
    
                                                                                                                                                                                                                                                                                                                                        }
                                                                                                                                                                                }
                                        )
                                                }
                                }
            }
    }