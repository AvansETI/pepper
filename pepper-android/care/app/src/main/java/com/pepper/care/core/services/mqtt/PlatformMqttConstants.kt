package com.pepper.care.core.services.mqtt

object PlatformMqttConstants {
    const val MQTT_DEFAULT_TOPIC: String = "pepper-zorg-ti"
    const val MQTT_DEFAULT_HOST: String = "tcp://git.jijbentzacht.nl"
    const val MQTT_DEFAULT_USERNAME: String = "drpepper"
    const val MQTT_DEFAULT_PASSWORD: String = "securityBEtheKEY"
    const val MQTT_DEFAULT_CLEAN_SESSION: Boolean = true
    const val MQTT_DEFAULT_CONNECTION_RECONNECT: Boolean = true
    const val MQTT_DEFAULT_CONNECTION_TIMEOUT: Int = 30000
    const val MQTT_DEFAULT_CONNECTION_KEEP_ALIVE_INTERVAL: Int = 1000
}