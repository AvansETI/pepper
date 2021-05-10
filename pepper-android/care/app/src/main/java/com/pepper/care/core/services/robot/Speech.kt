package com.pepper.care.core.services.robot

import android.util.Log
import com.aldebaran.qi.sdk.QiContext
import com.aldebaran.qi.sdk.builder.SayBuilder

object Speech {
    public fun say(text: String, qiContext: QiContext) {
        Log.d("RobotTalk", text)
        SayBuilder.with(qiContext).withText(text).build().run()
    }
}