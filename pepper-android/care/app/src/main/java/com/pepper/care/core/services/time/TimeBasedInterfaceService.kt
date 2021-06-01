package com.pepper.care.core.services.time

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LifecycleService
import com.pepper.care.common.CommonConstants
import kotlinx.coroutines.*
import org.joda.time.LocalTime
import org.koin.android.ext.android.inject

@FlowPreview
@ExperimentalCoroutinesApi
class TimeBasedInterfaceService : LifecycleService() {

    private val sharedPreferences: SharedPreferences.Editor by inject()

    companion object {
        const val LOOP_TIMER_MS: Long = 900000 /* Every 15 min */
        var isFinished: Boolean = true

        fun start(context: Context) {
            val intent = Intent(context, TimeBasedInterfaceService::class.java)
            context.startService(intent)
        }

        fun stop(context: Context) {
            val intent = Intent(context, TimeBasedInterfaceService::class.java)
            isFinished = false
            context.stopService(intent)
        }
    }

    override fun onCreate() {
        super.onCreate()
        initLoop()
    }

    private fun initLoop() {
        GlobalScope.launch {
            do {
                val currentTime = LocalTime()
                val timeOfDay = isDayOrNight(currentTime)

                sharedPreferences.putString(CommonConstants.COMMON_SHARED_PREF_LIVE_THEME_KEY, timeOfDay.name).commit()

                Log.d(
                    TimeBasedInterfaceService::class.simpleName,
                    "${currentTime.toString("HH:mm")}, ${timeOfDay.name}"
                )
                delay(LOOP_TIMER_MS)
            } while (isFinished)
        }
    }

    private fun isDayOrNight(time: LocalTime): InterfaceTime {
        return if (time.hourOfDay in 22 downTo 6) InterfaceTime.DAY else InterfaceTime.NIGHT
    }
}
