package com.pepper.care.common.presentation.views

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.pepper.care.common.ClickCallback
import com.pepper.care.common.entities.InformUserRecyclerItem
import com.pepper.care.common.entities.PlatformMealsResponse
import com.pepper.care.common.entities.RecyclerAdapterItem
import com.pepper.care.databinding.AdapterItemInformBinding
import com.pepper.care.databinding.AdapterItemMealBinding

@Suppress("UNCHECKED_CAST")
class UniversalRecyclerAdapter(
    private val clickCallback: ClickCallback<RecyclerAdapterItem>
) :
    ListAdapter<RecyclerAdapterItem, BaseViewHolder<*>>(
        ClickableDiffCallback()
    ) {

    private val ADAPT_ITEM_MEAL: Int = 0
    private val ADAPT_ITEM_INFORM: Int = 1

    override fun getItemViewType(position: Int): Int = when (getItem(position)) {
        is PlatformMealsResponse -> ADAPT_ITEM_MEAL
        is InformUserRecyclerItem -> ADAPT_ITEM_INFORM
        else -> throw IllegalStateException("Not supported")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        return when (viewType) {
            ADAPT_ITEM_MEAL -> {
                ViewHolder.MealView.from(parent)
            }
            ADAPT_ITEM_INFORM -> {
                ViewHolder.InformView.from(parent)
            }
            else -> throw IllegalArgumentException()
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        when (holder) {
            is ViewHolder.MealView -> holder.bind(
                getItem(position) as PlatformMealsResponse,
                clickCallback as ClickCallback<PlatformMealsResponse>
            )
            is ViewHolder.InformView -> holder.bind(
                getItem(position) as InformUserRecyclerItem,
                clickCallback as ClickCallback<InformUserRecyclerItem>
            )
            else -> throw IllegalStateException("Not supported")
        }
    }

    sealed class ViewHolder {

        class MealView private constructor(
            private val binding: AdapterItemMealBinding
        ) : BaseViewHolder<PlatformMealsResponse>(binding.root) {

            override fun bind(
                item: PlatformMealsResponse,
                clickCallback: ClickCallback<PlatformMealsResponse>
            ) {
                binding.apply {
                    this.meal = item
                    this.executePendingBindings()
                }

                binding.clickableCard.setOnClickListener {
                    clickCallback.onClicked(binding.root, item)
                }
            }

            companion object {
                fun from(parent: ViewGroup): MealView {
                    val layoutInflater = LayoutInflater.from(parent.context)
                    val binding =
                        AdapterItemMealBinding.inflate(layoutInflater, parent, false)

                    return MealView(binding)
                }
            }
        }

        class InformView private constructor(
            private val binding: AdapterItemInformBinding
        ) : BaseViewHolder<InformUserRecyclerItem>(binding.root) {

            override fun bind(
                item: InformUserRecyclerItem,
                clickCallback: ClickCallback<InformUserRecyclerItem>
            ) {
                binding.apply {
                    this.inform = item
                    this.executePendingBindings()
                }
            }

            companion object {
                fun from(parent: ViewGroup): InformView {
                    val layoutInflater = LayoutInflater.from(parent.context)
                    val binding =
                        AdapterItemInformBinding.inflate(layoutInflater, parent, false)

                    return InformView(binding)
                }
            }
        }
    }

}

abstract class BaseViewHolder<T>(itemView: View) : RecyclerView.ViewHolder(itemView) {
    abstract fun bind(item: T, clickCallback: ClickCallback<T>)
}

/**
 * Callback for calculating the diff between two non-null items in a list.
 *
 * Used by ListAdapter to calculate the minimum number of changes between and old list and a new
 * list that's been passed to `submitList`.
 */
class ClickableDiffCallback : DiffUtil.ItemCallback<RecyclerAdapterItem>() {

    override fun areItemsTheSame(
        oldItem: RecyclerAdapterItem,
        newItem: RecyclerAdapterItem
    ): Boolean {
        when (newItem.getType() == oldItem.getType()) {
            true -> {
                when (newItem.getType()) {
                    RecyclerAdapterItem.ViewTypes.MEAL -> {
                        (oldItem as PlatformMealsResponse).id == (newItem as PlatformMealsResponse).id
                    }
                    RecyclerAdapterItem.ViewTypes.INFORM -> {
                        (oldItem as InformUserRecyclerItem).informType == (newItem as InformUserRecyclerItem).informType
                    }
                }
            }
        }
        return false
    }

    override fun areContentsTheSame(
        oldItem: RecyclerAdapterItem,
        newItem: RecyclerAdapterItem
    ): Boolean {
        when (newItem.getType() == oldItem.getType()) {
            true -> {
                when (newItem.getType()) {
                    RecyclerAdapterItem.ViewTypes.MEAL -> {
                        (oldItem as PlatformMealsResponse).name == (newItem as PlatformMealsResponse).name
                                && oldItem.description == newItem.description
                                && oldItem.type == newItem.type
                                && oldItem.allergies == newItem.allergies
                                && oldItem.calories == newItem.calories
                                && oldItem.source == newItem.source
                    }
                    RecyclerAdapterItem.ViewTypes.INFORM -> {
                        (oldItem as InformUserRecyclerItem).informType == (newItem as InformUserRecyclerItem).informType
                    }
                }
            }
        }
        return false
    }
}
