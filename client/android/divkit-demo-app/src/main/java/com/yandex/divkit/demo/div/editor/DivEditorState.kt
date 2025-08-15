package com.yandex.divkit.demo.div.editor

import com.yandex.div2.DivData
import org.json.JSONObject

sealed class DivEditorState {

    object InitialState : DivEditorState()

    object LoadingState : DivEditorState()

    data class FailedState(
        val message: String
    ) : DivEditorState()

    data class DivReceivedState(
        val divDataList: List<DivData>,
        val rawDiv: JSONObject,
        val isSingleCard: Boolean
    ) : DivEditorState()
}
