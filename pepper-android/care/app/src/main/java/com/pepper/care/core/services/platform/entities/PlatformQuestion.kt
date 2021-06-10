package com.pepper.care.core.services.platform.entities

data class PlatformQuestion(
    var id: String?,
    var patientId: String?,
    var text: String?,
) : PlatformEntity()