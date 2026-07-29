package com.byd.telemetry

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {

    private lateinit var vehicleManager: BydVehicleManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vehicleManager = BydVehicleManager(this)

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF121212)
                ) {
                    val batteryData by vehicleManager.batteryState.collectAsState()
                    BydDashboardScreen(batteryData)
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        vehicleManager.startListening()
    }

    override fun onStop() {
        super.onStop()
        vehicleManager.stopListening()
    }
}

@Composable
fun BydDashboardScreen(data: BydBatteryData) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "QUẢN LÝ PIN & NĂNG LƯỢNG BYD",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 12.dp)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Dung lượng Pin (SOC)", color = Color.Gray, fontSize = 14.sp)
                Text(
                    text = "${data.socPercent}%",
                    color = if (data.socPercent > 20) Color(0xFF00E676) else Color(0xFFFF5252),
                    fontSize = 56.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = if (data.isCharging) "⚡ Đang sạc điện..." else "Quãng đường còn lại: ~${data.remainingRangeKm} km",
                    color = Color.LightGray,
                    fontSize = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            InfoTile(title = "Điện áp Pin", value = "${data.voltageV} V", modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.width(8.dp))
            InfoTile(title = "Công suất", value = "${data.powerKw} kW", modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            InfoTile(title = "Dòng điện", value = "${data.currentA} A", modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.width(8.dp))
            InfoTile(title = "Nhiệt độ Pin", value = "${data.temperatureC} °C", modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun InfoTile(title: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color(0xFF252525)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, color = Color.Gray, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}
