package com.pepper.care

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.aldebaran.qi.sdk.QiSDK
import com.aldebaran.qi.sdk.`object`.conversation.*
import com.aldebaran.qi.sdk.builder.*
import com.aldebaran.qi.sdk.design.activity.RobotActivity
import com.aldebaran.qi.sdk.design.activity.conversationstatus.SpeechBarDisplayPosition
import com.aldebaran.qi.sdk.design.activity.conversationstatus.SpeechBarDisplayStrategy
import com.example.awesomedialog.*
import com.pepper.care.common.utility.AnimationUtil
import com.pepper.care.common.repo.AppPreferencesRepository
import com.pepper.care.common.usecases.GetPatientNameUseCaseUsingRepository
import com.pepper.care.common.utility.DialogCallback
import com.pepper.care.common.utility.DialogUtil
import com.pepper.care.core.services.mqtt.MqttMessageCallbacks
import com.pepper.care.core.services.mqtt.PlatformMqttListenerService
import com.pepper.care.core.services.robot.*
import com.pepper.care.dialog.DialogRoutes
import com.pepper.care.dialog.common.usecases.AddPatientQuestionExplanationUseCaseUsingRepository
import com.pepper.care.feedback.common.usecases.AddPatientGivenHealthFeedbackUseCaseUsingRepository
import com.pepper.care.feedback.common.usecases.AddPatientHealthFeedbackUseCaseUsingRepository
import com.pepper.care.feedback.entities.FeedbackEntity
import com.pepper.care.info.presentation.InfoSliderActivity
import com.pepper.care.order.common.usecases.AddPatientFoodChoiceUseCaseUsingRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import java.util.*


@ExperimentalStdlibApi
@FlowPreview
@ExperimentalCoroutinesApi
class MainActivity : RobotActivity() {

    private val sendMealChoice: AddPatientFoodChoiceUseCaseUsingRepository by inject()
    private val sendFeedbackState: AddPatientHealthFeedbackUseCaseUsingRepository by inject()
    private val sendFeedbackAnswer: AddPatientGivenHealthFeedbackUseCaseUsingRepository by inject()
    private val sendQuestionAnswer: AddPatientQuestionExplanationUseCaseUsingRepository by inject()
    private val getPatientName: GetPatientNameUseCaseUsingRepository by inject()
    private val appPreferences: AppPreferencesRepository by inject()

    private val showingDialog: MutableLiveData<AlertDialog> = MutableLiveData()

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

            if (!message.contains("move")) return
            val stringArray = message.split(":").toTypedArray()

