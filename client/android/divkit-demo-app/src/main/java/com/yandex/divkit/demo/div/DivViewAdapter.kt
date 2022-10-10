package com.yandex.divkit.demo.div

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yandex.div.core.Div2Context
import com.yandex.div.core.view2.Div2View
import com.yandex.div.json.map
import com.yandex.div2.DivData
import org.json.JSONArray
import org.json.JSONObject

class DivViewAdapter(
    val context: Div2Context
) : RecyclerView.Adapter<DivViewHolder>() {

    private val items = mutableListOf<DivItem>()

    private var resetCount = 0
    private var startAnimationCount = 0
    private var stopAnimationCount = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        DivViewHolder(
            Div2View(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                )
            }
        )

    override fun onBindViewHolder(holder: DivViewHolder, position: Int) {
        with(items[position]) {
            holder.setData(this)
        }
        if (resetCount >= 0) {
            resetCount--
            holder.view.resetToInitialState()
        }
        if (startAnimationCount >= 0) {
            startAnimationCount--
            (holder.view as? Div2View)?.startDivAnimation()
        }
        if (stopAnimationCount >= 0) {
            stopAnimationCount--
            (holder.view as? Div2View)?.stopDivAnimation()
        }
    }

    override fun onViewRecycled(holder: DivViewHolder) {
        holder.view.cleanup()
        super.onViewRecycled(holder)
    }

    override fun getItemCount() = items.size

    fun addFromJson(json: JSONObject) {
        items.addFirst(divItemFromJson(json))
        notifyItemInserted(0)
    }

    fun add(divData: DivData) {
        items.addFirst(DivItem(divData))
        notifyItemInserted(0)
    }

    fun remove(itemId: Int) {
        items.removeAt(itemId)
        notifyItemRemoved(itemId)
    }

    fun removeAll() {
        val itemCount = items.size
        items.clear()
        notifyItemRangeRemoved(0, itemCount)
    }

    fun resetAll() {
        resetCount = items.size
        notifyDataSetChanged()
    }

    fun startAllAnimation() {
        startAnimationCount = items.size
        notifyDataSetChanged()
    }

    fun stopAllAnimation() {
        stopAnimationCount = items.size
        notifyDataSetChanged()
    }

    fun clear() {
        items.clear()
    }

    fun toJson() = JSONArray().also { array ->
        items.forEach {
            array.put(it.data.writeToJSON())
        }
    }

    fun fromJson(array: JSONArray) {
        items.clear()
        addArrayFromJson(array)
        notifyDataSetChanged()
    }

    private fun divItemFromJson(json: JSONObject): DivItem {
        return when {
            json.isDiv2Data() -> DivItem(json.asDiv2DataWithTemplates())
            else -> DivItem(json.asDiv2Data())
        }
    }

    fun addArrayFromJson(array: JSONArray) {
        array.map { divItemFromJson(it as JSONObject) }
            .forEach { items.add(it) }
    }

    private fun <T> MutableList<T>.addFirst(t: T) = add(0, t)
}
