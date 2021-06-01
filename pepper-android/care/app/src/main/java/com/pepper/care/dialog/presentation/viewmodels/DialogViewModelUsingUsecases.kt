package com.pepper.care.dialog.presentation.viewmodels

import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import androidx.core.os.bundleOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import com.aldebaran.qi.sdk.`object`.conversation.Phrase
import com.pepper.care.R
import com.pepper.care.common.AppResult
import com.pepper.care.common.DialogCallback
import com.pepper.care.common.DialogUtil
import com.pepper.care.common.usecases.GetPatientNameUseCaseUsingRepository
import com.pepper.care.core.services.robot.DynamicConcepts
import com.pepper.care.core.services.robot.RobotManager
import com.pepper.care.dialog.DialogConstants.DIALOG_MOCK_ANSWER
import com.pepper.care.dialog.DialogConstants.DIALOG_MOCK_ID_LENGTH
import com.pepper.care.dialog.DialogConstants.DIALOG_MOCK_MSG_LENGTH
import com.pepper.care.dialog.DialogConstants.DIALOG_MOCK_NAME
import com.pepper.care.dialog.DialogConstants.DIALOG_MOCK_QUESTION
import com.pepper.care.dialog.DialogConstants.DIALOG_MOCK_TIME
import com.pepper.care.dialog.DialogRoutes
import com.pepper.care.dialog.FabType
import com.pepper.care.dialog.common.usecases.GetAvailableScreensUseCaseUsingRepository
import com.pepper.care.dialog.common.views.FabCallback
import kotlinx.coroutines.launch
import java.util.*
import kotlin.concurrent.schedule

