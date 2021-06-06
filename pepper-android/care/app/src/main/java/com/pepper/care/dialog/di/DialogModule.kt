package com.pepper.care.dialog.di

import com.pepper.care.common.repo.AppPreferencesRepository
import com.pepper.care.dialog.common.usecases.AddPatientQuestionExplanationUseCaseUsingRepository
import com.pepper.care.dialog.common.usecases.GetDailyQuestionsUseCaseUsingRepository
import com.pepper.care.dialog.common.usecases.GetDailyRemindersUseCaseUsingRepository
import com.pepper.care.dialog.presentation.viewmodels.DialogViewModelUsingUsecases
import com.pepper.care.dialog.repo.QuestionRepository
import com.pepper.care.dialog.repo.QuestionRepositoryImpl
import com.pepper.care.dialog.repo.ReminderRepository
import com.pepper.care.dialog.repo.ReminderRepositoryImpl
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val dialogModule = module {
    single {
        provideReminderRepository(get())
    }
    single {
        GetDailyRemindersUseCaseUsingRepository(get())
    }
    single {
        provideQuestionRepository(get())
    }
    single {
        GetDailyQuestionsUseCaseUsingRepository(get())
    }
    single {
        AddPatientQuestionExplanationUseCaseUsingRepository(get())
    }
    viewModel {
        DialogViewModelUsingUsecases(get(), get(), get())
    }
}

fun provideReminderRepository(appPreferencesRepository: AppPreferencesRepository): ReminderRepository {
    return ReminderRepositoryImpl(appPreferencesRepository)
}

fun provideQuestionRepository(appPreferencesRepository: AppPreferencesRepository): QuestionRepository {
    return QuestionRepositoryImpl(appPreferencesRepository)
}