package com.pepper.care.common.di

import android.app.Application
import org.koin.dsl.module
import com.pepper.care.common.repo.*
import com.pepper.care.common.usecases.GetPatientUseCaseUsingRepository
import com.pepper.care.core.services.mqtt.MessagingHelper
import org.koin.android.ext.koin.androidApplication

val commonModule = module {
    single {
        getDataStore(androidApplication())
    }
    single {
        GetPatientUseCaseUsingRepository(get())
    }
    single<PatientRepository> {
        providePatientRepository(get())
    }
    single<MessagingHelper> {
        provideMessagingHelper(get(), get())
    }
}

fun getDataStore(androidApplication: Application): AppPreferencesRepository {
    return AppPreferencesRepository(androidApplication)
}

fun providePatientRepository(appPreferencesRepository: AppPreferencesRepository): PatientRepository {
    return PatientRepositoryImpl(appPreferencesRepository)
}

fun provideMessagingHelper(appPreferencesRepository: AppPreferencesRepository, patientRepository: PatientRepository): MessagingHelper {
    return MessagingHelper(appPreferencesRepository, patientRepository)
}


