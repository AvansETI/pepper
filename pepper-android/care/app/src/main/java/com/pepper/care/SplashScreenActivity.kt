package com.pepper.care

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import androidx.core.content.res.ResourcesCompat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@ExperimentalStdlibApi
@FlowPreview
@ExperimentalCoroutinesApi
class SplashScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Thread.sleep(1750)

        val newIntent = Intent(this, MainActivity::class.java)
            .setAction(intent.action)
            .putExtras(intent)
        startActivity(newIntent)
        finish()
    }
}