package com.yandex.divkit.demo.div.editor.list

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.yandex.div.core.Div2Context
import com.yandex.div.core.view2.Div2View
import com.yandex.div2.DivData
import com.yandex.div2.DivPatch

class DivEditorAdapter(val context: Div2Context) :
    ListAdapter<DivEditorItem, DivEditorViewHolder>(DIFF_CALLBACK) {

    private val viewHolderList = mutableListOf<DivEditorViewHolder>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DivEditorViewHolder {
        val holder = DivEditorViewHolder(Div2View(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        })
        viewHolderList.add(holder)
        return holder
    }

    override fun onBindViewHolder(holder: DivEditorViewHolder, position: Int) {
        holder.setData(getItem(position))
    }

    fun setList(divDataList: List<DivData>) {
        submitList(divDataList.map { DivEditorItem(it, it.logId) })
    }

    fun applyPath(divPatch: DivPatch, errorCallback: () -> Unit): Boolean {
        var resultSuccessApplied = true
        viewHolderList.forEach { holder ->
            val successApplied = holder.applyPatch(divPatch)
            if (!successApplied) {
                resultSuccessApplied = false
                errorCallback.invoke()
            }
        }
        return resultSuccessApplied
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<DivEditorItem>() {
            override fun areItemsTheSame(oldItem: DivEditorItem, newItem: DivEditorItem) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: DivEditorItem, newItem: DivEditorItem) =
                oldItem.dataContentTheSame(newItem)
        }

        @JvmStatic
        private fun DivEditorItem.dataContentTheSame(another: DivEditorItem) =
            this.data.writeToJSON().toString() == another.data.writeToJSON().toString()
    }

}