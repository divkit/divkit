package com.yandex.div.internal.widget.tabs

import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView

internal class TabsPagerAdapter<TabData : Any, TabView : Any>(
    private val pagesBinder: TabPagesBinder<TabData, TabView>,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    // Child View hierarchy state is intentionally not saved or restored; its loss is accepted.
    private val tabs: MutableList<TabData> = mutableListOf()

    fun updateData(tabs: List<TabData>) {
        this.tabs.clear()
        this.tabs.addAll(tabs)
        notifyDataSetChanged()
    }

    override fun getItemCount() = tabs.size

    override fun getItemViewType(position: Int) = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val placeholder = FrameLayout(parent.context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
            )
        }
        return object : RecyclerView.ViewHolder(placeholder) {}
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        pagesBinder.bindByAdapterRequest(holder.itemView, position, tabs[position])
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        pagesBinder.onPlaceholderRecycled(holder.itemView)
    }
}
