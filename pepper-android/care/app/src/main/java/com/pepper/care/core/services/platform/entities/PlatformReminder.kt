package com.pepper.care.core.services.platform.entities

import org.joda.time.LocalDateTime

data class PlatformReminder(
    val id: String,
    val patientId: String,
    val thing: String,
    val timestamp: LocalDateTime
) : PlatformEntity()