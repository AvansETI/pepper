package com.pepper.care.core.services.mqtt

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LifecycleService
import com.pepper.care.KeyTypes
import com.pepper.care.common.ClickCallback
import com.pepper.care.common.entities.RecyclerAdapterItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.koin.android.ext.android.inject

@FlowPreview
@ExperimentalCoroutinesApi
class PlatformMqttListenerService : LifecycleService() {

    val sharedPreferences: SharedPreferences by inject()

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
        object : SharedPreferences.OnSharedPreferenceChangeListener {
            override fun onSharedPreferenceChanged(
                sharedPreferences: SharedPreferences?,
                key: String?
            ) {
                when (key){
                    KeyTypes.TEST_KEY.name -> {
                        clientHelper.publish(sharedPreferences!!.getString(key, "Fout")!!,0)
                    }
                }
            }
        }
}
