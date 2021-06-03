package com.pepper.care.dialog.presentation.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aldebaran.qi.sdk.`object`.conversation.Phrase
import com.pepper.care.common.AppResult
import com.pepper.care.common.usecases.GetPatientNameUseCaseUsingRepository
import com.pepper.care.core.services.robot.DynamicConcepts
import com.pepper.care.core.services.robot.RobotManager
import com.pepper.care.dialog.DialogConstants.DIALOG_MOCK_NAME
import com.pepper.care.dialog.DialogConstants.DIALOG_MOCK_QUESTION
import com.pepper.care.dialog.DialogConstants.DIALOG_MOCK_TIME
import com.pepper.care.dialog.DialogRoutes
import com.pepper.care.dialog.common.usecases.GetAvailableScreensUseCaseUsingRepository
import kotlinx.coroutines.launch

class DialogViewModelUsingUsecases(
    private val getName: GetPatientNameUseCaseUsingRepository,
    private val getAvailableScreens: GetAvailableScreensUseCaseUsingRepository
) : ViewModel(), DialogViewModel {

    override val bottomText: MutableLiveData<String> = MutableLiveData("")
    override val inputText: MutableLiveData<String> = MutableLiveData("")

    private val currentScreen: MutableLiveData<DialogRoutes> = MutableLiveData(DialogRoutes.INTRO)
    private val fetchedName: MutableLiveData<String> = MutableLiveData(DIALOG_MOCK_NAME)
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
            }
            DialogRoutes.ACCESS -> {
                bottomText.apply {
                    value =
                        "Bij het gebruik van pepper wordt er informatie met de verpleegkundige gedeeld, ga je hiermee akkoord?"
                }
            }
            DialogRoutes.ID -> {
                bottomText.apply {
                    value = "Om verder te gaan met het proces, heb ik uw ID nodig."
                }
            }
            DialogRoutes.PATIENT -> {
                fetchPatientDetails()
                fetchUpcomingScreens()
                bottomText.apply {
                    value = "Wat fijn om u weer te zien, ${fetchedName.value}!"
                }
            }
            DialogRoutes.REMINDER -> {
                fetchPatientDetails()
                fetchReminders()
                fetchUpcomingScreens()
                bottomText.apply {
                    value =
                        "Om ${DIALOG_MOCK_TIME}u moet u medicatie innemen."
                }
            }
            DialogRoutes.QUESTION -> {
                fetchPatientDetails()
                fetchQuestions()
                bottomText.apply {
                    value = "${DIALOG_MOCK_QUESTION}?"
                }
            }
            DialogRoutes.DENIED -> {
                bottomText.apply {
                    value =
                        "Helaas kan ik je niet verder helpen. Tot ziens!"
                }
            }
            DialogRoutes.GOODBYE -> {
                fetchPatientDetails()
                bottomText.apply {
                    value =
                        "Tot ziens, ${fetchedName.value}!"
                }
            }
            else -> throw NotImplementedError("Not implemented")
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
}