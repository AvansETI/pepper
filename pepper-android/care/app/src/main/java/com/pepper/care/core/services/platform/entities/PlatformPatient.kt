package com.pepper.care.core.services.platform.entities

import java.time.LocalDate

class PlatformPatient(
    val id: String,
    val name: String,
    val birthDate: LocalDate,
    val allergies: Set<Allergy>,
) : PlatformEntity()