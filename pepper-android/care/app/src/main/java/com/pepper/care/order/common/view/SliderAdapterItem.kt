package com.pepper.care.order.common.view

import com.pepper.care.core.services.platform.entities.Allergy

abstract class SliderAdapterItem {

    enum class ViewTypes {
        MEAL, ERROR
    }

    open fun getViewType() : ViewTypes {
        return ViewTypes.MEAL
    }
}

data class MealSliderItem(
    val id: String,
    val name: String,
    val description: String,
    val allergies: Set<Allergy>,
    val calories: String,
    val source: String,
    var isFavorite: Boolean
) : SliderAdapterItem() {

    override fun getViewType(): ViewTypes {
        return ViewTypes.MEAL
    }
}

data class ErrorSliderItem(
    var errorType: ErrorText
) : SliderAdapterItem() {

    enum class ErrorText(val text: String) {
        NO_MEALS_RESULTS_FOUND("Geen maaltijden gevonden.")
    }

    override fun getViewType() : ViewTypes {
        return ViewTypes.ERROR
    }
}