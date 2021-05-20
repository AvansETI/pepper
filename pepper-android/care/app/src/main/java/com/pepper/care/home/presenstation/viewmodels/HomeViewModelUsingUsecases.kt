package com.pepper.care.home.presenstation.viewmodels

import android.util.Log
import android.view.View
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import com.pepper.care.R
import com.pepper.care.dialog.DialogRoutes
import kotlinx.coroutines.launch
import org.joda.time.LocalDateTime
import java.util.*

class HomeViewModelUsingUsecases : ViewModel(), HomeViewModel {

    override fun goToNextScreen(view: View) {
        view.findNavController().navigate(
            R.id.dialogFragment, bundleOf(
                Pair<String, DialogRoutes>("ROUTE_TYPE", DialogRoutes.INTRO)
            )
        )
    }

    override val standbyText: String = "Pepper staat momenteel op standby..."
    override val buttonText: String = "Druk hier om het gesprek te starten"
}