            when (stringArray[1]) {
                "save" -> {
                    RobotManager.saveCurrentLocation(stringArray[2])
                }
                "goto" -> {
                    RobotManager.moveToLocation(stringArray[2])
                }
            }
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
                    PepperAction.SELECT_PATIENT_BIRTHDAY -> {
                        val patientBday = string!!
                        Log.d(MainActivity::class.simpleName, "Patient birthday: $patientBday")
                    }
                    PepperAction.SELECT_PATIENT_NAME -> {
                        val patientName = string!!
                        Log.d(MainActivity::class.simpleName, "Patient name: $patientName")

                        this@MainActivity.lifecycleScope.launch {
                            getPatientName.invoke().asLiveData().observeForever {
                                this@MainActivity.showingDialog.postValue(
                                    DialogUtil.buildDialog(
                                        this@MainActivity,
                                        it,
                                        DialogRoutes.IDNAME,
                                        null
                                    )
                                )
                            }
                        }
                    }
                    PepperAction.SELECT_MEAL_ITEM -> {
                        val selectedMeal = string!!
                        Log.d(MainActivity::class.simpleName, "Meal selected: $selectedMeal")

                        val dialog = DialogUtil.buildDialog(
                            this@MainActivity,
                            selectedMeal,
                            DialogRoutes.ORDER,
                            null
                        )

                        dialog.setOnCancelListener {
                            lifecycleScope.launch {
                                sendMealChoice.invoke(selectedMeal)
                            }
                        }

                        this@MainActivity.showingDialog.postValue(dialog)
                    }
                    PepperAction.INPUT_EXPLAIN_QUESTION -> {
                        val questionExplanation = string!!
                        Log.d(
                            MainActivity::class.simpleName,
                            "Question explained: $questionExplanation"
                        )

                        val dialog = DialogUtil.buildDialog(
                            this@MainActivity,
                            questionExplanation,
                            DialogRoutes.FEEDBACK,
                            null
                        )

                        dialog.setOnCancelListener {
                            lifecycleScope.launch {
                                sendQuestionAnswer.invoke(questionExplanation)
                            }
                        }

                        this@MainActivity.showingDialog.postValue(dialog)
                    }
                    PepperAction.SELECT_FEEDBACK_NUMBER -> {
                        val feedbackNumber = Integer.parseInt(string!!)
                        Log.d(MainActivity::class.simpleName, "Feedback number: $feedbackNumber")
                        lifecycleScope.launch {
                            appPreferences.updateFeedbackSlider(feedbackNumber)
                        }
                    }
                    PepperAction.INPUT_EXPLAIN_FEEDBACK -> {
                        val givenFeedback = string!!
                        Log.d(MainActivity::class.simpleName, "Given feedback: $givenFeedback")

                        var feedbackNumber = 8
                        appPreferences.feedbackSliderFlow.asLiveData().observeForever {
                            feedbackNumber = it
                        }

                        val message = when {
                            feedbackNumber >= 7 -> FeedbackEntity.FeedbackMessage.GOOD
                            feedbackNumber < 5 -> FeedbackEntity.FeedbackMessage.BAD
                            else -> FeedbackEntity.FeedbackMessage.OKAY
                        }

                        val dialog = DialogUtil.buildDialog(
                            this@MainActivity,
                            "${message.text}, $givenFeedback",
                            DialogRoutes.FEEDBACK,
                            null
                        )

                        dialog.setOnCancelListener {
                            lifecycleScope.launch {
                                sendFeedbackState.invoke(message)
                                sendFeedbackAnswer.invoke(givenFeedback)
                            }
                        }

                        this@MainActivity.showingDialog.postValue(dialog)
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
            }
            DialogRoutes.ACCESS -> {
                this.findNavController(R.id.child_nav_host_fragment).navigate(
                    R.id.dialogFragment, bundleOf(
                        Pair<String, DialogRoutes>("ROUTE_TYPE", DialogRoutes.ACCESS)
                    ), AnimationUtil.getDefaultAnimation()
                )
            }
            DialogRoutes.IDBDAY -> {
                this.findNavController(R.id.child_nav_host_fragment).navigate(
                    R.id.dialogFragment, bundleOf(
                        Pair<String, DialogRoutes>("ROUTE_TYPE", DialogRoutes.IDBDAY)
                    ), AnimationUtil.getDefaultAnimation()
                )
            }
            DialogRoutes.IDNAME -> {
                this.findNavController(R.id.child_nav_host_fragment).navigate(
                    R.id.dialogFragment, bundleOf(
                        Pair<String, DialogRoutes>("ROUTE_TYPE", DialogRoutes.IDNAME)
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
            DialogRoutes.DENIED -> {
                this.findNavController(R.id.child_nav_host_fragment).navigate(
                    R.id.dialogFragment, bundleOf(
                        Pair<String, DialogRoutes>("ROUTE_TYPE", DialogRoutes.DENIED)
                    ), AnimationUtil.getDefaultAnimation()
                )
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

    override fun onBackPressed() {
        Log.d(MainActivity::class.simpleName, "User tried pressing back button")
    }

    override fun onDestroy() {
        QiSDK.unregister(this@MainActivity, RobotManager.robot)
        super.onDestroy()
    }
}