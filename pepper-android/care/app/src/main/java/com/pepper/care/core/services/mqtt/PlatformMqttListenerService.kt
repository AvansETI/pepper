package com.pepper.care.core.services.mqtt

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import com.pepper.care.common.repo.AppPreferencesRepository
import com.pepper.care.feedback.presentation.viewmodels.FeedbackViewModelUsingUsecases
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.launch
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.koin.android.ext.android.inject

@FlowPreview
@ExperimentalCoroutinesApi
class PlatformMqttListenerService : LifecycleService() {

    private val appPreferences: AppPreferencesRepository by inject()
    private val messagingHelper: MessagingHelper by inject()

    companion object {
        lateinit var clientHelper: PlatformMqttClientHelper
        lateinit var encryptionHelper: EncryptionHelper
        lateinit var callback: MqttMessageCallbacks

        fun start(context: Context, callback: MqttMessageCallbacks) {
            val intent = Intent(context, PlatformMqttListenerService::class.java)
            this.clientHelper = PlatformMqttClientHelper(context)
            this.encryptionHelper = EncryptionHelper()
            this.callback = callback
            context.startService(intent)
        }

        fun stop(context: Context) {
            val intent = Intent(context, PlatformMqttListenerService::class.java)
            context.stopService(intent)
        }
    }

    override fun onCreate() {
        super.onCreate()
        setMqttCallBack()
        appPreferences.publishMessageFlow.asLiveData().observeForever {
            val message = if (PlatformMqttConstants.MQTT_DEFAULT_ENCRYPTION_ENABLED) {
                encryptionHelper.encrypt(it!!, PlatformMqttConstants.MQTT_DEFAULT_ENCRYPTION_PASSWORD)
            } else {
                it!!
            }

            clientHelper.publish(message, 0)
        }
    }

    private fun setMqttCallBack() {
        clientHelper.setCallback(object : MqttCallbackExtended {
            override fun connectComplete(b: Boolean, s: String) {
                Log.d(
                    PlatformMqttListenerService::class.java.simpleName,
                    "Connection complete"
                )
                clientHelper.subscribe(0)
            }

            override fun connectionLost(cause: Throwable?) {
                Log.d(
                    PlatformMqttListenerService::class.java.simpleName,
                    "Connection lost ${cause.toString()}"
                )
            }

            override fun messageArrived(topic: String?, message: MqttMessage?) {
                val arrived = if (PlatformMqttConstants.MQTT_DEFAULT_ENCRYPTION_ENABLED) {
                    encryptionHelper.decrypt(message.toString(), PlatformMqttConstants.MQTT_DEFAULT_ENCRYPTION_PASSWORD)
                } else {
                    message.toString()
                }

                lifecycleScope.launch {
                    messagingHelper.onMessageReceived(
                        topic,
                        arrived
                    )
                }

            }

            override fun deliveryComplete(token: IMqttDeliveryToken?) {
                Log.d(
                    PlatformMqttListenerService::class.java.simpleName,
                    "Delivery complete"
                )
            }
        })
    }
}
