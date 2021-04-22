package com.pepper.care.home.presenstation.viewmodels

import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import com.pepper.care.R
import kotlinx.coroutines.launch
import org.joda.time.LocalDateTime
import java.util.*

class HomeViewModelUsingUsecases : ViewModel(), HomeViewModel {

    override val todaysDateText = "Vandaag, ${LocalDateTime().toString("EEEE d MMMM", Locale("nl"))}"

    override fun onButtonClick(view: View) {
        Log.d("ViewModel", "Button pressed")
        view.findNavController().navigate(R.id.orderFragment)
    }
}