package com.pepper.care.common.entities

abstract class RecyclerAdapterItem {

    enum class ViewTypes {
        MEAL, INFORM
    }

    open fun getViewType() : ViewTypes {
        return ViewTypes.MEAL
    }
}

data class InformUserRecyclerItem(
    var informType: InformText
) : RecyclerAdapterItem() {

    enum class InformText(val text: String) {
        INTERNET_ERROR("Kon geen verbinding maken met het Pepper Care platform."),
        NO_MEALS_RESULTS_FOUND("Er zijn geen maaltijden gevonden.")
    }

    override fun getViewType() : ViewTypes {
        return ViewTypes.INFORM
    }
}