package com.pepper.care.core.services.mqtt

import android.content.Context
import android.util.Log
import com.pepper.care.core.services.mqtt.PlatformMqttConstants.MQTT_DEFAULT_CLEAN_SESSION
import com.pepper.care.core.services.mqtt.PlatformMqttConstants.MQTT_DEFAULT_CONNECTION_KEEP_ALIVE_INTERVAL
import com.pepper.care.core.services.mqtt.PlatformMqttConstants.MQTT_DEFAULT_CONNECTION_RECONNECT
import com.pepper.care.core.services.mqtt.PlatformMqttConstants.MQTT_DEFAULT_CONNECTION_TIMEOUT
import com.pepper.care.core.services.mqtt.PlatformMqttConstants.MQTT_DEFAULT_HOST
import com.pepper.care.core.services.mqtt.PlatformMqttConstants.MQTT_DEFAULT_PASSWORD
import com.pepper.care.core.services.mqtt.PlatformMqttConstants.MQTT_DEFAULT_TOPIC
import com.pepper.care.core.services.mqtt.PlatformMqttConstants.MQTT_DEFAULT_USERNAME
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*

class PlatformMqttClientHelper(
    context: Context?
) {
    private val clientId: String = MqttClient.generateClientId()
    var client: MqttAndroidClient
    val serverUri = MQTT_DEFAULT_HOST

    init {
        client = MqttAndroidClient(context, serverUri, clientId)
        connect()
    }

    fun setCallback(callback: MqttCallbackExtended?) {
        client.setCallback(callback)
    }

    private fun connect() {
        try {
            client.connect(getMqttOptions(), null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken) {
                    val disconnectedBufferOptions =
                        DisconnectedBufferOptions()
                    disconnectedBufferOptions.isBufferEnabled = true
                    disconnectedBufferOptions.bufferSize = 100
                    disconnectedBufferOptions.isPersistBuffer = false
                    disconnectedBufferOptions.isDeleteOldestMessages = false
                    client.setBufferOpts(disconnectedBufferOptions)
                }

                override fun onFailure(
                    asyncActionToken: IMqttToken,
                    exception: Throwable
                ) {
                    Log.w(
                        PlatformMqttClientHelper::class.simpleName,
                        "Failed to connect to: $serverUri ; $exception"
                    )
                }
            })
        } catch (ex: MqttException) {
            ex.printStackTrace()
        }
    }

    fun subscribe(qos: Int = 0) {
        val subscriptionTopic = MQTT_DEFAULT_TOPIC
        try {
            client.subscribe(subscriptionTopic, qos, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken) {
                    Log.w(
                        PlatformMqttClientHelper::class.simpleName,
                        "Subscribed to topic '$subscriptionTopic'"
                    )
                }

                override fun onFailure(
                    asyncActionToken: IMqttToken,
                    exception: Throwable
                ) {
                    Log.w(
                        PlatformMqttClientHelper::class.simpleName,
                        "Subscription to topic '$subscriptionTopic' failed!"
                    )
                }
            })
        } catch (ex: MqttException) {
            System.err.println("Exception whilst subscribing to topic '$subscriptionTopic'")
            ex.printStackTrace()
        }
    }

    fun publish(msg: String, qos: Int = 0) {
        val publishTopic = MQTT_DEFAULT_TOPIC
        try {
            val message = MqttMessage()
            message.payload = msg.toByteArray()
            client.publish(publishTopic, message.payload, qos, false)
            Log.d(
                PlatformMqttClientHelper::class.simpleName,
                "Message published to topic `$publishTopic`: $msg"
            )
        } catch (e: MqttException) {
            Log.d(
                PlatformMqttClientHelper::class.simpleName,
                "Error Publishing to $publishTopic: " + e.message
            )
            e.printStackTrace()
        }

    }

    fun isConnected(): Boolean {
        return client.isConnected
    }

    fun destroy() {
        client.unregisterResources()
        client.disconnect()
    }

    private fun getMqttOptions() : MqttConnectOptions {
        val options = MqttConnectOptions()
        options.isAutomaticReconnect = MQTT_DEFAULT_CONNECTION_RECONNECT
        options.isCleanSession = MQTT_DEFAULT_CLEAN_SESSION
        options.userName = MQTT_DEFAULT_USERNAME
        options.password = MQTT_DEFAULT_PASSWORD.toCharArray()
        options.connectionTimeout = MQTT_DEFAULT_CONNECTION_TIMEOUT
        options.keepAliveInterval = MQTT_DEFAULT_CONNECTION_KEEP_ALIVE_INTERVAL
        return options
    }
}
