package com.pepper.care.common.presentation.views

import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect

abstract class BaseFragment : Fragment {

    constructor() : super()

    constructor(@LayoutRes contentLayoutId: Int) : super(contentLayoutId)

    abstract val navigationDestinationId: Int
        @IdRes get

    private fun isCurrentDestination() =
        findNavController().currentDestination?.id == navigationDestinationId

    protected fun popBackStack() {
        if (isCurrentDestination()) findNavController().popBackStack()
    }

    protected fun popBackStack(@IdRes destinationId: Int, inclusive: Boolean) {
        if (isCurrentDestination()) findNavController().popBackStack(destinationId, inclusive)
    }

    protected fun navigateUp() {
        if (isCurrentDestination()) findNavController().navigateUp()
    }

    protected fun NavDirections.navigateToIt() {
        if (isCurrentDestination()) findNavController().navigate(this)
    }

    protected fun <T> Flow<T>.collectInLifecycleScope(action: suspend (T) -> Unit) {
        lifecycleScope.launchWhenStarted {
            collect { value ->
                action(value)
            }
        }
    }

    protected fun <T> LiveData<T>.observeInLifecycleScope(observer: (T) -> Unit) {
        observe(viewLifecycleOwner, observer)
    }
}