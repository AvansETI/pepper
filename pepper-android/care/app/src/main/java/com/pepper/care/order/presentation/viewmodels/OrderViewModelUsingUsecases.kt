package com.pepper.care.order.presentation.viewmodels

import androidx.lifecycle.ViewModel
import org.joda.time.LocalDateTime
import java.util.*

class OrderViewModelUsingUsecases : ViewModel(), OrderViewModel {
    override val orderText: String = "Het menu van ${LocalDateTime().toString("EEEE d MMMM", Locale("nl"))} is"

}