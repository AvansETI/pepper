package com.pepper.care.feedback.di

import android.content.SharedPreferences
import com.pepper.care.feedback.common.usecases.AddPatientHealthFeedbackUseCaseUsingRepository
import com.pepper.care.feedback.common.usecases.AddPatientGivenHealthFeedbackUseCaseUsingRepository
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
        AddPatientHealthFeedbackUseCaseUsingRepository(get())
    }
    single {
        AddPatientGivenHealthFeedbackUseCaseUsingRepository(get())
    }
    viewModel {
        FeedbackViewModelUsingUsecases()
    }
}

fun provideFeedbackRepository(editor: SharedPreferences.Editor): FeedbackRepository {
    return FeedbackRepositoryImpl(editor)
}