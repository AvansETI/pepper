package com.pepper.care.core.services.mqtt

import android.util.Log
import com.pepper.care.common.repo.AppPreferencesRepository
import com.pepper.care.common.repo.PatientRepository
import com.pepper.care.core.services.platform.entities.Allergy
import com.pepper.care.core.services.platform.entities.PlatformMeal
import com.pepper.care.core.services.platform.entities.PlatformMessage
import com.pepper.care.core.services.platform.entities.PlatformMessageBuilder
import com.pepper.care.core.services.platform.entities.PlatformMessageBuilder.Sender
import com.pepper.care.core.services.platform.entities.PlatformMessageBuilder.Person
import com.pepper.care.core.services.platform.entities.PlatformMessageBuilder.Task
import java.lang.Exception

class MessagingHelper(
    private val appPreferences: AppPreferencesRepository,
    private val patientRepository: PatientRepository
) : MqttMessageCallbacks {

    var patientGetId = ""
    var mealGetId = ""
    var questionGetId = ""
    var reminderGetId = ""

    val mealOrderPostId = "2001"
    val answerPostId = "2002"
    val feedbackPostId = "2003"

    val meals: MutableList<PlatformMeal> = mutableListOf()

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
                val data = message.data!!
                val ids: List<String> = data.substring(1, data.length - 1).replace(" ", "").split(",")

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
            else -> {
                Log.e(MessagingHelper::class.simpleName, "Unknown task command")
            }
        }
    }

}