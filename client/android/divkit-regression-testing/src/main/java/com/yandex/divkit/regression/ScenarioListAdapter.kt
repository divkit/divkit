package com.yandex.divkit.regression

import android.app.Activity
import android.graphics.Color
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.yandex.div.core.view2.Div2View
import com.yandex.divkit.regression.data.Priority
import com.yandex.divkit.regression.data.Scenario

internal class ScenarioListAdapter(
    private val activity: Activity,
    private val div2ViewCreator: Div2ViewCreator
) :
    ListAdapter<Scenario, ScenarioViewHolder>(Callback) {
    enum class ScenarioViewType {
        HEADER, LIST
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScenarioViewHolder {
        val item = div2ViewCreator.createDiv2View(
            activity,
            "application/scenario_list_item.json",
            parent,
            ScenarioLogDelegate.Stub,
        )
        item.layoutParams = RecyclerView.LayoutParams(MATCH_PARENT, WRAP_CONTENT)

        when(viewType) {
            ScenarioViewType.HEADER.ordinal -> {
                item.setBackgroundColor(Color.TRANSPARENT)
                item.setBackgroundResource(R.drawable.rounded_top_corners)
            }
            ScenarioViewType.LIST.ordinal -> {
                item.setBackgroundResource(0)
                item.setBackgroundColor(ContextCompat.getColor(activity, R.color.white))
            }
        }

        return ScenarioViewHolder(item) { position ->
            ScenarioActivity.launch(parent.context, position)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when(position) {
            0 -> ScenarioViewType.HEADER.ordinal
            else -> ScenarioViewType.LIST.ordinal
        }
    }

    override fun onBindViewHolder(holder: ScenarioViewHolder, position: Int) {
        val scenario = getItem(position)
        holder.bind(scenario)
    }

    object Callback : DiffUtil.ItemCallback<Scenario>() {
        override fun areItemsTheSame(oldItem: Scenario, newItem: Scenario): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Scenario, newItem: Scenario): Boolean {
            return oldItem == newItem
        }
    }
}

internal class ScenarioViewHolder(
    private val divView: Div2View,
    private val clickListener: (position: Int) -> Unit
) : RecyclerView.ViewHolder(divView) {

    private fun priorityColor(priority: Priority): String {
        return when (priority) {
            Priority.blocker -> "#D098D3"
            Priority.critical -> "#f1dff2"
            else -> "#f1f1f1"
        }
    }

    fun bind(scenario: Scenario) {
        divView.setVariable("title", scenario.title)
        divView.setVariable("background_color", priorityColor(scenario.priority))
        itemView.setOnClickListener {
            clickListener(scenario.position)
        }
    }
}
