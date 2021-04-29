package com.pepper.care.core.services.mqtt

interface MqttMessageCallbacks {
    fun onMessageReceived(topic: String?, message: String?)
}
