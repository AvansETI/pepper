package com.pepper.care

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.aldebaran.qi.sdk.QiContext
import com.aldebaran.qi.sdk.QiSDK
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks
import com.pepper.care.core.services.encryption.EncryptionService
import com.pepper.care.core.services.mqtt.MqttMessageCallbacks
import com.pepper.care.core.services.mqtt.PlatformMqttListenerService
import com.pepper.care.info.presentation.InfoSliderActivity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.launch
import java.lang.NullPointerException
import java.security.GeneralSecurityException
import org.koin.android.ext.android.inject
import java.lang.Exception

@FlowPreview
@ExperimentalCoroutinesApi
class MainActivity : AppCompatActivity(), RobotLifecycleCallbacks, MqttMessageCallbacks {

    private val encryptionService: EncryptionService by inject()
    private val sharedPreferences: SharedPreferences.Editor by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setup()
    }

    private fun setup() {
        registerRobotCallbacks()
        startServices()
    }

    private fun registerRobotCallbacks() {
        QiSDK.register(this, this)
    }

    private fun startServices() {
        lifecycleScope.launch {
            PlatformMqttListenerService.start(this@MainActivity, this@MainActivity)
            PlatformMqttListenerService.start(this@MainActivity, this@MainActivity)
        }
        QiSDK.register(this, this)
        initUiElements()
    }

    private fun startTimeBasedInterfaceService() {
        lifecycleScope.launch {

        }
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

    override fun onMessageReceived(topic: String?, message: String?) {
        var decrypted = ""
        try {
            decrypted = encryptionService.decrypt(message!!, "pepper")
        } catch (e: Exception) {
            e.printStackTrace()
            return
        }

        if (decrypted.contains("bot")) {
            return
        }

        Log.d(
            MainActivity::class.java.simpleName,
            "Receive message: \"$decrypted\" from topic: \"$topic\""
        )

        val message1 = "bot: " + java.util.UUID.randomUUID().toString()
        sharedPreferences.putString(KeyTypes.MQTT_PUBLISH.name, message1).commit()

        Log.d(
            MainActivity::class.java.simpleName,
            "Send message: $message1"
        )
    }
}

enum class KeyTypes(key: String) {
    MQTT_PUBLISH("MQTT_PUBLISH")
}
