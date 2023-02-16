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
internal open class BackHandlingRecyclerView : RecyclerView, BackHandlingView {
    private lateinit var mBackKeyPressedHelper: BackKeyPressedHelper

    constructor(context: Context) : super(context) {
        mBackKeyPressedHelper = BackKeyPressedHelper(this, true)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        mBackKeyPressedHelper = BackKeyPressedHelper(this, true)
    }

    constructor(
        context: Context, attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
        mBackKeyPressedHelper = BackKeyPressedHelper(this, true)
    }

    @CallSuper
    override fun onKeyPreIme(keyCode: Int, event: KeyEvent): Boolean {
        return mBackKeyPressedHelper.onKeyAction(keyCode, event) ||
                super.onKeyPreIme(keyCode, event)
    }

    @CallSuper
    override fun onWindowFocusChanged(hasWindowFocus: Boolean) {
        super.onWindowFocusChanged(hasWindowFocus)
        mBackKeyPressedHelper.onWindowFocusChanged(hasWindowFocus)
    }

    @CallSuper
    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        // will be null when onVisibilityChanged called from super constructor
        if (this::mBackKeyPressedHelper.isInitialized) {
            mBackKeyPressedHelper.onVisibilityChanged()
        }
    }

    /**
     * @see BackKeyPressedHelper.setOnBackClickListener
     */
    override  fun setOnBackClickListener(listener: OnBackClickListener?) {
        mBackKeyPressedHelper.setOnBackClickListener(listener)
    }
}
