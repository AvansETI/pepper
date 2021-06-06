package com.pepper.care.core.services.platform.entities

data class PlatformMeal(
    val id: String?,
    val name: String?,
    val description: String?,
    val allergies: Set<Allergy>?,
    val calories: String?,
    val image: String?
) : PlatformEntity()