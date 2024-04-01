package com.yandex.div.core.view2.reuse

import android.view.View
import com.yandex.div.core.dagger.DivViewScope
import com.yandex.div.core.downloader.PersistentDivDataObserver
import com.yandex.div.core.view2.Div2View
import javax.inject.Inject

@DivViewScope
internal class InputFocusTracker @Inject constructor(
    div2View: Div2View,
) {
    private var focusedInputTag: Any? = null
    private var changingState = false
    private val divDataChangedObserver = InputFocusPersistentDivDataChangedObserver()

    init {
        div2View.addPersistentDivDataObserver(divDataChangedObserver)
    }

    fun inputFocusChanged(tag: Any, focused: Boolean) {
        if (changingState) return
        if (focused) {
            focusedInputTag = tag
        } else {
            if (focusedInputTag == tag) focusedInputTag = null
        }
    }

    fun requestFocusIfNeeded(view: View) {
        if (view.tag != null && view.tag == focusedInputTag && changingState) {
            divDataChangedObserver.focusRequestedDuringChangeState = true
            view.requestFocus()
        }
    }

    inner class InputFocusPersistentDivDataChangedObserver: PersistentDivDataObserver {
        var focusRequestedDuringChangeState = false

        override fun onBeforeDivDataChanged() {
            changingState = true
            focusRequestedDuringChangeState = false
        }

        override fun onAfterDivDataChanged() {
            changingState = false
            // in this case input with saved tag doesn't exist in current layout
            if (!focusRequestedDuringChangeState) focusedInputTag = null
        }
    }
}
