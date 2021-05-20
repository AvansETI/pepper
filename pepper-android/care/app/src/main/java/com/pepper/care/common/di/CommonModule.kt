package com.pepper.care.common.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import org.koin.dsl.module
import com.pepper.care.common.CommonConstants
import com.pepper.care.common.DynamicApiConverterFactory
import com.pepper.care.common.api.PlatformApi
import com.pepper.care.common.repo.*
import com.pepper.care.common.usecases.GetPatientBirthdayUseCaseUsingRepository
import com.pepper.care.common.usecases.GetPatientNameUseCaseUsingRepository
import com.pepper.care.core.services.encryption.EncryptionService
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidApplication
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

val commonModule = module {
    single{
        getSharedPrefs(androidApplication())
    }
    single<SharedPreferences.Editor> {
        getSharedPrefs(androidApplication()).edit()
    }
    single<Retrofit> {
        provideRetrofit(get())
    }
    single<EncryptionService> {
        provideEncryptionService()
    }
    factory<OkHttpClient> {
        provideOkHttpClient()
    }
    factory<PlatformApi> {
        providePlatformApi(get())
    }
    single {
        GetPatientNameUseCaseUsingRepository(get())
    }
    single {
        GetPatientBirthdayUseCaseUsingRepository(get())
    }
    single<PatientRepository> {
        providePatientRepository()
    }
}

/* Shared Prefences */
fun getSharedPrefs(androidApplication: Application): SharedPreferences {
    return androidApplication.getSharedPreferences(CommonConstants.COMMON_PREFENCES, Context.MODE_PRIVATE)
}

/* API Calling */
fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
    return Retrofit.Builder()
            .baseUrl("https://5f92c184eca67c0016409f9d.mockapi.io/api/intern/")
            .client(okHttpClient)
            .addConverterFactory(DynamicApiConverterFactory.create())
            .build()
}

fun provideOkHttpClient(): OkHttpClient {
    return OkHttpClient()
            .newBuilder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build()
}

fun providePlatformApi(retrofit: Retrofit): PlatformApi {
    return retrofit.create(PlatformApi::class.java)
}

fun providePatientRepository(): PatientRepository {
    return PatientRepositoryImpl()
}

fun provideEncryptionService(): EncryptionService {
    return EncryptionService()
}


