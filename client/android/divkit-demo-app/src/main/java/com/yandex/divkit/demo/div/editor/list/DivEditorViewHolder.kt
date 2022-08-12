package com.yandex.divkit.demo.div.editor.list

import androidx.recyclerview.widget.RecyclerView
import com.yandex.div.DivDataTag
import com.yandex.div.core.view2.Div2View
import com.yandex.div2.DivPatch

class DivEditorViewHolder(val div2View: Div2View) : RecyclerView.ViewHolder(div2View) {

    fun setData(divItem: DivEditorItem) = div2View.setData(divItem.data, DivDataTag(divItem.id))

    fun applyPatch(patch: DivPatch): Boolean = div2View.applyPatch(patch)

}