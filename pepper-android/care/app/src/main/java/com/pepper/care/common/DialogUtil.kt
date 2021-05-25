package com.pepper.care.common

import android.app.Activity
import android.graphics.Typeface
import android.view.View
import com.example.awesomedialog.*
import com.pepper.care.R
import com.pepper.care.dialog.DialogRoutes

object DialogUtil {

    fun buildDialog(view: View, body: String, screen: DialogRoutes, callback: DialogCallback){
        AwesomeDialog.build(view.context as Activity)
            .title(getDialogTitle(screen), Typeface.DEFAULT_BOLD, R.color.black)
            .body(getDialogBody(body, screen), null, R.color.black)
            .onPositive("Ja, dit klopt!", R.color.green) {
                callback.onDialogConfirm(view)
            }
            .onNegative("Nee, dit klopt niet!", R.color.red)
    }

    private fun getDialogTitle(screen: DialogRoutes): String {
        return when (screen) {
            DialogRoutes.ID -> "Bent u dit?"
            DialogRoutes.ORDER -> "Gekozen maaltijd:"
            else -> "U gaf als antwoord:"
        }
    }

    private fun getDialogBody(body: String, screen: DialogRoutes): String {
        return when (screen) {
            DialogRoutes.ID -> body
            DialogRoutes.ORDER -> body
            DialogRoutes.QUESTION -> body
            DialogRoutes.FEEDBACK -> body
            else -> "Geen antwoord gegeven."
        }
    }
}

interface DialogCallback{
    fun onDialogConfirm(view: View)
}