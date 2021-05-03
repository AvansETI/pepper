package com.pepper.care

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.aldebaran.qi.sdk.QiContext
import com.aldebaran.qi.sdk.QiSDK
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks
import com.aldebaran.qi.sdk.design.activity.RobotActivity
import com.pepper.care.common.CommonConstants.COMMON_DEVICE_ID
import com.pepper.care.common.CommonConstants.COMMON_SHARED_PREF_LIVE_THEME_KEY
import com.pepper.care.common.usecases.GetNetworkConnectionStateUseCase
import com.pepper.care.core.services.mqtt.MqttMessageCallbacks
import com.pepper.care.core.services.mqtt.PlatformMqttClientHelper
import com.pepper.care.core.services.mqtt.PlatformMqttListenerService
import com.pepper.care.core.services.time.InterfaceTime
import com.pepper.care.core.services.time.TimeBasedInterfaceService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import com.pepper.care.info.presentation.InfoSliderActivity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.koin.android.ext.android.inject
import org.joda.time.LocalDateTime

@FlowPreview
@ExperimentalCoroutinesApi
class MainActivity : AppCompatActivity(), RobotLifecycleCallbacks, MqttMessageCallbacks {

    private val sharedPreferences: SharedPreferences by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setup()
    }

    private fun setup() {
        registerRobotCallbacks()
        registerSharedPrefences()
        startServices()
    }

    private fun registerSharedPrefences() {
        sharedPreferences.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener)
    }

    private fun registerRobotCallbacks() {
        QiSDK.register(this, this)
    }

    private fun startServices() {
        lifecycleScope.launch {
            //PlatformMqttListenerService.start(this@MainActivity, this@MainActivity)
            TimeBasedInterfaceService.start(this@MainActivity)
        }
        //QiSDK.register(this, this)
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

    // TODO find a way to change theme without affecting running services
    override fun onDestroy() {
        //QiSDK.unregister(this@MainActivity, this@MainActivity)
        //PlatformMqttListenerService.stop(this@MainActivity)
        //TimeBasedInterfaceService.stop(this@MainActivity)
        //sharedPreferences.unregisterOnSharedPreferenceChangeListener(sharedPreferenceChangeListener)
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

    /*
    NOTE:   The activity gets destroyed after changing the theme, because an theme can only be initialised at start.
            This will result in a onDestroy call in the MainActivity and services will stop.
     */

    private val sharedPreferenceChangeListener: SharedPreferences.OnSharedPreferenceChangeListener =
        SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
            when (key) {
                COMMON_SHARED_PREF_LIVE_THEME_KEY -> {
                    when (sharedPreferences.getString(COMMON_SHARED_PREF_LIVE_THEME_KEY, InterfaceTime.DAY.name)) {
                        InterfaceTime.DAY.name -> {
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                            Log.d(MainActivity::class.simpleName, "Applied Day Theme")
                        }
                        InterfaceTime.NIGHT.name -> {
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                            Log.d(MainActivity::class.simpleName, "Applied Night Theme")
                        }
                    }
                }
            }
        }
}