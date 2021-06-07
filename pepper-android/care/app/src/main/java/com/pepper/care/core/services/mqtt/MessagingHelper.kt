package com.pepper.care.core.services.mqtt

import android.util.Log
import com.pepper.care.core.services.platform.entities.PlatformMessage
import com.pepper.care.core.services.platform.entities.PlatformMessageBuilder
import com.pepper.care.core.services.platform.entities.PlatformMessageBuilder.Sender
import com.pepper.care.core.services.platform.entities.PlatformMessageBuilder.Person
import com.pepper.care.core.services.platform.entities.PlatformMessageBuilder.Task
import java.lang.Exception

class MessagingHelper : MqttMessageCallbacks {

    override fun onMessageReceived(topic: String?, message: String?) {
        Log.i(
            MessagingHelper::class.simpleName,
            "Received to following message: '${message!!}' from ${topic!!}"
        )

        val platformMessage = parse(message)

        if (platformMessage == null) {
            return
        }

        if (platformMessage.sender != Sender.PLATFORM) {
            return
        }

        handlePlatformMessage(platformMessage)

    }

    private fun handlePlatformMessage(message: PlatformMessage) {
        Log.i(MessagingHelper::class.simpleName, "new message: $message")

        when (message.task) {
            else -> {
                Log.e(MessagingHelper::class.simpleName, "Unknown task command")
            }
        }
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

}