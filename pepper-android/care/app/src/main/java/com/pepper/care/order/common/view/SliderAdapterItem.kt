package com.pepper.care.order.common.view

abstract class SliderAdapterItem {

    enum class ViewTypes {
        MEAL, ERROR
    }

    open fun getViewType() : ViewTypes {
        return ViewTypes.MEAL
    }
}

data class MealSliderItem(
    val id: Int,
    val name: String,
    val description: String,
    val allergies: String,
    val calories: Int,
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
        INTERNET_ERROR("Kon geen verbinding maken met het Pepper Care platform."),
        NO_MEALS_RESULTS_FOUND("Er zijn geen maaltijden gevonden.")
    }

    override fun getViewType() : ViewTypes {
        return ViewTypes.ERROR
    }
}