package com.pepper.care.core.services.mqtt

import org.eclipse.paho.client.mqttv3.MqttMessage

interface MqttMessageCallbacks {
    fun onMessageReceived(topic: String?, message: MqttMessage?)
}