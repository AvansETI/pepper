package com.pepper.care.core.services.platform.entities

data class PlatformFeedback(
    val id: String,
    val name: String?,
    val description: String?,
) : PlatformEntity()