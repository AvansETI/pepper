package com.pepper.care.common.di

import android.app.Application
import org.koin.dsl.module
import com.pepper.care.common.DynamicApiConverterFactory
import com.pepper.care.common.api.PlatformApi
import com.pepper.care.common.repo.*
import com.pepper.care.common.usecases.GetPatientBirthdayUseCaseUsingRepository
import com.pepper.care.common.usecases.GetPatientNameUseCaseUsingRepository
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidApplication
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

val commonModule = module {
    single {
        getDataStore(androidApplication())
    }
    single<Retrofit> {
        provideRetrofit(get())
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

/* Data Store Prefences */
fun getDataStore(androidApplication: Application): AppPreferencesRepository {
    return AppPreferencesRepository(androidApplication)
}

/* API Calling */
fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
    return Retrofit.Builder()
            .baseUrl("https://shitposts.nl/img/")
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


