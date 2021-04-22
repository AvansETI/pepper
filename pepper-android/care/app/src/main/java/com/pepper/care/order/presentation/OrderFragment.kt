package com.pepper.care.order.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pepper.care.R
import com.pepper.care.common.presentation.views.BaseFragment
import com.pepper.care.databinding.FragmentHomeBinding
import com.pepper.care.databinding.FragmentOrderBinding
import com.pepper.care.home.presenstation.viewmodels.HomeViewModel
import com.pepper.care.home.presenstation.viewmodels.HomeViewModelUsingUsecases
import com.pepper.care.order.presentation.viewmodels.OrderViewModel
import com.pepper.care.order.presentation.viewmodels.OrderViewModelUsingUsecases
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.koin.androidx.viewmodel.ext.android.viewModel

@FlowPreview
@ExperimentalCoroutinesApi
class OrderFragment : BaseFragment() {

    private val viewModel: OrderViewModel by viewModel<OrderViewModelUsingUsecases>()
    private lateinit var viewBinding: FragmentOrderBinding

    override val navigationDestinationId: Int = R.id.orderFragment

    private fun setupDataBinding(inflater: LayoutInflater, container: ViewGroup?) {
        viewBinding = FragmentOrderBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = this@OrderFragment.viewLifecycleOwner
            viewModel = this@OrderFragment.viewModel
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
        bindToEvents()
    }

    private fun bindToEvents() {

    }
}