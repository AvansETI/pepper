package com.pepper.care.order.di

import com.pepper.care.common.repo.AppPreferencesRepository
import com.pepper.care.order.common.usecases.AddPatientFoodChoiceUseCaseUsingRepository
import com.pepper.care.order.common.usecases.GetPlatformMealsUseCaseUsingRepository
import com.pepper.care.order.presentation.viewmodels.OrderViewModelUsingUsecases
import com.pepper.care.order.repo.OrderRepository
import com.pepper.care.order.repo.OrderRepositoryImpl
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val orderModule = module {
    single {
        provideOrderRepository(get())
    }
    single {
        GetPlatformMealsUseCaseUsingRepository(get())
    }
    single {
        AddPatientFoodChoiceUseCaseUsingRepository(get())
    }
    viewModel {
        OrderViewModelUsingUsecases(get(), get())
    }
}

fun provideOrderRepository(appPreferencesRepository: AppPreferencesRepository): OrderRepository {
    return OrderRepositoryImpl(appPreferencesRepository)
}