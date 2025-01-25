package com.yandex.div.core.view2.reuse

import android.view.View
import com.yandex.div.core.actions.closeKeyboard
import com.yandex.div.core.dagger.DivViewScope
import com.yandex.div.core.downloader.PersistentDivDataObserver
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.divs.widgets.DivInputView
import java.lang.ref.WeakReference
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

    fun inputFocusChanged(tag: Any?, view: DivInputView, focused: Boolean) {
        when {
            changingState -> return
            focused -> {
                focusedInputTag = tag
                lastFocused = WeakReference(view)
            }
            !focused -> {
                focusedInputTag = null
                lastFocused = null
            }
        }
    }

    fun requestFocusIfNeeded(view: View) {
        if (view.tag != null && view.tag == focusedInputTag && changingState) {
            divDataChangedObserver.focusRequestedDuringChangeState = true
            view.requestFocus()
        }
    }

    fun removeFocusFromFocusedInput() {
        lastFocused?.get()?.also {
            it.clearFocus()
            it.closeKeyboard()
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

    companion object {
        private var lastFocused: WeakReference<View>? = null
    }
}
