package com.pepper.care.core.services.mqtt

interface MqttMessageCallbacks {
    suspend fun onMessageReceived(topic: String?, message: String?)
}
