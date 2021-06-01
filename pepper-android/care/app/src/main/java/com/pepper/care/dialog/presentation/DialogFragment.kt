package com.pepper.care.dialog.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pepper.care.R
import com.pepper.care.common.presentation.views.BaseFragment
import com.pepper.care.databinding.FragmentDialogBinding
import com.pepper.care.dialog.DialogRoutes
import com.pepper.care.dialog.presentation.viewmodels.DialogViewModel
import com.pepper.care.dialog.presentation.viewmodels.DialogViewModelUsingUsecases
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.koin.androidx.viewmodel.ext.android.viewModel

@FlowPreview
@ExperimentalCoroutinesApi
class DialogFragment : BaseFragment() {

    private val viewModel: DialogViewModel by viewModel<DialogViewModelUsingUsecases>()
    private lateinit var viewBinding: FragmentDialogBinding
    private lateinit var route: DialogRoutes

    override val navigationDestinationId: Int = R.id.dialogFragment

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setupDataBinding(inflater, container)
        setToolbarBackButtonVisibility(View.GONE)
        this.route = arguments?.get("ROUTE_TYPE") as DialogRoutes
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.updateDataBasedOnRoute(route)
    }

    private fun setupDataBinding(inflater: LayoutInflater, container: ViewGroup?) {
        viewBinding = FragmentDialogBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = this@DialogFragment.viewLifecycleOwner
            viewModel = this@DialogFragment.viewModel
        }
    }
}