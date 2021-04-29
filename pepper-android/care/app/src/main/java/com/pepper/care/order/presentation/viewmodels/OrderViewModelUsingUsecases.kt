package com.pepper.care.order.presentation.viewmodels

import android.util.Log
import android.view.View
import androidx.lifecycle.*
import androidx.navigation.findNavController
import com.pepper.care.R
import com.pepper.care.common.AppResult
import com.pepper.care.common.ClickCallback
import com.pepper.care.common.entities.InformUserRecyclerItem
import com.pepper.care.common.entities.PlatformMealsResponse
import com.pepper.care.common.entities.RecyclerAdapterItem
import com.pepper.care.order.common.usecases.GetPlatformMealsUseCaseUsingRepository
import kotlinx.coroutines.launch
import org.joda.time.LocalDateTime
import java.util.*

class OrderViewModelUsingUsecases(
    private val getPlatformMealsUseCaseUsingRepository: GetPlatformMealsUseCaseUsingRepository
) : ViewModel(), OrderViewModel {

    override val orderText: String =
        "Het maaltijd menu, ${LocalDateTime().toString("EEEE d MMMM", Locale("nl"))}"

    override val meal: MutableLiveData<PlatformMealsResponse> = MutableLiveData()
    override val recyclerList = MutableLiveData<List<RecyclerAdapterItem>>(emptyList())
    override val recyclerVisibility: MutableLiveData<Boolean> = MutableLiveData(false)
    override val progressVisibility: MutableLiveData<Boolean> = MutableLiveData(true)
    override val recyclerSpanCount: MutableLiveData<Int> = MutableLiveData(3)

    override fun onStart() {
        showProgressView(true)
        viewModelScope.launch {
            when (val result = getPlatformMealsUseCaseUsingRepository.invoke()) {
                is AppResult.Success -> {
                    if (result.successData.isNotEmpty()) recyclerList.value =
                        result.successData else recyclerList.value =
                        listOf(InformUserRecyclerItem(InformUserRecyclerItem.InformText.NO_MEALS_RESULTS_FOUND))
                    showProgressView(false)
                }
                is AppResult.Error -> {
                    recyclerList.value =
                        listOf(InformUserRecyclerItem(InformUserRecyclerItem.InformText.INTERNET_ERROR))
                    result.exception.message
                    showProgressView(false)
                }
            }
        }
    }

    private fun showProgressView(boolean: Boolean) {
        updateSpanCount()
        this@OrderViewModelUsingUsecases.progressVisibility.apply { value = boolean }
        this@OrderViewModelUsingUsecases.recyclerVisibility.apply { value = !boolean }
    }

    private fun updateSpanCount() {
        this@OrderViewModelUsingUsecases.recyclerSpanCount.apply {
            value = calculateSpanCount()
        }
    }

    private fun calculateSpanCount(): Int {
        return when {
            recyclerList.value!!.size == 2 -> 2
            recyclerList.value!!.size <= 1 -> 1
            else -> 3
        }
    }

    override fun onBackPress(view: View) {
        view.findNavController().popBackStack()
    }

    override val adapterClickedListener: ClickCallback<RecyclerAdapterItem> =
        object : ClickCallback<RecyclerAdapterItem> {

            override fun onClicked(view: View, item: RecyclerAdapterItem) {
                when (item.getViewType()) {
                    RecyclerAdapterItem.ViewTypes.MEAL -> {
                        Log.d(OrderViewModelUsingUsecases::class.simpleName, "Clicked on Item")

                        this@OrderViewModelUsingUsecases.meal.apply {
                            value = item as PlatformMealsResponse
                        }

                        view.findNavController().navigate(R.id.orderViewMealFragment)
                    }
                    RecyclerAdapterItem.ViewTypes.INFORM -> {
                        Log.d(OrderViewModelUsingUsecases::class.simpleName, "Clicked on Item!")
                    }
                }
            }
        }
}