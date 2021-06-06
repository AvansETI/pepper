package com.pepper.care.common.di

import android.app.Application
import org.koin.dsl.module
import com.pepper.care.common.repo.*
import com.pepper.care.common.usecases.GetPatientNameUseCaseUsingRepository
import com.pepper.care.core.services.platform.entities.PlatformEntity
import com.pepper.care.core.services.platform.entities.PlatformMeal
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.koin.android.ext.koin.androidApplication

val commonModule = module {
    single {
        getDataStore(androidApplication())
    }
    single {
        GetPatientNameUseCaseUsingRepository(get())
    }
    single<PatientRepository> {
        providePatientRepository(get())
    }
}

fun getDataStore(androidApplication: Application): AppPreferencesRepository {
    return AppPreferencesRepository(androidApplication)
}

fun providePatientRepository(appPreferencesRepository: AppPreferencesRepository): PatientRepository {
    return PatientRepositoryImpl(appPreferencesRepository)
}


