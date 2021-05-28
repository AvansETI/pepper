package com.pepper.care

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.aldebaran.qi.sdk.QiSDK
import com.aldebaran.qi.sdk.`object`.conversation.*
import com.aldebaran.qi.sdk.builder.*
import com.aldebaran.qi.sdk.design.activity.RobotActivity
import com.aldebaran.qi.sdk.design.activity.conversationstatus.SpeechBarDisplayPosition
import com.aldebaran.qi.sdk.design.activity.conversationstatus.SpeechBarDisplayStrategy
import com.example.awesomedialog.*
import com.pepper.care.core.services.mqtt.MqttMessageCallbacks
import com.pepper.care.core.services.mqtt.PlatformMqttListenerService
import com.pepper.care.core.services.robot.*
import com.pepper.care.dialog.DialogRoutes
import com.pepper.care.info.presentation.InfoSliderActivity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.launch

@FlowPreview
@ExperimentalCoroutinesApi
class MainActivity : RobotActivity(), MqttMessageCallbacks {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        RobotManager.robot = PepperRobot(actionCallback)
        setup()
    }

    private fun setup() {
        startRobotServices()
        startDeviceServices()
    }

    private fun startRobotServices() {
        setSpeechBarDisplayStrategy(SpeechBarDisplayStrategy.IMMERSIVE)
        setSpeechBarDisplayPosition(SpeechBarDisplayPosition.BOTTOM)
        QiSDK.register(this@MainActivity, RobotManager.robot)
    }

    private fun startDeviceServices() {
        lifecycleScope.launch {
            PlatformMqttListenerService.start(this@MainActivity, this@MainActivity)
        }
        initUiElements()
    }

    private fun initUiElements() {
        this.findViewById<ImageView>(R.id.back_toolbar_button).setOnClickListener {
            Log.d(MainActivity::class.simpleName, "Clicked on back button!")
            when (this.findNavController(R.id.child_nav_host_fragment).currentDestination?.id) {
                R.id.orderViewMealFragment -> {
                    this.findNavController(R.id.child_nav_host_fragment)
                        .popBackStack(R.id.orderFragment, true);
                    this.findNavController(R.id.child_nav_host_fragment)
                        .navigate(R.id.orderFragment)
                }
            }
        }
        this.findViewById<ImageView>(R.id.info_toolbar_button).setOnClickListener {
            Log.d(MainActivity::class.simpleName, "Clicked on info button!")
            startActivity(Intent(this, InfoSliderActivity::class.java))
        }
    }

    override fun onMessageReceived(topic: String?, message: String?) {
        Log.d(MainActivity::class.simpleName, message!!)
    }

    private val actionCallback: PepperActionCallback = object : PepperActionCallback {
        override fun onRobotAction(action: PepperAction) {
            runOnUiThread {
                when (action) {
                    PepperAction.MOVE_TO_INTRO -> {
                        this@MainActivity.findNavController(R.id.child_nav_host_fragment)
                            .navigate(
                                R.id.dialogFragment, bundleOf(
                                    Pair<String, DialogRoutes>("ROUTE_TYPE", DialogRoutes.INTRO)
                                )
                            )
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        QiSDK.unregister(this@MainActivity, RobotManager.robot)
        super.onDestroy()
    }
}
