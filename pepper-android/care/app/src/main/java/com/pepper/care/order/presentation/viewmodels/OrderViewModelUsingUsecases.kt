package com.pepper.care.order.presentation.viewmodels

import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import kotlin.collections.ArrayList

class OrderViewModelUsingUsecases(
    private val getPlatformMealsUseCaseUsingRepository: GetPlatformMealsUseCaseUsingRepository
) : ViewModel(), OrderViewModel {
    override val orderText: String = "Het maaltijd menu, ${LocalDateTime().toString("EEEE d MMMM", Locale("nl"))}"

    override val mealsList = MutableLiveData<List<RecyclerAdapterItem>>()
    override val errorList = MutableLiveData<List<RecyclerAdapterItem>>(listOf(InformUserRecyclerItem(InformUserRecyclerItem.InformText.NO_MEALS_RESULTS_FOUND)))

    override fun onStart() {
        viewModelScope.launch {
            when (val result =  getPlatformMealsUseCaseUsingRepository.invoke()) {
                is AppResult.Success -> {
                    mealsList.value = result.successData

                    mealsList.value!!.forEach {
                        it as PlatformMealsResponse
                        Log.d(OrderViewModelUsingUsecases::class.simpleName, it.name)
                    }
                }
                is AppResult.Error -> result.exception.message
            }
        }
    }

    override val adapterClickedListener: ClickCallback<RecyclerAdapterItem> =
        object : ClickCallback<RecyclerAdapterItem> {

            override fun onClicked(view: View, item: RecyclerAdapterItem) {
                when (item.getType()) {
                    RecyclerAdapterItem.ViewTypes.MEAL -> {
                        Log.d(OrderViewModelUsingUsecases::class.simpleName, "Clicked on Item!")
                        view.findNavController().navigate(R.id.homeFragment)
                    }
                    RecyclerAdapterItem.ViewTypes.INFORM -> {
                        Log.d(OrderViewModelUsingUsecases::class.simpleName, "Clicked on Item!")
                    }
                }
            }
        }
}