package com.pepper.care.dialog.di

import com.pepper.care.dialog.common.usecases.GetAvailableScreensUseCaseUsingRepository
import com.pepper.care.dialog.presentation.viewmodels.DialogViewModelUsingUsecases
import com.pepper.care.dialog.repo.AvailableScreenRepository
import com.pepper.care.dialog.repo.AvailableScreenRepositoryImpl
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val dialogModule = module {
    single {
        GetAvailableScreensUseCaseUsingRepository(get())
    }
    single<AvailableScreenRepository> {
        provideAvailableScreenRepository()
    }
    viewModel {
        DialogViewModelUsingUsecases(get(), get())
    }
}

fun provideAvailableScreenRepository(): AvailableScreenRepository {
    return AvailableScreenRepositoryImpl()
}