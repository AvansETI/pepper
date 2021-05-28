package com.pepper.care.home.presenstation.viewmodels

import android.view.View
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModel
import androidx.navigation.findNavController
import com.pepper.care.R
import com.pepper.care.dialog.DialogRoutes

class HomeViewModelUsingUsecases : ViewModel(), HomeViewModel {

    override fun goToNextScreen(view: View) {
        view.findNavController().navigate(
            R.id.dialogFragment, bundleOf(
                Pair<String, DialogRoutes>("ROUTE_TYPE", DialogRoutes.INTRO)
            )
        )
    }

    override fun onCreated() {

    }

    override val standbyText: String = "Pepper staat momenteel op standby..."
    override val buttonText: String = "Druk hier om het gesprek te starten"
}