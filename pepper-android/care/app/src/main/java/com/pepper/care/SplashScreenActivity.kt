package com.pepper.care

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.joda.time.LocalTime

@ExperimentalStdlibApi
@FlowPreview
@ExperimentalCoroutinesApi
class SplashScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        updateAppBasedOnTime()
        Thread.sleep(1750)

        val newIntent = Intent(this, MainActivity::class.java)
            .setAction(intent.action)
            .putExtras(intent)
        startActivity(newIntent)
        finish()
    }

    private fun updateAppBasedOnTime() {
        val currentTime = LocalTime()
        when (isDayOrNight(currentTime)) {
            InterfaceTime.DAY -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                Log.d(MainActivity::class.simpleName, "Applied Day Theme")
            }
            InterfaceTime.NIGHT -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                Log.d(MainActivity::class.simpleName, "Applied Night Theme")
            }
        }
    }

    private fun isDayOrNight(time: LocalTime): InterfaceTime {
        return if (time.hourOfDay in 21 downTo 6) InterfaceTime.DAY else InterfaceTime.NIGHT
    }

    private enum class InterfaceTime{
        DAY, NIGHT
    }
}