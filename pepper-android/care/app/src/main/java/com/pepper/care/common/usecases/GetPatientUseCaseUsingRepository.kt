package com.pepper.care.common.usecases

import com.pepper.care.common.repo.PatientRepository
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import org.joda.time.LocalDate


interface GetPatientUseCase {
    suspend operator fun invoke(name: String? = null, birthDate: LocalDate? = null) : StateFlow<String>
}

class GetPatientUseCaseUsingRepository(
    private val patientRepository: PatientRepository
) : GetPatientUseCase {

    override suspend fun invoke(name: String?, birthDate: LocalDate?) : StateFlow<String> {
        if (name == null && birthDate == null) {
            return patientRepository.fetchName()
        }

        return patientRepository.fetchName(name!!, birthDate!!)
    }

}