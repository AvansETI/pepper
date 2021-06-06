package com.pepper.care.common.utility

import android.app.Activity
import android.graphics.Typeface
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.example.awesomedialog.*
import com.pepper.care.R
import com.pepper.care.dialog.DialogRoutes

object DialogUtil {

    fun buildDialog(activity: Activity, body: String, screen: DialogRoutes, callback: DialogCallback?) : AlertDialog{
        return AwesomeDialog.build(activity)
            .title(getDialogTitle(screen), Typeface.DEFAULT_BOLD, R.color.black)
            .body(getDialogBody(body, screen), null, R.color.black)
            .onPositive("Ja, dit klopt!", R.color.green) {
                callback?.onDialogConfirm(activity.window.decorView.rootView)
            }
            .onNegative("Nee, dit klopt niet!", R.color.red) {
                callback?.onDialogDeny(activity.window.decorView.rootView)
            }
    }

    private fun getDialogTitle(screen: DialogRoutes): String {
        return when (screen) {
            DialogRoutes.IDNAME -> "Ben jij deze persoon?"
            DialogRoutes.ORDER -> "Gekozen maaltijd:"
            else -> "Je gaf als antwoord:"
        }
    }

    private fun getDialogBody(body: String, screen: DialogRoutes): String {
        return when (screen) {
            DialogRoutes.IDNAME -> body
            DialogRoutes.ORDER -> body
            DialogRoutes.QUESTION -> body
            DialogRoutes.FEEDBACK -> body
            else -> "Geen antwoord gegeven."
        }
    }
}

interface DialogCallback{
    fun onDialogConfirm(view: View)
    fun onDialogDeny(view: View)
}