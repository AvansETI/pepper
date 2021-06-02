package com.pepper.care.order.common.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.pepper.care.common.ClickCallback
import com.pepper.care.databinding.SliderErrorItemBinding
import com.pepper.care.databinding.SliderMealItemBinding

@Suppress("UNCHECKED_CAST")
class SliderAdapter(
    private val clickCallback: ClickCallback<SliderAdapterItem>
) :
    ListAdapter<SliderAdapterItem, BaseViewHolder<*>>(
        ClickableDiffCallback()
    ) {

    private val ADAPT_ITEM_MEAL: Int = 0
    private val ADAPT_ITEM_ERROR: Int = 1

    override fun getItemViewType(position: Int): Int = when (getItem(position)) {
        is MealSliderItem -> ADAPT_ITEM_MEAL
        is ErrorSliderItem -> ADAPT_ITEM_ERROR
        else -> throw IllegalStateException("Not supported")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        return when (viewType) {
            ADAPT_ITEM_MEAL -> ViewHolder.MealSliderCard.from(parent)
            ADAPT_ITEM_ERROR ->ViewHolder.ErrorSliderCard.from(parent)
            else -> throw IllegalArgumentException()
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        when (holder) {
            is ViewHolder.MealSliderCard -> holder.bind(
                getItem(position) as MealSliderItem,
                clickCallback as ClickCallback<MealSliderItem>
            )
            is ViewHolder.ErrorSliderCard -> holder.bind(
                getItem(position) as ErrorSliderItem,
                clickCallback as ClickCallback<ErrorSliderItem>
            )
            else -> throw IllegalStateException("Not supported")
        }
    }

    sealed class ViewHolder {
        class MealSliderCard private constructor(
            private val binding: SliderMealItemBinding
        ) : BaseViewHolder<MealSliderItem>(binding.root) {

            override fun bind(
                item: MealSliderItem,
                clickCallback: ClickCallback<MealSliderItem>
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
                fun from(parent: ViewGroup): MealSliderCard {
                    val layoutInflater = LayoutInflater.from(parent.context)
                    val binding =
                        SliderMealItemBinding.inflate(layoutInflater, parent, false)

                    return MealSliderCard(binding)
                }
            }
        }

        class ErrorSliderCard private constructor(
            private val binding: SliderErrorItemBinding
        ) : BaseViewHolder<ErrorSliderItem>(binding.root) {

            override fun bind(
                item: ErrorSliderItem,
                clickCallback: ClickCallback<ErrorSliderItem>
            ) {
                binding.apply {
                    this.error = item
                    this.executePendingBindings()
                }
            }

            companion object {
                fun from(parent: ViewGroup): ErrorSliderCard {
                    val layoutInflater = LayoutInflater.from(parent.context)
                    val binding =
                        SliderErrorItemBinding.inflate(layoutInflater, parent, false)

                    return ErrorSliderCard(binding)
                }
            }
        }
    }
}

abstract class BaseViewHolder<T>(itemView: View) : RecyclerView.ViewHolder(itemView) {
    abstract fun bind(item: T, clickCallback: ClickCallback<T>)
}

class ClickableDiffCallback : DiffUtil.ItemCallback<SliderAdapterItem>() {

    @Suppress("UnusedEquals")
    override fun areItemsTheSame(
        oldItem: SliderAdapterItem,
        newItem: SliderAdapterItem
    ): Boolean {
        when (newItem.getViewType() == oldItem.getViewType()) {
            true -> {
                when (newItem.getViewType()) {
                    SliderAdapterItem.ViewTypes.MEAL -> {
                        (oldItem as MealSliderItem).id == (newItem as MealSliderItem).id
                    }
                }
            }
        }
        return false
    }

    override fun areContentsTheSame(
        oldItem: SliderAdapterItem,
        newItem: SliderAdapterItem
    ): Boolean {
        when (newItem.getViewType() == oldItem.getViewType()) {
            true -> {
                when (newItem.getViewType()) {
                    SliderAdapterItem.ViewTypes.MEAL -> {
                        (oldItem as MealSliderItem).name == (newItem as MealSliderItem).name
                                && oldItem.description == newItem.description
                                && oldItem.allergies == newItem.allergies
                                && oldItem.calories == newItem.calories
                                && oldItem.source == newItem.source
                    }
                }
            }
        }
        return false
    }
}