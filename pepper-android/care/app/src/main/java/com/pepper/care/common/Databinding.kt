package com.pepper.care.common

import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.pepper.care.R
import com.pepper.care.feedback.entities.FeedbackEntity

@BindingAdapter("items")
fun setItems(
    recyclerView: RecyclerView,
    items: List<Any>?
) {
    if (recyclerView.adapter != null) {
        val adapter = (recyclerView.adapter as ListAdapter<Any, RecyclerView.ViewHolder>)
        adapter.submitList(items)
    }
}

@BindingAdapter("imageSource")
fun setImageUrlSource(imageView: ImageView, url: String?) {
    if (url == null) return
    Glide.with(imageView.context)
        .load(url)
        .into(imageView)
}

@BindingAdapter("isVisible")
fun setIsVisible(view: View, isVisible: Boolean) {
    view.isVisible = isVisible
}

@BindingAdapter("setSpanCount")
fun setSpanCount(recyclerView: RecyclerView, amount: Int) {
    if (recyclerView.layoutManager != null) {
        recyclerView.layoutManager = GridLayoutManager(recyclerView.context, amount)
        Log.d("Databinding", "Recyclerview updated, columns $amount")
    }
}

@BindingAdapter("colorCardSource")
fun setColorSource(imageView: ImageView, type: FeedbackEntity.FeedbackMessage) {
    val image = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(image)
    canvas.drawColor(
        imageView.resources.getColor(
                        when(type){
                            FeedbackEntity.FeedbackMessage.BAD -> R.color.red
                            FeedbackEntity.FeedbackMessage.OKAY -> R.color.yellow
                            FeedbackEntity.FeedbackMessage.GOOD -> R.color.green
                      }
            , imageView.context.theme)
    )

    Glide.with(imageView.context)
        .load(image)
        .into(imageView)
}

@BindingAdapter("iconSource")
fun setIconSource(imageView: ImageView, type: FeedbackEntity.FeedbackMessage) {
    Glide.with(imageView.context)
        .load(when(type){
            FeedbackEntity.FeedbackMessage.BAD -> R.drawable.ic_feedback_bad
            FeedbackEntity.FeedbackMessage.OKAY -> R.drawable.ic_feedback_okay
            FeedbackEntity.FeedbackMessage.GOOD -> R.drawable.ic_feedback_good
        })
        .into(imageView)
}