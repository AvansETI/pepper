package com.pepper.care.order.events.viewmeal.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pepper.care.R
import com.pepper.care.common.presentation.views.BaseFragment
import com.pepper.care.databinding.FragmentHomeBinding
import com.pepper.care.databinding.FragmentOrderDetailBinding
import com.pepper.care.order.presentation.viewmodels.OrderViewModel
import com.pepper.care.order.presentation.viewmodels.OrderViewModelUsingUsecases
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.koin.androidx.viewmodel.ext.android.viewModel

@FlowPreview
@ExperimentalCoroutinesApi
class OrderViewMealFragment : BaseFragment() {

    private val viewModel: OrderViewModel by viewModel<OrderViewModelUsingUsecases>()
    private lateinit var viewBinding: FragmentOrderDetailBinding

    override val navigationDestinationId: Int = R.id.orderViewMealFragment

    private fun setupDataBinding(inflater: LayoutInflater, container: ViewGroup?) {
        viewBinding = FragmentOrderDetailBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = this@OrderViewMealFragment.viewLifecycleOwner
            viewModel = this@OrderViewMealFragment.viewModel
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setupDataBinding(inflater, container)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}