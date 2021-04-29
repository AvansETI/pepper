package com.pepper.care.core

import android.app.Application
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.pepper.care.ActivityLifecycle
import com.pepper.care.common.di.commonModule
import com.pepper.care.core.services.mqtt.PlatformMqttListenerService
import com.pepper.care.home.di.homeModule
import com.pepper.care.order.di.orderModule
import kotlinx.coroutines.FlowPreview
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

@FlowPreview
@ExperimentalStdlibApi
class MyApplication : Application(), LifecycleObserver {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@MyApplication)
            modules(commonModule)
            modules(homeModule)
            modules(orderModule)
        }

        registerActivityLifecycleCallbacks(ActivityLifecycle)
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    internal fun onBackground() {

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    internal fun onForeground() {

    }
}