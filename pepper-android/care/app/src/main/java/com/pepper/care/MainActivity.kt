package com.pepper.care

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
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
import com.pepper.care.info.presentation.InfoSliderActivity
import kotlinx.coroutines.launch
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.koin.android.ext.android.inject

@FlowPreview
@ExperimentalCoroutinesApi
class MainActivity : AppCompatActivity(), RobotLifecycleCallbacks, MqttMessageCallbacks {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setup()
    }

    private fun setup() {
        registerRobotCallbacks()
        startMqttService()
    }

    private fun registerRobotCallbacks() {
        QiSDK.register(this, this)
    }

    private fun startMqttService() {
        lifecycleScope.launch {
            PlatformMqttListenerService.start(this@MainActivity, this@MainActivity)
        }
        QiSDK.register(this, this)
        initUiElements()
    }

    private fun initUiElements() {
        this.findViewById<ImageView>(R.id.back_toolbar_button).setOnClickListener {
            Log.d(MainActivity::class.simpleName, "Clicked on back button!")
            when(this.findNavController(R.id.child_nav_host_fragment).currentDestination?.id){
                R.id.orderViewMealFragment -> {
                    this.findNavController(R.id.child_nav_host_fragment).popBackStack(R.id.orderFragment, true);
                    this.findNavController(R.id.child_nav_host_fragment).navigate(R.id.orderFragment)
                }
            }
        }

        this.findViewById<ImageView>(R.id.info_toolbar_button).setOnClickListener {
            Log.d(MainActivity::class.simpleName, "Clicked on info button!")
            startActivity(Intent(this, InfoSliderActivity::class.java))
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