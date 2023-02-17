package com.yandex.div.core.view2.backbutton

import android.content.Context
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.View
import androidx.annotation.CallSuper
import androidx.recyclerview.widget.RecyclerView
import com.yandex.div.core.view2.backbutton.BackKeyPressedHelper.OnBackClickListener

/**
 * [RecyclerView] adapter to [BackKeyPressedHelper] that handles BACK key press.
 */
internal open class BackHandlingRecyclerView(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr), BackHandlingView {

    private val backKeyPressedHelper = BackKeyPressedHelper(this)

    @CallSuper
    override fun onKeyPreIme(keyCode: Int, event: KeyEvent): Boolean {
        return backKeyPressedHelper.onKeyAction(keyCode, event) ||
                super.onKeyPreIme(keyCode, event)
    }

    @CallSuper
    override fun onWindowFocusChanged(hasWindowFocus: Boolean) {
        super.onWindowFocusChanged(hasWindowFocus)
        backKeyPressedHelper.onWindowFocusChanged(hasWindowFocus)
    }

    @CallSuper
    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        backKeyPressedHelper.onVisibilityChanged()
    }

    /**
     * @see BackKeyPressedHelper.setOnBackClickListener
     */
    override  fun setOnBackClickListener(listener: OnBackClickListener?) {
        descendantFocusability = if (listener != null) FOCUS_BEFORE_DESCENDANTS else FOCUS_AFTER_DESCENDANTS
        backKeyPressedHelper.setOnBackClickListener(listener)
    }
}
