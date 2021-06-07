package com.pepper.care.core.services.platform.entities

import com.pepper.care.core.services.platform.entities.PlatformMessageBuilder.Sender
import com.pepper.care.core.services.platform.entities.PlatformMessageBuilder.Person
import com.pepper.care.core.services.platform.entities.PlatformMessageBuilder.Task

data class PlatformMessage(
    val sender: Sender?,
    val senderId: String?,
    val person: Person?,
    val personId: String?,
    val task: Task?,
    val taskId: String?,
    val data: String?
) : PlatformEntity()
