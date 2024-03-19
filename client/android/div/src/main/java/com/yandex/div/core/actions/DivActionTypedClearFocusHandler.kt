package com.yandex.div.core.actions

import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import com.yandex.div.core.view2.Div2View
import com.yandex.div2.DivActionTyped
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class DivActionTypedClearFocusHandler @Inject constructor() : DivActionTypedHandler {
    override fun handleAction(action: DivActionTyped, view: Div2View): Boolean = when (action) {
        is DivActionTyped.ClearFocus -> {
            view.clearFocus()
            closeKeyboard(view)
            true
        }

        else -> false
    }

    private fun closeKeyboard(view: Div2View) {
        val imm = ContextCompat.getSystemService(view.getContext(), InputMethodManager::class.java)
        imm?.hideSoftInputFromWindow(view.windowToken, InputMethodManager.SHOW_IMPLICIT)
    }
}
