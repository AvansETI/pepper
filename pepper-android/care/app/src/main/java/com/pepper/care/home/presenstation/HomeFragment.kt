package com.pepper.care.home.presenstation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pepper.care.R
import com.pepper.care.common.presentation.views.BaseFragment
import com.pepper.care.databinding.FragmentHomeBinding
import com.pepper.care.home.presenstation.viewmodels.HomeViewModel
import com.pepper.care.home.presenstation.viewmodels.HomeViewModelUsingUsecases
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.koin.androidx.viewmodel.ext.android.viewModel

@FlowPreview
@ExperimentalCoroutinesApi
class HomeFragment : BaseFragment() {

    private val viewModel: HomeViewModel by viewModel<HomeViewModelUsingUsecases>()
    private lateinit var viewBinding: FragmentHomeBinding

    override val navigationDestinationId: Int = R.id.homeFragment

    private fun setupDataBinding(inflater: LayoutInflater, container: ViewGroup?) {
        viewBinding = FragmentHomeBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = this@HomeFragment.viewLifecycleOwner
            viewModel = this@HomeFragment.viewModel
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