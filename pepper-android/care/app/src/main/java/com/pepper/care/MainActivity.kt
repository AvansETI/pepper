package com.pepper.care

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.aldebaran.qi.sdk.QiContext
import com.aldebaran.qi.sdk.QiSDK
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks
import com.aldebaran.qi.sdk.design.activity.RobotActivity
import com.pepper.care.common.CommonConstants.COMMON_DEVICE_ID
import com.pepper.care.common.usecases.GetNetworkConnectionStateUseCase
import com.pepper.care.core.services.mqtt.MqttMessageCallbacks
import com.pepper.care.core.services.mqtt.PlatformMqttClientHelper
import com.pepper.care.core.services.mqtt.PlatformMqttListenerService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.launch
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.koin.android.ext.android.inject

@FlowPreview
@ExperimentalCoroutinesApi
class MainActivity : AppCompatActivity(), RobotLifecycleCallbacks, MqttMessageCallbacks {

    private val getNetworkConnectionStateUseCase: GetNetworkConnectionStateUseCase by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Check for connection with platform
        val connectionResult: MutableLiveData<GetNetworkConnectionStateUseCase.ConnectionState> = MutableLiveData()

        lifecycleScope.launch {
            getNetworkConnectionStateUseCase.invoke(connectionResult, COMMON_DEVICE_ID)
        }

        connectionResult.observe(this, Observer { result ->
            Log.d(MainActivity::class.simpleName, result.toString())
            when (result) {
                GetNetworkConnectionStateUseCase.ConnectionState.NO_INTERNET_CONNECTION -> setup()
                GetNetworkConnectionStateUseCase.ConnectionState.CONNECTION_VERIFIED -> setup()
                else -> Log.d("MAIN", "Starting up")
            }
        })
    }

    private fun setup() {
        registerRobotCallbacks()
        startMqttService()
    }

    private fun registerRobotCallbacks() {
        // Register the RobotLifecycleCallbacks to this Activity.
        QiSDK.register(this, this)
    }

    private fun startMqttService() {
        // Start Mqtt service and register callbacks
        lifecycleScope.launch {
            PlatformMqttListenerService.start(this@MainActivity, this@MainActivity)
        }
    }

    override fun onDestroy() {
        // Unregister the RobotLifecycleCallbacks for this Activity.
        QiSDK.unregister(this@MainActivity, this@MainActivity)
        PlatformMqttListenerService.stop(this@MainActivity)
        super.onDestroy()
    }

    override fun onRobotFocusGained(qiContext: QiContext) {
        // The robot focus is gained.
    }

    override fun onRobotFocusLost() {
        // The robot focus is lost.
    }

    override fun onRobotFocusRefused(reason: String) {
        // The robot focus is refused.
    }

    override fun onMessageReceived(topic: String?, message: MqttMessage?) {
        // Handle messaged.
    }
}