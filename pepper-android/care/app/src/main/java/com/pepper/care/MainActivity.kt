package com.pepper.care

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.bundleOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.aldebaran.qi.sdk.QiSDK
import com.aldebaran.qi.sdk.`object`.conversation.*
import com.aldebaran.qi.sdk.builder.*
import com.aldebaran.qi.sdk.design.activity.RobotActivity
import com.aldebaran.qi.sdk.design.activity.conversationstatus.SpeechBarDisplayPosition
import com.aldebaran.qi.sdk.design.activity.conversationstatus.SpeechBarDisplayStrategy
import com.example.awesomedialog.*
import com.pepper.care.common.AppResult
import com.pepper.care.common.CommonConstants
import com.pepper.care.common.CommonConstants.COMMON_SHARED_PREF_LIVE_THEME_KEY
import com.pepper.care.common.DialogUtil
import com.pepper.care.common.usecases.GetPatientNameUseCaseUsingRepository
import com.pepper.care.core.services.mqtt.MqttMessageCallbacks
import com.pepper.care.core.services.mqtt.PlatformMqttListenerService
import org.koin.android.ext.android.inject
import com.pepper.care.core.services.robot.*
import com.pepper.care.core.services.time.InterfaceTime
import com.pepper.care.dialog.DialogRoutes
import com.pepper.care.dialog.common.usecases.GetAvailableScreensUseCaseUsingRepository
import com.pepper.care.feedback.entities.FeedbackEntity
import com.pepper.care.info.presentation.InfoSliderActivity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.launch
import java.util.*

@ExperimentalStdlibApi
@FlowPreview
@ExperimentalCoroutinesApi
class MainActivity : RobotActivity(), MqttMessageCallbacks {

    private val getAvailableScreens: GetAvailableScreensUseCaseUsingRepository by inject()
    private val getPatientName: GetPatientNameUseCaseUsingRepository by inject()
    private val sharedPreferencesEditor: SharedPreferences.Editor by inject()
    private val sharedPreferences: SharedPreferences by inject()

