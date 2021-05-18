package com.pepper.care.core.services.mqtt

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LifecycleService
import com.pepper.care.common.CommonConstants.COMMON_SHARED_PREF_PUBLISH_MSG_KEY
import com.pepper.care.core.services.encryption.EncryptionService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.koin.android.ext.android.inject
import java.lang.Exception

@FlowPreview
@ExperimentalCoroutinesApi
class PlatformMqttListenerService : LifecycleService() {

    private val sharedPreferences: SharedPreferences by inject()
    private val encryptionService: EncryptionService by inject()

    companion object {
        lateinit var clientHelper: PlatformMqttClientHelper
        lateinit var callback: MqttMessageCallbacks

        fun start(context: Context, callback: MqttMessageCallbacks) {
            val intent = Intent(context, PlatformMqttListenerService::class.java)
            this.clientHelper = PlatformMqttClientHelper(context)
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
        sharedPreferences.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener)
        setMqttCallBack()
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
                callback.onMessageReceived(topic, message.toString())
            }

            override fun deliveryComplete(token: IMqttDeliveryToken?) {
                Log.d(
                    PlatformMqttListenerService::class.java.simpleName,
                    "Delivery complete"
                )
            }
        })
    }

    private val sharedPreferenceChangeListener: SharedPreferences.OnSharedPreferenceChangeListener =
        SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
            when (key) {
                COMMON_SHARED_PREF_PUBLISH_MSG_KEY -> {
                    //                    val message = sharedPreferences!!.getString(key, "error")
                    //
                    //                    if (message.equals("error")) {
                    //                        return
                    //                    }
                    //
                    //                    var encrypted = ""
                    //                    try {
                    //                        encrypted = encryptionService.encrypt(message!!, "pepper")
                    //                    } catch (e: Exception) {
                    //                        e.printStackTrace()
                    //                        return
                    //                    }

                    clientHelper.publish(
                        sharedPreferences!!.getString(
                            COMMON_SHARED_PREF_PUBLISH_MSG_KEY, "error"
                        )!!, 0
                    )
                }
            }
        }

}
