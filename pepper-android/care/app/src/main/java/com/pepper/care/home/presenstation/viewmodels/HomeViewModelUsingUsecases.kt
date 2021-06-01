package com.pepper.care.home.presenstation.viewmodels

import android.view.View
import androidx.lifecycle.ViewModel
import androidx.navigation.findNavController
import com.pepper.care.R

class HomeViewModelUsingUsecases : ViewModel(), HomeViewModel {
    override val standbyText: String = "Pepper staat momenteel op standby..."

    override fun onStart(view: View) {
        view.findNavController().navigate(
            R.id.orderFragment
        )
    }
}