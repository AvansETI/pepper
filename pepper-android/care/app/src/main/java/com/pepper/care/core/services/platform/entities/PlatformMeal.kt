package com.pepper.care.core.services.platform.entities

data class PlatformMeal(
    var id: String?,
    var name: String?,
    var description: String?,
    var allergies: Set<Allergy>?,
    var calories: String?,
    var image: String?
) : PlatformEntity()