package com.byd.telemetry

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class BydBatteryData(
    val socPercent: Int = 0,
    val voltageV: Float = 0f,
    val currentA: Float = 0f,
    val powerKw: Float = 0f,
    val temperatureC: Float = 0f,
    val remainingRangeKm: Int = 0,
    val isCharging: Boolean = false
)

class BydVehicleManager(private val context: Context) {

    private val _batteryState = MutableStateFlow(BydBatteryData())
    val batteryState: StateFlow<BydBatteryData> = _batteryState

    private val bydReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent == null) return

            when (intent.action) {
                "com.byd.intent.action.BATTERY_INFO",
                "android.intent.action.BATTERY_CHANGED" -> {
                    val level = intent.getIntExtra("level", -1)
                    val scale = intent.getIntExtra("scale", -1)
                    val soc = if (level != -1 && scale != -1) (level * 100 / scale.toFloat()).toInt() 
                              else intent.getIntExtra("soc", 0)

                    val voltage = intent.getIntExtra("voltage", 0) / 1000f
                    val temperature = intent.getIntExtra("temperature", 0) / 10f
                    val current = intent.getFloatExtra("byd_current", 0f) 
                    val power = (voltage * current) / 1000f
                    val range = intent.getIntExtra("byd_range", 0)
                    val status = intent.getIntExtra("status", -1)
                    val isCharging = status == 2

                    _batteryState.value = BydBatteryData(
                        socPercent = soc,
                        voltageV = voltage,
                        currentA = current,
                        powerKw = power,
                        temperatureC = temperature,
                        remainingRangeKm = range,
                        isCharging = isCharging
                    )
                }
            }
        }
    }

    fun startListening() {
        val filter = IntentFilter().apply {
            addAction("android.intent.action.BATTERY_CHANGED")
            addAction("com.byd.intent.action.BATTERY_INFO")
            addAction("com.byd.intent.action.ENERGY_INFO")
        }
        context.registerReceiver(bydReceiver, filter)
    }

    fun stopListening() {
        try {
            context.unregisterReceiver(bydReceiver)
        } catch (_: Exception) {}
    }
}
