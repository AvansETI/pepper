package com.pepper.care

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.aldebaran.qi.sdk.QiContext
import com.aldebaran.qi.sdk.QiSDK
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks
import com.aldebaran.qi.sdk.`object`.conversation.*
import com.aldebaran.qi.sdk.builder.*
import com.aldebaran.qi.sdk.design.activity.RobotActivity
import com.aldebaran.qi.sdk.design.activity.conversationstatus.SpeechBarDisplayPosition
import com.aldebaran.qi.sdk.design.activity.conversationstatus.SpeechBarDisplayStrategy
import com.example.awesomedialog.*
import com.pepper.care.common.CommonConstants.COMMON_MSG_NAV_STANDBY
import com.pepper.care.common.CommonConstants.COMMON_SHARED_PREF_LIVE_THEME_KEY
import com.pepper.care.core.services.mqtt.MqttMessageCallbacks
import com.pepper.care.core.services.mqtt.PlatformMqttListenerService
import com.pepper.care.core.services.robot.RobotSpeechService
import com.pepper.care.core.services.time.InterfaceTime
import com.pepper.care.info.presentation.InfoSliderActivity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

@FlowPreview
@ExperimentalCoroutinesApi
class MainActivity : RobotActivity(), RobotLifecycleCallbacks, MqttMessageCallbacks {

    private val sharedPreferences: SharedPreferences by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setup()
    }

    private fun setup() {
        registerRobotCallbacks()
        registerSharedPreferences()
        startServices()
    }

    private fun registerSharedPreferences() {
        sharedPreferences.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener)
    }

    private fun registerRobotCallbacks() {
        setSpeechBarDisplayStrategy(SpeechBarDisplayStrategy.IMMERSIVE)
        setSpeechBarDisplayPosition(SpeechBarDisplayPosition.BOTTOM)
        QiSDK.register(this@MainActivity, this@MainActivity)
    }

    private fun startServices() {
        lifecycleScope.launch {
            PlatformMqttListenerService.start(this@MainActivity, this@MainActivity)
            //TimeBasedInterfaceService.start(this@MainActivity)
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

    override fun onDestroy() {
        QiSDK.unregister(this@MainActivity, this@MainActivity)
        super.onDestroy()
    }

    override fun onMessageReceived(topic: String?, message: String?) {
        when (message) {
            COMMON_MSG_NAV_STANDBY -> {
                Log.d(MainActivity::class.simpleName, COMMON_MSG_NAV_STANDBY)
                this.findNavController(R.id.child_nav_host_fragment).navigate(R.id.homeFragment)
            }
            else -> throw IllegalStateException("Not a valid option")
        }
    }

    /*
    NOTE:   The activity gets destroyed after changing the theme, because an theme can only be initialised at start.
            This will result in a onDestroy call in the MainActivity and services will stop.
     */

    private val sharedPreferenceChangeListener: SharedPreferences.OnSharedPreferenceChangeListener =
        SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
            when (key) {
                COMMON_SHARED_PREF_LIVE_THEME_KEY -> {
                    when (sharedPreferences.getString(
                        COMMON_SHARED_PREF_LIVE_THEME_KEY,
                        InterfaceTime.DAY.name
                    )) {
                        InterfaceTime.DAY.name -> {
                            //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
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

    override fun onRobotFocusGained(qiContext: QiContext) {
        setSpeechActions(qiContext)
    }

    private fun setSpeechActions(qiContext: QiContext) {

        /* Init */
        val topic = TopicBuilder.with(qiContext).withResource(R.raw.dialog).build()
        val chatBot = QiChatbotBuilder.with(qiContext).withTopic(topic).build()
        RobotSpeechService.setChatBot(chatBot)

        val chat = ChatBuilder.with(qiContext).withChatbot(chatBot).build()
        RobotSpeechService.setChat(chat)

        /* Get the greetings dynamic concept. */
        RobotSpeechService.getDynamicConcept(chatBot)
        RobotSpeechService.addDynamicContents(listOf(Phrase("hamburger"), Phrase("pizza")))

        /* Run the Chat action asynchronously. */
        val future = chat.async().run()

        /* Callbacks */
        chat.addOnHeardListener { humanInput ->
            Log.d(MainActivity::class.simpleName, "Human: ${humanInput.text}")
        }

        chat.addOnSayingChangedListener { robotResponse ->
            if (!robotResponse.text.isNullOrBlank()) Log.d(
                MainActivity::class.simpleName,
                "Robot: ${robotResponse.text}"
            )
        }

        future.thenConsume { chatFuture ->
            if (chatFuture.hasError()) {
                Log.e(
                    MainActivity::class.simpleName,
                    "Discussion finished with error.",
                    future.error
                )
            }
        }
    }

    private fun speechNavigator(phrase: Phrase) {
        when (phrase.text) {

            else -> throw IllegalStateException("Not a valid option")
        }
    }

    override fun onRobotFocusLost() {
        RobotSpeechService.onLost()
    }

    override fun onRobotFocusRefused(reason: String) {

    }
}