    private val showingDialog: MutableLiveData<AlertDialog> = MutableLiveData()
    private val givenFeedbackNumber: MutableLiveData<Int> = MutableLiveData()

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
        initUiElements()
        lifecycleScope.launch {
            PlatformMqttListenerService.start(this@MainActivity, this@MainActivity)
            //TimeBasedInterfaceService.start(this@MainActivity)
        }
    }

    private fun initUiElements() {
        this.findViewById<ImageView>(R.id.back_toolbar_button).setOnClickListener {
            Log.d(MainActivity::class.simpleName, "Clicked on back button!")
            when (this.findNavController(R.id.child_nav_host_fragment).currentDestination?.id) {
                R.id.orderViewMealFragment -> {
                    this.findNavController(R.id.child_nav_host_fragment)
                        .popBackStack(R.id.orderFragment, true)
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
        Log.d(MainActivity::class.simpleName, "Received to following message: '${message!!}' from ${topic!!}")
    }

    private val actionCallback: PepperActionCallback = object : PepperActionCallback {
        override fun onRobotAction(action: PepperAction, string: String?) {
            runOnUiThread {
                when (action) {
                    PepperAction.NAVIGATE_TO -> {
                        Log.d(MainActivity::class.simpleName, "Navigate to: ${string!!}")
                        screenNavigationHandler(DialogRoutes.valueOf(string))
                    }
                    PepperAction.NAVIGATE_TO_CHOICE -> {
                        Log.d(MainActivity::class.simpleName, "Navigate choice to: ${string!!}")
                        navigateToCorrectCustomScreen(DialogRoutes.valueOf(string))
                    }
                    PepperAction.SELECT_PATIENT_ID -> {
                        val patientId = string!!
                        Log.d(MainActivity::class.simpleName, "Patient id: $patientId")

                        val fetchedName: MutableLiveData<String> =
                            MutableLiveData()

                        this@MainActivity.lifecycleScope.launch {
                            when (val result = getPatientName.invoke()) {
                                is AppResult.Success -> {
                                    fetchedName.postValue(result.successData)
                                }
                                is AppResult.Error -> result.exception.message
                            }
                        }

                        fetchedName.observeForever {
                            this@MainActivity.showingDialog.postValue(
                                DialogUtil.buildDialog(
                                    this@MainActivity,
                                    it,
                                    DialogRoutes.ID,
                                    null
                                )
                            )
                        }
                    }
                    PepperAction.SELECT_MEAL_ITEM -> {
                        val selectedMeal = string!!
                        Log.d(MainActivity::class.simpleName, "Meal selected: $selectedMeal")
                        this@MainActivity.showingDialog.postValue(
                            DialogUtil.buildDialog(
                                this@MainActivity,
                                selectedMeal,
                                DialogRoutes.ORDER,
                                null
                            )
                        )
                    }
                    PepperAction.INPUT_EXPLAIN_QUESTION -> {
                        val questionExplanation = string!!
                        Log.d(
                            MainActivity::class.simpleName,
                            "Question explained: $questionExplanation"
                        )
                        this@MainActivity.showingDialog.postValue(
                            DialogUtil.buildDialog(
                                this@MainActivity,
                                questionExplanation,
                                DialogRoutes.FEEDBACK,
                                null
                            )
                        )
                    }
                    PepperAction.SELECT_FEEDBACK_NUMBER -> {
                        val feedbackNumber = Integer.parseInt(string!!)
                        Log.d(MainActivity::class.simpleName, "Feedback number: $feedbackNumber")
                        this@MainActivity.givenFeedbackNumber.postValue(feedbackNumber)
                        sharedPreferencesEditor.putInt(CommonConstants.COMMON_SHARED_PREF_UPDATE_FEEDBACK_SLIDER, feedbackNumber).commit()
                    }
                    PepperAction.INPUT_EXPLAIN_FEEDBACK -> {
                        val givenFeedback = string!!
                        Log.d(MainActivity::class.simpleName, "Given feedback: $givenFeedback")
                        this@MainActivity.showingDialog.postValue(
                            DialogUtil.buildDialog(
                                this@MainActivity,
                                "${
                                    when {
                                        givenFeedbackNumber.value!! >= 7 -> FeedbackEntity.FeedbackMessage.GOOD.text
                                        givenFeedbackNumber.value!! < 5 -> FeedbackEntity.FeedbackMessage.BAD.text
                                        else -> FeedbackEntity.FeedbackMessage.OKAY.text
                                    }
                                }, $givenFeedback",
                                DialogRoutes.FEEDBACK,
                                null
                            )
                        )
                    }
                    PepperAction.CONFIRM_DIALOG_SELECT -> {
                        val selected = string!!
                        Log.d(MainActivity::class.simpleName, "Confirm dialog: $selected")
                        showingDialog.apply { value!!.cancel() }
                    }
                }
            }
        }
    }

    private fun screenNavigationHandler(route: DialogRoutes) {
        when (route) {
            DialogRoutes.STANDBY -> {
                this.findNavController(R.id.child_nav_host_fragment).navigate(R.id.homeFragment)
            }
            DialogRoutes.INTRO -> {
                this.findNavController(R.id.child_nav_host_fragment).navigate(
                    R.id.dialogFragment, bundleOf(
                        Pair<String, DialogRoutes>("ROUTE_TYPE", DialogRoutes.INTRO)
                    )
                )
            }
            DialogRoutes.ID -> {
                this.findNavController(R.id.child_nav_host_fragment).navigate(
                    R.id.dialogFragment, bundleOf(
                        Pair<String, DialogRoutes>("ROUTE_TYPE", DialogRoutes.ID)
                    )
                )
            }
            DialogRoutes.PATIENT -> {
                this.findNavController(R.id.child_nav_host_fragment).navigate(
                    R.id.dialogFragment, bundleOf(
                        Pair<String, DialogRoutes>("ROUTE_TYPE", DialogRoutes.PATIENT)
                    )
                )
            }
            DialogRoutes.ORDER -> {
                this.findNavController(R.id.child_nav_host_fragment).navigate(R.id.orderFragment)
            }
            DialogRoutes.REMINDER -> {
                this.findNavController(R.id.child_nav_host_fragment).navigate(
                    R.id.dialogFragment, bundleOf(
                        Pair<String, DialogRoutes>("ROUTE_TYPE", DialogRoutes.REMINDER)
                    )
                )
            }
            DialogRoutes.QUESTION -> {
                this.findNavController(R.id.child_nav_host_fragment).navigate(
                    R.id.dialogFragment, bundleOf(
                        Pair<String, DialogRoutes>("ROUTE_TYPE", DialogRoutes.QUESTION)
                    )
                )
            }
            DialogRoutes.FEEDBACK -> {
                this.findNavController(R.id.child_nav_host_fragment).navigate(R.id.feedbackFragment)
            }
            DialogRoutes.GOODBYE -> {
                this.findNavController(R.id.child_nav_host_fragment).navigate(
                    R.id.dialogFragment, bundleOf(
                        Pair<String, DialogRoutes>("ROUTE_TYPE", DialogRoutes.GOODBYE)
                    )
                )
            }
        }
    }

    private fun navigateToCorrectCustomScreen(currentScreen: DialogRoutes) {
        val fetchedAvailableScreens: MutableLiveData<IntArray> =
            MutableLiveData(intArrayOf(0, 0, 0))

        this@MainActivity.lifecycleScope.launch {
            when (val result = getAvailableScreens.invoke()) {
                is AppResult.Success -> {
                    fetchedAvailableScreens.postValue(result.successData)
                }
                is AppResult.Error -> result.exception.message
            }
        }

        fetchedAvailableScreens.observeForever {
            when (currentScreen) {
                DialogRoutes.ORDER -> {
                    when {
                        it[1] == 1 -> screenNavigationHandler(DialogRoutes.REMINDER)
                        it[2] == 1 -> screenNavigationHandler(DialogRoutes.QUESTION)
                        else -> screenNavigationHandler(DialogRoutes.FEEDBACK)
                    }
                }
                DialogRoutes.REMINDER -> {
                    when {
                        it[2] == 1 -> screenNavigationHandler(DialogRoutes.QUESTION)
                        else -> screenNavigationHandler(DialogRoutes.FEEDBACK)
                    }
                }
                else -> {
                    when {
                        it[0] == 1 -> screenNavigationHandler(DialogRoutes.ORDER)
                        it[1] == 1 -> screenNavigationHandler(DialogRoutes.REMINDER)
                        it[2] == 1 -> screenNavigationHandler(DialogRoutes.QUESTION)
                        else -> screenNavigationHandler(DialogRoutes.FEEDBACK)
                    }
                }
            }
        }
    }

    /*
    NOTE:   The activity gets destroyed after changing the theme, because an theme can only be initialised at start.
            This will result in a onDestroy call in the MainActivity and services will stop.
     */

    private val timeListener: SharedPreferences.OnSharedPreferenceChangeListener =
        SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
            when (key) {
                COMMON_SHARED_PREF_LIVE_THEME_KEY -> {
                    when (sharedPreferences.getString(
                        COMMON_SHARED_PREF_LIVE_THEME_KEY,
                        InterfaceTime.DAY.name
                    )) {
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

    override fun onDestroy() {
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(timeListener)
        QiSDK.unregister(this@MainActivity, RobotManager.robot)
        super.onDestroy()
    }
}