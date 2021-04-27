package com.pepper.care.order.di

import com.pepper.care.common.api.PlatformApi
import com.pepper.care.common.repo.PlatformMealsRepository
import com.pepper.care.common.repo.PlatformMealsRepositoryImpl
import com.pepper.care.order.common.usecases.GetPlatformMealsUseCase
import com.pepper.care.order.common.usecases.GetPlatformMealsUseCaseUsingRepository
import com.pepper.care.order.presentation.viewmodels.OrderViewModelUsingUsecases
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val orderModule = module {
    single {
        GetPlatformMealsUseCaseUsingRepository(get())
    }
    single {
        providePlatformMealsRepository(get())
    }
    viewModel {
        OrderViewModelUsingUsecases(get())
    }
}

fun providePlatformMealsRepository(api: PlatformApi): PlatformMealsRepository {
    return PlatformMealsRepositoryImpl(api)
}