class DialogViewModelUsingUsecases(
    private val getName: GetPatientNameUseCaseUsingRepository,
    private val getAvailableScreens: GetAvailableScreensUseCaseUsingRepository
) : ViewModel(), DialogViewModel {

    override val bottomText: MutableLiveData<String> = MutableLiveData("")
    override val inputText: MutableLiveData<String> = MutableLiveData("")
    override val isNextButtonVisible: MutableLiveData<Boolean> = MutableLiveData(false)
    override val isKeyboardVisible: MutableLiveData<Boolean> = MutableLiveData(false)
    override val isKeyboardNumeric: MutableLiveData<Boolean> = MutableLiveData(false)
    override val inputTextLength: MutableLiveData<Int> = MutableLiveData(0)
    override val fabType: MutableLiveData<FabType> = MutableLiveData(FabType.NEXT)

    private val fetchedName: MutableLiveData<String> = MutableLiveData(DIALOG_MOCK_NAME)
    private val currentScreen: MutableLiveData<DialogRoutes> = MutableLiveData(DialogRoutes.INTRO)
    private val fetchedAvailableScreens: MutableLiveData<IntArray> =
        MutableLiveData(intArrayOf(0, 0, 0))

    override fun updateDataBasedOnRoute(type: DialogRoutes) {
        currentScreen.apply {
            value = type
        }
        handleRouteEvents()
    }

    private fun handleRouteEvents() {
        when (currentScreen.value) {
            DialogRoutes.INTRO -> {
                bottomText.apply {
                    value =
                        "Welkom, mijn naam is Pepper! Vandaag, zal ik het werk van mijn collega overnemen."
                }
                setupNextButton()
            }
            DialogRoutes.ID -> {
                bottomText.apply {
                    value = "Om verder te gaan met het proces, heb ik uw ID nodig."
                }
                setupKeyboardButton()
            }
            DialogRoutes.PATIENT -> {
                fetchPatientDetails()
                fetchUpcomingScreens()
                bottomText.apply {
                    value = "Wat fijn om u weer te zien, ${fetchedName.value}!"
                }
                setupNextButton()
            }
            DialogRoutes.REMINDER -> {
                fetchPatientDetails()
                fetchReminders()
                fetchUpcomingScreens()
                bottomText.apply {
                    value =
                        "Om ${DIALOG_MOCK_TIME}u moet u medicatie innemen."
                }
                setupNextButton()
            }
            DialogRoutes.QUESTION -> {
                fetchPatientDetails()
                fetchQuestions()
                bottomText.apply {
                    value = "${DIALOG_MOCK_QUESTION}?"
                }
                setupKeyboardButton()
            }
            DialogRoutes.GOODBYE -> {
                fetchPatientDetails()
                bottomText.apply {
                    value =
                        "Tot ziens, ${fetchedName.value}. Ik wens u nog een fijne dag tegemoet!"
                }
                setupNextButton()
            }
            else -> throw NotImplementedError("Not implemented")
        }
    }

    override val fabCallback: FabCallback =
        object : FabCallback {
            override fun onClick(view: View) {
                when (fabType.value) {
                    FabType.NEXT -> {
                        when (currentScreen.value) {
                            DialogRoutes.INTRO -> {
                                view.findNavController().navigate(
                                    R.id.dialogFragment, bundleOf(
                                        Pair<String, DialogRoutes>("ROUTE_TYPE", DialogRoutes.ID)
                                    )
                                )
                            }
                            DialogRoutes.PATIENT -> {
                                when {
                                    fetchedAvailableScreens.value!![0] == 1 -> {
                                        view.findNavController().navigate(R.id.orderFragment)
                                    }
                                    fetchedAvailableScreens.value!![1] == 1 -> {
                                        view.findNavController().navigate(
                                            R.id.dialogFragment, bundleOf(
                                                Pair<String, DialogRoutes>(
                                                    "ROUTE_TYPE",
                                                    DialogRoutes.REMINDER
                                                )
                                            )
                                        )
                                    }
                                    fetchedAvailableScreens.value!![2] == 1 -> {
                                        view.findNavController().navigate(
                                            R.id.dialogFragment, bundleOf(
                                                Pair<String, DialogRoutes>(
                                                    "ROUTE_TYPE",
                                                    DialogRoutes.QUESTION
                                                )
                                            )
                                        )
                                    }
                                    else -> {
                                        view.findNavController().navigate(R.id.feedbackFragment)
                                    }
                                }
                            }
                            DialogRoutes.REMINDER -> {
                                when {
                                    fetchedAvailableScreens.value!![2] == 1 -> {
                                        view.findNavController().navigate(
                                            R.id.dialogFragment, bundleOf(
                                                Pair<String, DialogRoutes>(
                                                    "ROUTE_TYPE",
                                                    DialogRoutes.QUESTION
                                                )
                                            )
                                        )
                                    }
                                    else -> {
                                        view.findNavController().navigate(R.id.feedbackFragment)
                                    }
                                }
                            }
                            DialogRoutes.GOODBYE -> {
                                view.findNavController().navigate(R.id.homeFragment)
                            }
                            else -> throw IllegalStateException("Not implemented")
                        }
                    }
                    FabType.KEYBOARD -> {
                        when (currentScreen.value) {
                            DialogRoutes.ID -> {
                                inputTextLength.apply { value = DIALOG_MOCK_ID_LENGTH }
                                isKeyboardNumeric.apply { value = true }
                            }
                            else -> {
                                inputTextLength.apply { value = DIALOG_MOCK_MSG_LENGTH }
                                isKeyboardNumeric.apply { value = false }
                            }
                        }
                        isKeyboardVisible.postValue(true)
                    }
                }
            }
        }

    private fun navigateToDialog(view: View) {
        when (currentScreen.value) {
            DialogRoutes.ID -> {
                DialogUtil.buildDialog(
                    view, fetchedName.value!!,
                    currentScreen.value!!, dialogCallback
                )
            }
            DialogRoutes.QUESTION -> {
                DialogUtil.buildDialog(
                    view,
                    if (inputText.value!!.isNotBlank()) inputText.value!! else DIALOG_MOCK_ANSWER,
                    currentScreen.value!!,
                    dialogCallback
                )
            }
            else -> throw IllegalStateException("Not implemented")
        }
    }

    private fun setupKeyboardButton() {
        fabType.apply {
            value = FabType.KEYBOARD
        }
        isNextButtonVisible.postValue(true)
    }

    private fun setupNextButton() {
        fabType.apply {
            value = FabType.NEXT
        }
        Timer().schedule(1000) {
            isNextButtonVisible.postValue(true)
        }
    }

    override val keyboardKeyListener: View.OnKeyListener =
        View.OnKeyListener { view, keyCode, event ->
            if (event?.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                isKeyboardVisible.postValue(false)
                navigateToDialog(view)
                true
            } else false
        }

    override val inputTextWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            // Do nothing.
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            inputText.apply {
                value = s.toString()
            }
        }

        override fun afterTextChanged(s: Editable?) {
            // Do nothing.
        }
    }

    private fun fetchUpcomingScreens() {
        viewModelScope.launch {
            when (val result = getAvailableScreens.invoke()) {
                is AppResult.Success -> {
                    fetchedAvailableScreens.postValue(result.successData)
                }
                is AppResult.Error -> result.exception.message
            }
        }
    }

    private fun fetchPatientDetails() {
        viewModelScope.launch {
            when (val result = getName.invoke()) {
                is AppResult.Success -> {
                    fetchedName.apply { value = result.successData }
                    RobotManager.addDynamicContents(
                        DynamicConcepts.NAME,
                        listOf(Phrase(result.successData))
                    )
                }
                is AppResult.Error -> result.exception.message
            }
        }
    }

    private fun fetchReminders() {
        viewModelScope.launch {
            RobotManager.addDynamicContents(
                DynamicConcepts.REMINDERS,
                listOf(Phrase(bottomText.value))
            )
        }
    }

    private fun fetchQuestions() {
        viewModelScope.launch {
            RobotManager.addDynamicContents(
                DynamicConcepts.QUESTIONS,
                listOf(Phrase(bottomText.value))
            )
        }
    }

    private val dialogCallback: DialogCallback =
        object : DialogCallback {
            override fun onDialogConfirm(view: View) {
                when (currentScreen.value) {
                    DialogRoutes.ID -> {
                        view.findNavController().navigate(
                            R.id.dialogFragment, bundleOf(
                                Pair<String, DialogRoutes>("ROUTE_TYPE", DialogRoutes.PATIENT)
                            )
                        )
                    }
                    DialogRoutes.QUESTION -> {
                        view.findNavController().navigate(R.id.feedbackFragment)
                    }
                    else -> throw IllegalStateException("Not implemented")
                }
            }

            override fun onDialogDeny(view: View) {

            }
        }
}