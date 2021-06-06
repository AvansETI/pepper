package com.pepper.care.core.services.platform.entities

class PlatformPatient(
    val id: String,
    val name: String,
    val birthDate: String,
    val allergies: Set<Allergy>,
) : PlatformEntity()