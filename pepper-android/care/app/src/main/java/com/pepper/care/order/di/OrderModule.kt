package com.pepper.care.order.di

import com.pepper.care.order.presentation.viewmodels.OrderViewModelUsingUsecases
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val orderModule = module {
    viewModel {
        OrderViewModelUsingUsecases()
    }
}
