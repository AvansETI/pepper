package com.pepper.care.core.services.mqtt

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.LifecycleService
import com.pepper.care.common.repo.AppPreferencesRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.koin.android.ext.android.inject

@FlowPreview
@ExperimentalCoroutinesApi
class PlatformMqttListenerService : LifecycleService() {

    private val appPreferences: AppPreferencesRepository by inject()

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
                callback.onMessageReceived(
                    topic,
                    encryptionHelper.decrypt(
                        message.toString(),
                        EncryptionHelper.ENCRYPTION_PASSWORD
                    )
                )
            }

            override fun deliveryComplete(token: IMqttDeliveryToken?) {
                Log.d(
                    PlatformMqttListenerService::class.java.simpleName,
                    "Delivery complete"
                )
            }
        })
    }

//    private val sharedPreferenceChangeListener: SharedPreferences.OnSharedPreferenceChangeListener =
//        SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
//            when (key) {
//                COMMON_SHARED_PREF_PUBLISH_MSG_KEY -> {
//                    val message = sharedPreferences!!.getString(
//                        COMMON_SHARED_PREF_PUBLISH_MSG_KEY,
//                        COMMON_SHARED_PREF_ERROR_STRING_VALUE
//                    )
//
//                    if (message.equals(COMMON_SHARED_PREF_ERROR_STRING_VALUE)) return@OnSharedPreferenceChangeListener
//
//                    clientHelper.publish(
//                        encryptionHelper.encrypt(
//                            message!!,
//                            EncryptionHelper.ENCRYPTION_PASSWORD
//                        ), 0
//                    )
//                }
//            }
//        }

}
