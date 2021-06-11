package com.pepper.care.core.services.platform.entities

data class PlatformReminder(
    var id: String?,
    var patientId: String?,
    var thing: String?,
) : PlatformEntity()