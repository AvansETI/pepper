package com.pepper.care.dialog.presentation.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.aldebaran.qi.sdk.`object`.conversation.Phrase
import com.pepper.care.common.repo.AppPreferencesRepository
import com.pepper.care.common.usecases.GetPatientUseCaseUsingRepository
import com.pepper.care.core.services.platform.entities.PlatformQuestion
import com.pepper.care.core.services.platform.entities.PlatformReminder
import com.pepper.care.core.services.robot.DynamicConcepts
import com.pepper.care.core.services.robot.RobotManager
import com.pepper.care.dialog.DialogConstants.DIALOG_NO_QUESTIONS
import com.pepper.care.dialog.DialogConstants.DIALOG_NO_REMINDERS
import com.pepper.care.dialog.DialogRoutes
import com.pepper.care.dialog.common.usecases.GetDailyQuestionsUseCaseUsingRepository
import com.pepper.care.dialog.common.usecases.GetDailyRemindersUseCaseUsingRepository
import kotlinx.coroutines.launch

class DialogViewModelUsingUsecases(
    private val get: GetPatientUseCaseUsingRepository,
    private val getReminders: GetDailyRemindersUseCaseUsingRepository,
    private val getQuestions: GetDailyQuestionsUseCaseUsingRepository
) : ViewModel(), DialogViewModel {

    override val bottomText: MutableLiveData<String> = MutableLiveData("")
    private val currentScreen: MutableLiveData<DialogRoutes> = MutableLiveData(DialogRoutes.INTRO)

    override fun updateDataBasedOnRoute(type: DialogRoutes) {
        currentScreen.apply { value = type }
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
            DialogRoutes.IDBDAY -> {
                bottomText.apply {
                    value =
                        "Om verder te gaan heb ik je geboortedatum nodig. Wijze: 'dag' 'maand' 'jaar'"
                }
            }
            DialogRoutes.IDNAME -> {
                bottomText.apply { value = "En wat is je naam?" }
            }
            DialogRoutes.PATIENT -> {
                fetchPatientName("Wat fijn om u weer te zien")
            }
            DialogRoutes.REMINDER -> {
                fetchPatientName(null)
                fetchReminders()
            }
            DialogRoutes.QUESTION -> {
                fetchPatientName(null)
                fetchQuestions()
            }
            DialogRoutes.DENIED -> {
                bottomText.apply { value = "Helaas kan ik je niet verder helpen. Tot ziens!" }
            }
            DialogRoutes.GOODBYE -> {
                fetchPatientName("Tot ziens")
            }
            else -> throw NotImplementedError("Not implemented")
        }
    }

    private fun fetchPatientName(text: String?) {

        viewModelScope.launch {
            val name = get.invoke().value
            RobotManager.addDynamicContents(
                DynamicConcepts.NAME,
                listOf(Phrase(name))
            )

            if (text != null) {
                bottomText.apply {
                    value = "$text, $name!"
                }
            }
        }
    }

    private fun fetchReminders() {
        viewModelScope.launch {
            val reminders: List<PlatformReminder> = getReminders.invoke().value
            val reminder = if (reminders.isNullOrEmpty()) DIALOG_NO_REMINDERS else reminders[0].thing
            bottomText.apply { value = reminder }
            RobotManager.addDynamicContents(
                DynamicConcepts.REMINDERS,
                listOf(Phrase(reminder))
            )
        }
    }

    private fun fetchQuestions() {
        viewModelScope.launch {
            val questions: List<PlatformQuestion> = getQuestions.invoke().value
            val question = if (questions.isNullOrEmpty()) DIALOG_NO_QUESTIONS else questions[0].text
            bottomText.apply { value = question }
            RobotManager.addDynamicContents(
                DynamicConcepts.QUESTIONS,
                listOf(Phrase(question))
            )

        }
    }
}