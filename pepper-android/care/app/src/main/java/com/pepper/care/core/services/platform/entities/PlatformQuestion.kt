package com.pepper.care.core.services.platform.entities

import org.joda.time.LocalDate

data class PlatformQuestion(
    val id: String,
    val patientId: String,
    val text: String,
    val timestamp: LocalDate
) : PlatformEntity()