package com.pepper.care

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.aldebaran.qi.sdk.QiContext
import com.aldebaran.qi.sdk.QiSDK
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks
import com.aldebaran.qi.sdk.`object`.conversation.Chat
import com.aldebaran.qi.sdk.`object`.conversation.QiChatbot
import com.aldebaran.qi.sdk.`object`.conversation.Topic
import com.aldebaran.qi.sdk.builder.ChatBuilder
import com.aldebaran.qi.sdk.builder.QiChatbotBuilder
import com.aldebaran.qi.sdk.builder.TopicBuilder
import com.aldebaran.qi.sdk.design.activity.RobotActivity
import com.aldebaran.qi.sdk.design.activity.conversationstatus.SpeechBarDisplayPosition
import com.aldebaran.qi.sdk.design.activity.conversationstatus.SpeechBarDisplayStrategy
import com.example.awesomedialog.*
import com.pepper.care.common.CommonConstants.COMMON_MSG_NAV_MEDICATION
import com.pepper.care.common.CommonConstants.COMMON_MSG_NAV_ORDER
import com.pepper.care.common.CommonConstants.COMMON_MSG_NAV_QUESTION
import com.pepper.care.common.CommonConstants.COMMON_MSG_NAV_STANDBY
import com.pepper.care.common.CommonConstants.COMMON_SHARED_PREF_LIVE_THEME_KEY
import com.pepper.care.core.services.Qi.RobotThreadingHelper
import com.pepper.care.core.services.encryption.EncryptionService
import com.pepper.care.core.services.mqtt.MqttMessageCallbacks
import com.pepper.care.core.services.mqtt.PlatformMqttListenerService
import com.pepper.care.core.services.time.InterfaceTime
import com.pepper.care.dialog.DialogRoutes
import com.pepper.care.info.presentation.InfoSliderActivity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

@FlowPreview
@ExperimentalCoroutinesApi
class MainActivity : RobotActivity(), RobotLifecycleCallbacks, MqttMessageCallbacks {

    private val encryptionService: EncryptionService by inject()
    private val sharedPreferencesEditor: SharedPreferences.Editor by inject()
    private val sharedPreferences: SharedPreferences by inject()
    private var qiContext: QiContext? = null

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

    // TODO find a way to change theme without affecting running services
    override fun onDestroy() {
        //PlatformMqttListenerService.stop(this@MainActivity)
        //TimeBasedInterfaceService.stop(this@MainActivity)
        //sharedPreferences.unregisterOnSharedPreferenceChangeListener(sharedPreferenceChangeListener)
        QiSDK.unregister(this@MainActivity, this@MainActivity)
        super.onDestroy()
    }

    override fun onRobotFocusGained(qiContext: QiContext) {
        Log.i("MAIN", "Connected to robot brain")
        this.qiContext = qiContext
        RobotThreadingHelper.setChat(getChatBot(R.raw.nl_greet))
        startChat(RobotThreadingHelper.getChat(), Regex("(order)"))
    }

    private fun getChatBot(chatResource: Int): Chat? {
        val chat: Array<Chat?> = arrayOfNulls<Chat>(1)
        RobotThreadingHelper.runOffMainThreadSynchronous(Runnable {
            val topic: Topic = TopicBuilder.with(qiContext).withResource(chatResource).build()
            val qiChatbot: QiChatbot = QiChatbotBuilder.with(qiContext).withTopic(topic).build()
            chat[0] = ChatBuilder.with(qiContext).withChatbot(qiChatbot).build()
        })
        return chat[0]
    }

    private fun startChat(chat: Chat, exitPhraseRegex: Regex) {
        RobotThreadingHelper.setChatFuture(chat.async().run())

        RobotThreadingHelper.runOffMainThreadSynchronous(Runnable {
            chat.addOnHeardListener { heardPhrase ->

                Log.d("chat onheard", heardPhrase.text)

                val phrase = heardPhrase.text.toLowerCase()

                if (phrase.matches(exitPhraseRegex)) {
                    Log.d("chat onheard", "input matches exit regex")

                    runOnUiThread {
                        navigateHandler(COMMON_MSG_NAV_ORDER)
                    }

                } else {
                    Log.d("chat onheard", "input does not match exit regex")
                }



                Log.d("chat onheard", heardPhrase.text)
            }
        })
    }

    override fun onRobotFocusLost() {
        Log.e("MAIN", "Connection is lost cannot communicate with robot brain")
    }

    override fun onRobotFocusRefused(reason: String) {
        Log.e("MAIN", reason)
    }

    override fun onMessageReceived(topic: String?, message: String?) {
        //        var decrypted = ""
//        try {
//            decrypted = encryptionService.decrypt(message!!, "pepper")
//        } catch (e: Exception) {
//            e.printStackTrace()
//            return
//        }
//
//        if (decrypted.contains("bot")) {
//            return
//        }
//
//        Log.d(
//            MainActivity::class.java.simpleName,
//            "Receive message: \"$decrypted\" from topic: \"$topic\""
//        )
//
//        val message1 = "bot: " + java.util.UUID.randomUUID().toString()
//        sharedPreferencesEditor.putString(CommonConstants.COMMON_SHARED_PREF_PUBLISH_MSG_KEY, message1).commit()
        navigateHandler(message)
    }

    private fun navigateHandler(message: String?) {
        when(message){
            COMMON_MSG_NAV_STANDBY-> {
                Log.d(MainActivity::class.simpleName, COMMON_MSG_NAV_STANDBY)
                this.findNavController(R.id.child_nav_host_fragment).navigate(R.id.homeFragment)
            }
            COMMON_MSG_NAV_ORDER-> {
                Log.d(MainActivity::class.simpleName, COMMON_MSG_NAV_ORDER)
                this.findNavController(R.id.child_nav_host_fragment).navigate(R.id.orderFragment)
            }
            COMMON_MSG_NAV_MEDICATION-> {
                Log.d(MainActivity::class.simpleName, COMMON_MSG_NAV_MEDICATION)
                this.findNavController(R.id.child_nav_host_fragment).navigate(
                    R.id.dialogFragment, bundleOf(
                        Pair<String, DialogRoutes>("ROUTE_TYPE", DialogRoutes.MEDICATION)
                    )
                )
            }
            COMMON_MSG_NAV_QUESTION-> {
                Log.d(MainActivity::class.simpleName, COMMON_MSG_NAV_QUESTION)
                this.findNavController(R.id.child_nav_host_fragment).navigate(
                    R.id.dialogFragment, bundleOf(
                        Pair<String, DialogRoutes>("ROUTE_TYPE", DialogRoutes.QUESTION)
                    )
                )
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
                    when (sharedPreferences.getString(COMMON_SHARED_PREF_LIVE_THEME_KEY, InterfaceTime.DAY.name)) {
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
}
