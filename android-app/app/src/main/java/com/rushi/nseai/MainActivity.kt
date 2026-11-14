package com.rushi.nseai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFFF3F6FA)
                ) {
                    StockAnalysisScreen()
                }
            }
        }
    }
}

@Composable
fun StockAnalysisScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "NSE INTRADAY AI",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF52647A)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Stock Analysis",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF142238)
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "AI-powered market analysis for NSE stocks",
            fontSize = 14.sp,
            color = Color(0xFF65758B)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 3.dp
            )
        ) {
            Column(
                modifier = Modifier.padding(18.dp)
            ) {
                Text(
                    text = "Selected stock",
                    fontSize = 13.sp,
                    color = Color(0xFF65758B)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "RELIANCE",
                    fontSize = 23.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF142238)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Timeframe: 5 minutes",
                    fontSize = 14.sp,
                    color = Color(0xFF34445A)
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFE5EDF8)
            )
        ) {
            Column(
                modifier = Modifier.padding(18.dp)
            ) {
                Text(
                    text = "AI TRADING SIGNAL",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF52647A)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Waiting for API connection",
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF244A78)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Your backend will provide BUY, SELL or HOLD signals in the next step.",
                    fontSize = 13.sp,
                    color = Color(0xFF52647A)
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Column(
                modifier = Modifier.padding(18.dp)
            ) {
                Text(
                    text = "Risk management",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF142238)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Entry price: --",
                    color = Color(0xFF52647A)
                )
                Text(
                    text = "Stop loss: --",
                    color = Color(0xFF52647A)
                )
                Text(
                    text = "Target price: --",
                    color = Color(0xFF52647A)
                )
                Text(
                    text = "Suggested quantity: --",
                    color = Color(0xFF52647A)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "PAPER TRADING ONLY • No real orders",
            modifier = Modifier.fillMaxWidth(),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF65758B)
        )
    }
}
