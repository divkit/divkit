package com.yandex.divkit.demo.div.editor.list

import androidx.recyclerview.widget.RecyclerView
import com.yandex.div.DivDataTag
import com.yandex.div.core.view2.Div2View
import com.yandex.div2.DivPatch
import com.yandex.divkit.demo.utils.applyPatchByConfig
import com.yandex.divkit.demo.utils.setDataByConfig

class DivEditorViewHolder(val div2View: Div2View) : RecyclerView.ViewHolder(div2View) {

    fun setData(divItem: DivEditorItem) {
        div2View.setDataByConfig(divItem.data, DivDataTag(divItem.id), null)
    }

    fun applyPatch(patch: DivPatch, errorCallback: () -> Unit) {
        div2View.applyPatchByConfig(patch) {
            if (!it) errorCallback()
        }
    }
}