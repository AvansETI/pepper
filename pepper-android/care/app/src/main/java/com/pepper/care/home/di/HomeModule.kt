package com.pepper.care.home.di

import com.pepper.care.home.presenstation.viewmodels.HomeViewModelUsingUsecases
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val homeModule = module {
    viewModel {
        HomeViewModelUsingUsecases()
    }
}
