package com.pepper.care.home.presenstation.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import org.joda.time.LocalDateTime
import java.util.*

class HomeViewModelUsingUsecases  : ViewModel(), HomeViewModel {

    override val todaysDateText = "Vandaag, ${LocalDateTime().toString("EEEE d MMMM", Locale("nl"))}"

    override fun onButtonClick() {
        Log.d("ViewModel", "Button pressed")
    }
}