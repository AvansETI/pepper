package com.pepper.care.dialog.presentation.viewmodels

import android.app.Activity
import android.graphics.Typeface
import android.view.View
import androidx.core.os.bundleOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import com.example.awesomedialog.*
import com.pepper.care.R
import com.pepper.care.common.AppResult
import com.pepper.care.common.usecases.GetPatientNameUseCaseUsingRepository
import com.pepper.care.dialog.DialogConstants.DIALOG_MOCK_MEDICATION
import com.pepper.care.dialog.DialogConstants.DIALOG_MOCK_NAME
import com.pepper.care.dialog.DialogConstants.DIALOG_MOCK_QNA
import com.pepper.care.dialog.DialogConstants.DIALOG_MOCK_QUESTION
import com.pepper.care.dialog.DialogConstants.DIALOG_MOCK_TIME
import com.pepper.care.dialog.DialogRoutes
import com.pepper.care.dialog.FabType
import com.pepper.care.dialog.common.usecases.GetAvailableScreensUseCaseUsingRepository
import com.pepper.care.dialog.common.views.FabCallback
import kotlinx.coroutines.launch
import java.util.*
import kotlin.concurrent.schedule

//todo why does viewmodel reset after change

class DialogViewModelUsingUsecases(
    private val getName: GetPatientNameUseCaseUsingRepository,
    private val getAvailableScreens: GetAvailableScreensUseCaseUsingRepository
) : ViewModel(), DialogViewModel {

    override val bottomText: MutableLiveData<String> = MutableLiveData("")
    override val isNextButtonVisible: MutableLiveData<Boolean> = MutableLiveData(false)
    override val fabType: MutableLiveData<FabType> = MutableLiveData(FabType.NEXT)

    private val fetchedName: MutableLiveData<String> = MutableLiveData(DIALOG_MOCK_NAME)
    private val currentScreen: MutableLiveData<DialogRoutes> = MutableLiveData(DialogRoutes.INTRO)
    private val fetchedAvailableScreens: MutableLiveData<IntArray> =
        MutableLiveData(intArrayOf(0, 0, 0))

    override fun updateDataBasedOnRoute(type: DialogRoutes) {
        currentScreen.apply {
            value = type
        }
        handleRouteEvents()
    }

    private fun handleRouteEvents() {
        when (currentScreen.value) {
            DialogRoutes.INTRO -> {
                bottomText.apply {
                    value =
                        "Welkom, patient. Mijn naam is Pepper en ik zal het gesprek van mijn collega overnemen."
                }
                setupNextButton()
            }
            DialogRoutes.ID -> {
                bottomText.apply {
                    value = "Om verder te gaan met het proces, heb ik uw ID nodig."
                }
                setupKeyboardButton()
            }
            DialogRoutes.PATIENT -> {
                fetchPatientDetails()
                fetchUpcomingScreens()
                bottomText.apply {
                    value = "Welkom, ${fetchedName.value}. Wat fijn om u weer te zien!"
                }
                setupNextButton()
            }
            DialogRoutes.MEDICATION -> {
                fetchUpcomingScreens()
                bottomText.apply {
                    value =
                        "Goedendag, ${fetchedName.value}. Om ${DIALOG_MOCK_TIME}u moet u de volgende medicatie innemen, ${DIALOG_MOCK_MEDICATION}."
                }
                setupNextButton()
            }
            DialogRoutes.QUESTION -> {
                bottomText.apply {
                    value = "${DIALOG_MOCK_QUESTION}, ${fetchedName.value}?"
                }
                setupKeyboardButton()
            }
            DialogRoutes.GOODBYE -> {
                bottomText.apply {
                    value =
                        "Tot ziens, ${fetchedName.value}. Ik wens u nog een fijne dag tegemoet!"
                }
                setupNextButton()
            }
        }
    }

    override val nextCallback: FabCallback =
        object : FabCallback {
            override fun onClick(view: View) {
                when (fabType.value) {
                    FabType.NEXT -> {
                        when (currentScreen.value) {
                            DialogRoutes.INTRO -> {
                                view.findNavController().navigate(
                                    R.id.dialogFragment, bundleOf(
                                        Pair<String, DialogRoutes>("ROUTE_TYPE", DialogRoutes.ID)
                                    )
                                )
                            }
                            DialogRoutes.PATIENT -> {
                                when {
                                    fetchedAvailableScreens.value!![0] == 1 -> {
                                        view.findNavController().navigate(R.id.orderFragment)
                                    }
                                    fetchedAvailableScreens.value!![1] == 1 -> {
                                        view.findNavController().navigate(
                                            R.id.dialogFragment, bundleOf(
                                                Pair<String, DialogRoutes>(
                                                    "ROUTE_TYPE",
                                                    DialogRoutes.MEDICATION
                                                )
                                            )
                                        )
                                    }
                                    fetchedAvailableScreens.value!![2] == 1 -> {
                                        view.findNavController().navigate(
                                            R.id.dialogFragment, bundleOf(
                                                Pair<String, DialogRoutes>(
                                                    "ROUTE_TYPE",
                                                    DialogRoutes.QUESTION
                                                )
                                            )
                                        )
                                    }
                                    else -> {
                                        view.findNavController().navigate(R.id.feedbackFragment)
                                    }
                                }
                            }
                            DialogRoutes.MEDICATION -> {
                                when {
                                    fetchedAvailableScreens.value!![2] == 1 -> {
                                        view.findNavController().navigate(
                                            R.id.dialogFragment, bundleOf(
                                                Pair<String, DialogRoutes>(
                                                    "ROUTE_TYPE",
                                                    DialogRoutes.QUESTION
                                                )
                                            )
                                        )
                                    }
                                    else -> {
                                        view.findNavController().navigate(R.id.feedbackFragment)
                                    }
                                }
                            }
                            DialogRoutes.GOODBYE -> {
                                view.findNavController().navigate(R.id.homeFragment)
                            }
                            else -> throw IllegalStateException("Not implemented")
                        }
                    }
                    FabType.KEYBOARD -> {
                        when (currentScreen.value) {
                            DialogRoutes.ID -> {
                                createDialog(view)
                            }
                            DialogRoutes.QUESTION -> {
                                createDialog(view)
                            }
                            else -> throw IllegalStateException("Not implemented")
                        }
                    }
                }
            }
        }

    private fun setupKeyboardButton() {
        fabType.apply {
            value = FabType.KEYBOARD
        }
        isNextButtonVisible.postValue(true)
    }

    private fun setupNextButton() {
        fabType.apply {
            value = FabType.NEXT
        }
        Timer().schedule(1000) {
            isNextButtonVisible.postValue(true)
        }
    }

    private fun createDialog(view: View) {
        AwesomeDialog.build(view.context as Activity)
            .title("Klopt het onderstaande?", Typeface.DEFAULT_BOLD, R.color.black)
            .body(getDialogBody(), null, R.color.black)
            .onPositive("Ja", R.color.green) {
                navigateToDestination(view)
            }
            .onNegative("Nee", R.color.red)
    }

    private fun getDialogBody(): String {
        return when (currentScreen.value) {
            DialogRoutes.ID -> "Klopt het dat uw naam ${fetchedName.value} is?"
            DialogRoutes.QUESTION -> "Het antwoord op de vraag was: $DIALOG_MOCK_QNA"
            else -> throw IllegalStateException("Not implemented")
        }
    }

    private fun navigateToDestination(view: View) {
        when (currentScreen.value) {
            DialogRoutes.ID -> {
                view.findNavController().navigate(
                    R.id.dialogFragment, bundleOf(
                        Pair<String, DialogRoutes>("ROUTE_TYPE", DialogRoutes.PATIENT)
                    )
                )
            }
            DialogRoutes.QUESTION -> {
                view.findNavController().navigate(R.id.feedbackFragment)
            }
            else -> throw IllegalStateException("Not implemented")
        }
    }

    private fun fetchUpcomingScreens() {
        viewModelScope.launch {
            when (val result = getAvailableScreens.invoke()) {
                is AppResult.Success -> {
                    fetchedAvailableScreens.postValue(result.successData)
                }
                is AppResult.Error -> result.exception.message
            }
        }
    }

    private fun fetchPatientDetails() {
        viewModelScope.launch {
            when (val result = getName.invoke()) {
                is AppResult.Success -> {
                    fetchedName.postValue(result.successData)
                }
                is AppResult.Error -> result.exception.message
            }
        }
    }
}