package com.pepper.care.core.services.platform.entities

import org.joda.time.LocalDateTime

data class PlatformQuestion(
    val id: String,
    val patientId: String,
    val text: String,
    val timestamp: LocalDateTime
) : PlatformEntity()