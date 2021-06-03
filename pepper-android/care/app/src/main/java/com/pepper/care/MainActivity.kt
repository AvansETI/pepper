package com.pepper.care

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
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
import com.pepper.care.common.AnimationUtil
import com.pepper.care.common.AppResult
import com.pepper.care.common.DialogUtil
import com.pepper.care.common.repo.AppPreferencesRepository
import com.pepper.care.common.usecases.GetPatientNameUseCaseUsingRepository
import com.pepper.care.core.services.mqtt.MqttMessageCallbacks
import com.pepper.care.core.services.mqtt.PlatformMqttListenerService
import com.pepper.care.core.services.robot.*
import com.pepper.care.dialog.DialogRoutes
import com.pepper.care.dialog.common.usecases.GetAvailableScreensUseCaseUsingRepository
import com.pepper.care.feedback.entities.FeedbackEntity
import com.pepper.care.info.presentation.InfoSliderActivity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import java.util.*


@ExperimentalStdlibApi
@FlowPreview
@ExperimentalCoroutinesApi
class MainActivity : RobotActivity() {

    private val getAvailableScreens: GetAvailableScreensUseCaseUsingRepository by inject()
    private val getPatientName: GetPatientNameUseCaseUsingRepository by inject()
    private val appPreferences: AppPreferencesRepository by inject()

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
            PlatformMqttListenerService.start(this@MainActivity, messageCallbacks)
        }
    }

    private fun initUiElements() {
        this.findViewById<ImageView>(R.id.info_toolbar_button).setOnClickListener {
            Log.d(MainActivity::class.simpleName, "Clicked on info button!")
            startActivity(Intent(this, InfoSliderActivity::class.java))
        }
    }

    private val messageCallbacks = object : MqttMessageCallbacks {
        override fun onMessageReceived(topic: String?, message: String?) {
            Log.d(
                MainActivity::class.simpleName,
                "Received to following message: '${message!!}' from ${topic!!}"
            )
        }
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
                        lifecycleScope.launch {
                            appPreferences.updateFeedbackSlider(feedbackNumber)
                        }
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
                this.findNavController(R.id.child_nav_host_fragment)
                    .navigate(R.id.homeFragment, null, AnimationUtil.getDefaultAnimation())
            }
            DialogRoutes.INTRO -> {
                this.findNavController(R.id.child_nav_host_fragment).navigate(
                    R.id.dialogFragment, bundleOf(
                        Pair<String, DialogRoutes>("ROUTE_TYPE", DialogRoutes.INTRO)
                    ), AnimationUtil.getDefaultAnimation()
                )
                lifecycleScope.launch {
                    appPreferences.updatePublishMessage("Hello World!")
                }
            }
            DialogRoutes.ID -> {
                this.findNavController(R.id.child_nav_host_fragment).navigate(
                    R.id.dialogFragment, bundleOf(
                        Pair<String, DialogRoutes>("ROUTE_TYPE", DialogRoutes.ID)
                    ), AnimationUtil.getDefaultAnimation()
                )
            }
            DialogRoutes.PATIENT -> {
                this.findNavController(R.id.child_nav_host_fragment).navigate(
                    R.id.dialogFragment, bundleOf(
                        Pair<String, DialogRoutes>("ROUTE_TYPE", DialogRoutes.PATIENT)
                    ), AnimationUtil.getDefaultAnimation()
                )
            }
            DialogRoutes.ORDER -> {
                this.findNavController(R.id.child_nav_host_fragment)
                    .navigate(R.id.orderFragment, null, AnimationUtil.getDefaultAnimation())
            }
            DialogRoutes.REMINDER -> {
                this.findNavController(R.id.child_nav_host_fragment).navigate(
                    R.id.dialogFragment, bundleOf(
                        Pair<String, DialogRoutes>("ROUTE_TYPE", DialogRoutes.REMINDER)
                    ), AnimationUtil.getDefaultAnimation()
                )
            }
            DialogRoutes.QUESTION -> {
                this.findNavController(R.id.child_nav_host_fragment).navigate(
                    R.id.dialogFragment, bundleOf(
                        Pair<String, DialogRoutes>("ROUTE_TYPE", DialogRoutes.QUESTION)
                    ), AnimationUtil.getDefaultAnimation()
                )
            }
            DialogRoutes.FEEDBACK -> {
                this.findNavController(R.id.child_nav_host_fragment)
                    .navigate(R.id.feedbackFragment, null, AnimationUtil.getDefaultAnimation())
            }
            DialogRoutes.GOODBYE -> {
                this.findNavController(R.id.child_nav_host_fragment).navigate(
                    R.id.dialogFragment, bundleOf(
                        Pair<String, DialogRoutes>("ROUTE_TYPE", DialogRoutes.GOODBYE)
                    ), AnimationUtil.getDefaultAnimation()
                )
            }
        }
        this.findViewById<ImageView>(R.id.info_toolbar_button).isVisible =
            route == DialogRoutes.STANDBY
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

    override fun onBackPressed() {
        Log.d(MainActivity::class.simpleName, "User tried pressing back button")
    }

    override fun onDestroy() {
        QiSDK.unregister(this@MainActivity, RobotManager.robot)
        super.onDestroy()
    }
}