package com.yandex.divkit.demo.div.editor

import android.graphics.Bitmap
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.yandex.divkit.demo.div.editor.DivEditorScreenshot.takeScreenshot
import com.yandex.divkit.demo.div.editor.list.DivEditorAdapter

class DivEditorUi(
    private val activity: AppCompatActivity,
    private val refreshButton: FloatingActionButton,
    private val failedTextMessage: TextView,
    private val divContainer: ViewGroup,
    private val div2Recycler: RecyclerView,
    private val div2Adapter: DivEditorAdapter
) {

    var onDiv2ViewDrawnListener: ((bitmap: Bitmap) -> Unit)? = null

    private val debounceOnViewDrawObserver = DebounceOnViewDrawObserver {
        activity.takeScreenshot { screenshot: Bitmap ->
            onDiv2ViewDrawnListener?.invoke(screenshot)
        }
    }

    fun updateState(newState: DivEditorState) {
        when (newState) {
            is DivEditorState.InitialState -> {
                showInitialState()
            }
            is DivEditorState.LoadingState -> {
                showLoadingState()
            }
            is DivEditorState.DivReceivedState -> {
                showDivReceivedState(newState)
            }
            is DivEditorState.FailedState -> {
                showFailedState(newState)
            }
        }
    }

    private fun showInitialState() {
        hideAll()
    }

    private fun showLoadingState() {
        hideAll()
        refreshButton.isEnabled = false
        failedTextMessage.visibility = View.VISIBLE
        failedTextMessage.text = "Loading..."
    }

    private fun showFailedState(failedState: DivEditorState.FailedState) {
        hideAll()
        failedTextMessage.visibility = View.VISIBLE
        failedTextMessage.text = failedState.message
        debounceOnViewDrawObserver.onDraw()
    }

    private fun showDivReceivedState(state: DivEditorState.DivReceivedState) {
        hideAll()
        div2Adapter.setList(state.divDataList)
        div2Recycler.viewTreeObserver.addOnDrawListener(debounceOnViewDrawObserver)

        // Make sure onDiv2ViewDrawnListener called, even if div2View not going to be drawn.
        debounceOnViewDrawObserver.onDraw()
    }

    private fun hideAll() {
        failedTextMessage.visibility = View.GONE
        refreshButton.isEnabled = true
        divContainer.viewTreeObserver.removeOnDrawListener(debounceOnViewDrawObserver)
    }
}
