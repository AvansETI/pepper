package com.pepper.care.core.services.platform.entities

import java.time.LocalDateTime

data class PlatformFeedback(
    val id: String?,
    val patientId: String?,
    val status: String?,
    val description: String?,
    val timestamp: LocalDateTime?
) : PlatformEntity()