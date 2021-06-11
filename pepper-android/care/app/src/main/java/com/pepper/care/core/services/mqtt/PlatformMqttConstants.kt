package com.pepper.care.core.services.mqtt

object PlatformMqttConstants {
    const val MQTT_DEFAULT_TOPIC: String = "pepper-zorg-ti"
    const val MQTT_DEFAULT_HOST: String = "tcp://10.0.2.2:1801"
    const val MQTT_DEFAULT_USERNAME: String = "pepper"
    const val MQTT_DEFAULT_PASSWORD: String = "pepper"
    const val MQTT_DEFAULT_ENCRYPTION_ENABLED: Boolean = true
    const val MQTT_DEFAULT_ENCRYPTION_PASSWORD: String = "pepper"
    const val MQTT_DEFAULT_CLEAN_SESSION: Boolean = true
    const val MQTT_DEFAULT_CONNECTION_RECONNECT: Boolean = true
    const val MQTT_DEFAULT_CONNECTION_TIMEOUT: Int = 30000
    const val MQTT_DEFAULT_CONNECTION_KEEP_ALIVE_INTERVAL: Int = 1000
}
