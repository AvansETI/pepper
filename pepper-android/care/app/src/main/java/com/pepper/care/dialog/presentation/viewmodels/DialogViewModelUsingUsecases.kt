package com.pepper.care.dialog.presentation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pepper.care.dialog.DialogConstants
import com.pepper.care.dialog.DialogConstants.DIALOG_MOCK_MEDICATION
import com.pepper.care.dialog.DialogConstants.DIALOG_MOCK_NAME
import com.pepper.care.dialog.DialogConstants.DIALOG_MOCK_QUESTION
import com.pepper.care.dialog.DialogConstants.DIALOG_MOCK_TIME
import com.pepper.care.dialog.DialogRoutes
import org.joda.time.LocalDateTime
import java.util.*

class DialogViewModelUsingUsecases : ViewModel(), DialogViewModel {

    override val bottomText: MutableLiveData<String> = MutableLiveData("")

    override fun updateDataBasedOnRoute(type: DialogRoutes) {
        when(type){
            DialogRoutes.INTRO -> {
                bottomText.apply {
                    value = "Welkom, patient. Mijn naam is Pepper en ik zal het gesprek van mijn collega overnemen."
                }
            }
            DialogRoutes.ID -> {
                bottomText.apply {
                    value = "Om verder te gaan met het proces, heb ik uw naam en/of id nodig..."
                }
            }
            DialogRoutes.PATIENT -> {
                bottomText.apply {
                    value = "Welkom, ${DIALOG_MOCK_NAME}. Wat fijn om u weer te zien!"
                }
            }
            DialogRoutes.MEDICATION -> {
                bottomText.apply {
                    value = "Goedendag, ${DIALOG_MOCK_NAME}. Om ${DIALOG_MOCK_TIME}u moet u de volgende medicatie innemen, ${DIALOG_MOCK_MEDICATION}."
                }
            }
            DialogRoutes.QUESTION -> {
                bottomText.apply {
                    value = "${DIALOG_MOCK_QUESTION}, ${DIALOG_MOCK_NAME}?"
                }
            }
            DialogRoutes.GOODBYE -> {
                bottomText.apply {
                    value = "Tot ziens, ${DIALOG_MOCK_NAME}. Ik wens u nog een fijne dag tegemoet!"
                }
            }
        }
    }
}