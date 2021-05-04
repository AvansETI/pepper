package com.pepper.care.feedback.di

import com.pepper.care.feedback.presentation.viewmodels.FeedbackViewModelUsingUsecases
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val feedbackModule = module {
    viewModel {
        FeedbackViewModelUsingUsecases()
    }
}