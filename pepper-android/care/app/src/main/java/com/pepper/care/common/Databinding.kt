package com.pepper.care.common

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

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