package com.pepper.care.core.services.time

import android.content.Context
import android.content.Intent
import androidx.lifecycle.LifecycleService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.joda.time.LocalDateTime
import java.util.*

@FlowPreview
@ExperimentalCoroutinesApi
class TimeBasedInterfaceService : LifecycleService() {

    companion object {
        lateinit var callback: TimeInterfaceCallbacks

        fun start(context: Context, callback: TimeInterfaceCallbacks) {
            val intent = Intent(context, TimeBasedInterfaceService::class.java)
            this.callback = callback
            context.startService(intent)
        }

        fun stop(context: Context) {
            val intent = Intent(context, TimeInterfaceCallbacks::class.java)
            context.stopService(intent)
        }
    }

    override fun onCreate() {
        super.onCreate()
        setCallback()
    }

    private fun setCallback() {

    }
}
