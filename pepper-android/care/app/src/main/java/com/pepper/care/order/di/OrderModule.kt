package com.pepper.care.order.di

import android.content.SharedPreferences
import com.pepper.care.common.api.PlatformApi
import com.pepper.care.order.common.usecases.AddPatientFoodChoiceUseCaseUsingRepository
import com.pepper.care.order.common.usecases.GetPlatformMealsUseCaseUsingRepository
import com.pepper.care.order.presentation.viewmodels.OrderViewModelUsingUsecases
import com.pepper.care.order.repo.OrderRepository
import com.pepper.care.order.repo.OrderRepositoryImpl
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val orderModule = module {
    single {
        GetPlatformMealsUseCaseUsingRepository(get())
    }
    single {
        provideOrderRepository(get(), get())
    }
    single {
        AddPatientFoodChoiceUseCaseUsingRepository(get())
    }
    viewModel {
        OrderViewModelUsingUsecases(get(), get(), get())
    }
}

fun provideOrderRepository(api: PlatformApi, editor: SharedPreferences.Editor): OrderRepository {
    return OrderRepositoryImpl(api, editor)
}