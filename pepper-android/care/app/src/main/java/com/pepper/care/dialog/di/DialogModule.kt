package com.pepper.care.dialog.di

import com.pepper.care.dialog.presentation.viewmodels.DialogViewModelUsingUsecases
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val dialogModule = module {
    viewModel {
        DialogViewModelUsingUsecases(get())
    }
}