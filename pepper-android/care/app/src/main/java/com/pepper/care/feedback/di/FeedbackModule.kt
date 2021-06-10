package com.pepper.care.feedback.di

import com.pepper.care.common.repo.AppPreferencesRepository
import com.pepper.care.feedback.common.usecases.AddPatientFeedbackUseCaseUsingRepository
import com.pepper.care.feedback.presentation.viewmodels.FeedbackViewModelUsingUsecases
import com.pepper.care.feedback.repo.FeedbackRepository
import com.pepper.care.feedback.repo.FeedbackRepositoryImpl
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val feedbackModule = module {
    single {
        provideFeedbackRepository(get())
    }
    single {
        AddPatientFeedbackUseCaseUsingRepository(get())
    }
    viewModel {
        FeedbackViewModelUsingUsecases(get(), get())
    }
}

fun provideFeedbackRepository(appPreferencesRepository: AppPreferencesRepository): FeedbackRepository {
    return FeedbackRepositoryImpl(appPreferencesRepository)
}