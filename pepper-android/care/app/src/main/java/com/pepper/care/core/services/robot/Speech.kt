package com.pepper.care.core.services.robot

import android.util.Log
import com.aldebaran.qi.Future
import com.aldebaran.qi.sdk.QiContext
import com.aldebaran.qi.sdk.`object`.conversation.Say
import com.aldebaran.qi.sdk.`object`.locale.Language
import com.aldebaran.qi.sdk.`object`.locale.Locale
import com.aldebaran.qi.sdk.`object`.locale.Region
import com.aldebaran.qi.sdk.builder.SayBuilder

object Speech {
    private var locale: Locale = Locale(Language.ENGLISH, Region.UNITED_STATES)


    fun say(text: String, qiContext: QiContext) {
        sayWithLocale(text, qiContext, locale)
    }

    fun setToDutch() {
        locale = Locale(Language.DUTCH, Region.NETHERLANDS)
    }

    fun setToEnglish() {
        locale = Locale(Language.ENGLISH, Region.UNITED_STATES)
    }

    fun sayWithLocale(text: String, qiContext: QiContext, locale: Locale) {
        Log.d("RobotTalk", text)

        val sayBuilding: Future<Say> = SayBuilder.with(qiContext)
            .withText("Hello")
            .buildAsync() // OK.

        val sayAsync: Say.Async = sayBuilding.async()
        sayAsync.async().run()

    }

}
