package com.pepper.care.common.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import org.koin.dsl.module
import com.pepper.care.common.CommonConstants
import com.pepper.care.common.DynamicApiConverterFactory
import com.pepper.care.common.api.PlatformConnectionApi
import com.pepper.care.common.repo.PlatformConnectionRepository
import com.pepper.care.common.repo.PlatformConnectionRepositoryImpl
import com.pepper.care.common.usecases.GetNetworkConnectionStateUseCase
import com.pepper.care.common.usecases.GetNetworkConnectionStateUseCaseUsingRepository
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
    single<GetNetworkConnectionStateUseCase> {
        GetNetworkConnectionStateUseCaseUsingRepository(get())
    }
    single<PlatformConnectionRepository> {
        providePlatformConnectionRepository(get())
    }
    single<Retrofit> {
        provideRetrofit(get())
    }
    factory<OkHttpClient> {
        provideOkHttpClient()
    }
    factory<PlatformConnectionApi> {
        providePlatformConnectionApi(get())
    }
}

/* Shared Prefences */
fun getSharedPrefs(androidApplication: Application): SharedPreferences {
    return  androidApplication.getSharedPreferences(CommonConstants.COMMON_PREFENCES, Context.MODE_PRIVATE)
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

/* Check Platform Connection */
fun providePlatformConnectionApi(retrofit: Retrofit): PlatformConnectionApi {
    return retrofit.create(PlatformConnectionApi::class.java)
}

fun providePlatformConnectionRepository(api: PlatformConnectionApi): PlatformConnectionRepository {
    return PlatformConnectionRepositoryImpl(api)
}
