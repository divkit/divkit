package com.yandex.divkit.demo.benchmark

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

internal class SingleViewAdapter<T : View>(
    val view: T
) : RecyclerView.Adapter<SingleViewAdapter.ViewHolder>() {

    override fun getItemCount(): Int = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = Unit

    class ViewHolder(
        view : View
    ) : RecyclerView.ViewHolder(view) {

        init {
            view.apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }
        }
    }
}
