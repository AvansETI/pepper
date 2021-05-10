package com.pepper.care.dialog.presentation.viewmodels

import android.app.Activity
import android.graphics.Color
import android.graphics.Typeface
import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.awesomedialog.*
import com.pepper.care.R
import com.pepper.care.common.AppResult
import com.pepper.care.common.entities.PatientDetails
import com.pepper.care.common.usecases.GetPatientDetailsUseCaseUsingRepository
import com.pepper.care.dialog.DialogConstants.DIALOG_MOCK_CHOICE
import com.pepper.care.dialog.DialogConstants.DIALOG_MOCK_MEDICATION
import com.pepper.care.dialog.DialogConstants.DIALOG_MOCK_NAME
import com.pepper.care.dialog.DialogConstants.DIALOG_MOCK_QUESTION
import com.pepper.care.dialog.DialogConstants.DIALOG_MOCK_TIME
import com.pepper.care.dialog.DialogRoutes
import kotlinx.coroutines.launch

class DialogViewModelUsingUsecases(
    private val getPatientDetails: GetPatientDetailsUseCaseUsingRepository
) : ViewModel(), DialogViewModel {

    override val bottomText: MutableLiveData<String> = MutableLiveData("")
    private val fetchedDetails: MutableLiveData<PatientDetails> = MutableLiveData(
        PatientDetails(
            DIALOG_MOCK_NAME)
    )

    private val currentScreen: MutableLiveData<DialogRoutes> = MutableLiveData(DialogRoutes.INTRO)

    override fun updateDataBasedOnRoute(type: DialogRoutes) {
        currentScreen.apply {
            value = type
        }
        handleRoute()
    }

    override fun onConfirmResult(view: View) {
        createDialog(view)
    }

    private fun handleRoute(){
        when(currentScreen.value){
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
                fetchPatientDetails()
                bottomText.apply {
                    value = "Welkom, ${fetchedDetails.value?.fullName}. Wat fijn om u weer te zien!"
                }
            }
            DialogRoutes.MEDICATION -> {
                bottomText.apply {
                    value = "Goedendag, ${fetchedDetails.value?.fullName}. Om ${DIALOG_MOCK_TIME}u moet u de volgende medicatie innemen, ${DIALOG_MOCK_MEDICATION}."
                }
            }
            DialogRoutes.QUESTION -> {
                bottomText.apply {
                    value = "${DIALOG_MOCK_QUESTION}, ${fetchedDetails.value?.fullName}?"
                }
            }
            DialogRoutes.GOODBYE -> {
                bottomText.apply {
                    value = "Tot ziens, ${fetchedDetails.value?.fullName}. Ik wens u nog een fijne dag tegemoet!"
                }
            }
        }
    }

    private fun createDialog(view: View) {
        AwesomeDialog.build(view.context as Activity)
            .title("Klopt het onderstaande?", Typeface.DEFAULT_BOLD, R.color.black)
            .body("U heeft gekozen voor, '${DIALOG_MOCK_CHOICE}'.", null, R.color.black)
            .onPositive("Ja", R.color.green) {
                Log.d(DialogViewModelUsingUsecases::class.simpleName, "${currentScreen.value?.name} - Akkoord.")
            }
            .onNegative("Nee", R.color.red) {
                Log.d(DialogViewModelUsingUsecases::class.simpleName, "${currentScreen.value?.name} - Geannuleerd")
            }
    }

    private fun fetchPatientDetails() {
        viewModelScope.launch {
            when (val result =  getPatientDetails.invoke()) {
                is AppResult.Success -> {
                    fetchedDetails.apply {
                        value = result.successData
                    }
                }
                is AppResult.Error -> result.exception.message
            }
        }
    }
}