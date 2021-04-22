package com.pepper.care

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.aldebaran.qi.sdk.QiContext
import com.aldebaran.qi.sdk.QiSDK
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks
import com.aldebaran.qi.sdk.design.activity.RobotActivity
import com.pepper.care.common.CommonConstants.COMMON_DEVICE_ID
import com.pepper.care.common.usecases.GetNetworkConnectionStateUseCase
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity(), RobotLifecycleCallbacks {

    private val getNetworkConnectionStateUseCase: GetNetworkConnectionStateUseCase by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        // Register the RobotLifecycleCallbacks to this Activity.
//        QiSDK.register(this, this)

        // Check for connection with platform
        val connectionResult: MutableLiveData<GetNetworkConnectionStateUseCase.ConnectionState> = MutableLiveData()

        lifecycleScope.launch {
            getNetworkConnectionStateUseCase.invoke(connectionResult, COMMON_DEVICE_ID)
        }

        connectionResult.observe(this, Observer { result ->
            Log.d("RESULT", result.toString())
            when (result) {
                GetNetworkConnectionStateUseCase.ConnectionState.NO_INTERNET_CONNECTION -> setup()
                GetNetworkConnectionStateUseCase.ConnectionState.CONNECTION_VERIFIED -> setup()
                else -> Log.d("MAIN", "Starting up")
            }
        })
    }

    private fun setup() {

    }

    override fun onDestroy() {
        // Unregister the RobotLifecycleCallbacks for this Activity.
//        QiSDK.unregister(this, this)
        super.onDestroy()
    }

    override fun onRobotFocusGained(qiContext: QiContext) {
        // The robot focus is gained.
    }

    override fun onRobotFocusLost() {
        // The robot focus is lost.
    }

    override fun onRobotFocusRefused(reason: String) {
        // The robot focus is refused.
    }
}