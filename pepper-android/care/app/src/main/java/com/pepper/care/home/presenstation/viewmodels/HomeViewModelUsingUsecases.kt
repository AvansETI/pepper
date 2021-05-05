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

    override val standbyText: String = "Pepper staat momenteel op standby..."
}