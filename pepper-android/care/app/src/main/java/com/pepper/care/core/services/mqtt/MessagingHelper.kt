package com.pepper.care.core.services.mqtt

import android.util.Log
import com.pepper.care.common.repo.AppPreferencesRepository
import com.pepper.care.common.repo.PatientRepository
import com.pepper.care.core.services.platform.entities.*
import com.pepper.care.core.services.platform.entities.PlatformMessageBuilder.Sender
import com.pepper.care.core.services.platform.entities.PlatformMessageBuilder.Person
import com.pepper.care.core.services.platform.entities.PlatformMessageBuilder.Task
import java.lang.Exception

class MessagingHelper(
    private val appPreferences: AppPreferencesRepository,
    private val patientRepository: PatientRepository
) : MqttMessageCallbacks {

    private val meals: MutableList<PlatformMeal> = mutableListOf()
    private val questions: MutableList<PlatformQuestion> = mutableListOf()
    private val reminders: MutableList<PlatformReminder> = mutableListOf()

    override suspend fun onMessageReceived(topic: String?, message: String?) {
        val platformMessage = parse(message!!) ?: return

        if (platformMessage.sender != Sender.PLATFORM) {
            return
        }

        Log.i(
            MessagingHelper::class.simpleName,
            "Received to following message: '${message}' from ${topic!!}"
        )

        handlePlatformMessage(platformMessage)
    }

    private fun parse(message: String): PlatformMessage? {
        try {
            val messageSplit = message.split('#').toTypedArray()
            val path = messageSplit[0]
            val data = messageSplit[1]
            val pathSplit = path.split(':').toTypedArray()

            return PlatformMessage(
                Sender.valueOf(pathSplit[0]),
                pathSplit[1],
                Person.valueOf(pathSplit[2]),
                pathSplit[3],
                Task.valueOf(pathSplit[4]),
                pathSplit[5],
                data.substring(1, data.length - 1)
            )
        } catch (e: Exception) {
            Log.e(MessagingHelper::class.simpleName, "Error parsing platform message")
            return null
        }

    }

    private suspend fun handlePlatformMessage(message: PlatformMessage) {
        Log.i(MessagingHelper::class.simpleName, "new message: $message")

        when (message.task) {
            Task.PATIENT_ID -> {
                val id = message.data!!
                appPreferences.updatePatientIdState(id)
            }
            Task.PATIENT_NAME -> {
                val name = message.data!!
                appPreferences.updatePatientNameState(name)
            }
            Task.MEAL_ID -> {
                val ids: List<String> = parseIds(message.data!!)

                for (id in ids) {
                    if (id != "") {
                        appPreferences.updatePublishMessage(
                            PlatformMessageBuilder.Builder()
                                .task(Task.MEAL)
                                .taskId(id)
                                .build()
                        )
                    }
                }
            }
            Task.MEAL_NAME -> {
                val id = message.taskId!!
                val name = message.data!!

                var foundMeal = false
                for (meal in meals) {
                    if (meal.id == id) {
                        foundMeal = true
                        meal.name = name
                    }
                }

                if (!foundMeal) {
                    meals.add(PlatformMeal(id, name, null, null, null, null))
                }

                appPreferences.updateMealsState(meals)
            }
            Task.MEAL_DESCRIPTION -> {
                val id = message.taskId!!
                val description = message.data!!

                var foundMeal = false
                for (meal in meals) {
                    if (meal.id == id) {
                        foundMeal = true
                        meal.description = description
                    }
                }

                if (!foundMeal) {
                    meals.add(PlatformMeal(id, null, description, null, null, null))
                }

                appPreferences.updateMealsState(meals)
            }
            Task.MEAL_CALORIES -> {
                val id = message.taskId!!
                val calories = message.data!!

                var foundMeal = false
                for (meal in meals) {
                    if (meal.id == id) {
                        foundMeal = true
                        meal.calories = calories
                    }
                }

                if (!foundMeal) {
                    meals.add(PlatformMeal(id, null, null, null, calories, null))
                }

                appPreferences.updateMealsState(meals)
            }
            Task.MEAL_ALLERGIES -> {
                val data = message.data!!
                val allergiesList: List<String> = data.substring(1, data.length - 1).replace(" ", "").split(",")
                val allergies: MutableSet<Allergy> = mutableSetOf()

                for (allergy in allergiesList) {
                    if (allergy != "") {
                        allergies.add(Allergy.valueOf(allergy))
                    }
                }

                val id = message.taskId!!

                var foundMeal = false
                for (meal in meals) {
                    if (meal.id == id) {
                        foundMeal = true
                        meal.allergies = allergies
                    }
                }

                if (!foundMeal) {
                    meals.add(PlatformMeal(id, null, null, allergies, null, null))
                }

                appPreferences.updateMealsState(meals)
            }
            Task.MEAL_IMAGE -> {
                val id = message.taskId!!
                val image = message.data!!

                var foundMeal = false
                for (meal in meals) {
                    if (meal.id == id) {
                        foundMeal = true
                        meal.image = image
                    }
                }

                if (!foundMeal) {
                    meals.add(PlatformMeal(id, null, null, null, null, image))
                }

                appPreferences.updateMealsState(meals)
            }
            Task.MEAL_ORDER_ID -> {
                appPreferences.updateMealOrderIdState(message.taskId!!)
            }
            Task.QUESTION_ID -> {
                val ids: List<String> = parseIds(message.data!!)

                for (id in ids) {
                    if (id != "") {
                        appPreferences.updatePublishMessage(
                            PlatformMessageBuilder.Builder()
                                .task(Task.QUESTION)
                                .taskId(id)
                                .build()
                        )
                    }
                }
            }
            Task.QUESTION_TEXT -> {
                questions.add(PlatformQuestion(message.taskId!!, message.personId!!, message.data!!))
                appPreferences.updateQuestionsState(questions)
            }
            Task.REMINDER_ID -> {
                val ids: List<String> = parseIds(message.data!!)

                for (id in ids) {
                    if (id != "") {
                        appPreferences.updatePublishMessage(
                            PlatformMessageBuilder.Builder()
                                .task(Task.REMINDER)
                                .taskId(id)
                                .build()
                        )
                    }
                }
            }
            Task.REMINDER_THING -> {
                reminders.add(PlatformReminder(message.taskId!!, message.personId!!, message.data!!))
                appPreferences.updateRemindersState(reminders)
            }
            Task.ANSWER_ID -> {
                appPreferences.updateAnswerIdState(message.taskId!!)
            }
            Task.FEEDBACK_ID -> {
                appPreferences.updateFeedbackIdState(message.taskId!!)
            }
            else -> {
                Log.e(MessagingHelper::class.simpleName, "Unknown task command")
            }
        }
    }

    private fun parseIds(data: String): List<String> {
        return data.substring(1, data.length - 1).replace(" ", "").split(",")
    }

}