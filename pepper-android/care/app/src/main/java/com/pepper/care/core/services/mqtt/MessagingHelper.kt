package com.pepper.care.core.services.mqtt

import android.util.Log
import com.pepper.care.common.repo.AppPreferencesRepository
import com.pepper.care.common.repo.PatientRepository
import com.pepper.care.core.services.platform.entities.PlatformMessage
import com.pepper.care.core.services.platform.entities.PlatformMessageBuilder
import com.pepper.care.core.services.platform.entities.PlatformMessageBuilder.Sender
import com.pepper.care.core.services.platform.entities.PlatformMessageBuilder.Person
import com.pepper.care.core.services.platform.entities.PlatformMessageBuilder.Task
import org.koin.java.KoinJavaComponent.inject
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
            else -> {
                Log.e(MessagingHelper::class.simpleName, "Unknown task command")
            }
        }
    }


}