package com.yandex.divkit.demo.div.editor.list

import com.yandex.div2.DivData
import java.util.UUID

data class DivEditorItem(
    val data: DivData,
    val id: String = UUID.randomUUID().toString()
)