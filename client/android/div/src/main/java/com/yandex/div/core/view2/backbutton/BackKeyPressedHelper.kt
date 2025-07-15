package com.yandex.div.core.view2.backbutton

import android.view.KeyEvent
import android.view.View
import com.yandex.div.core.view2.backbutton.BackKeyPressedHelper.OnBackClickListener
import com.yandex.div.core.view2.divs.gainAccessibilityFocus

/**
 * This class helps to handle BACK key inside some View.
 * Set [OnBackClickListener] and call [.onKeyAction] to notify that listener
 * when a user taps BACK.
 */
internal class BackKeyPressedHelper(private val mOwnerView: View) {
    private var mOnBackClickListener: OnBackClickListener? = null

    /**
     * Back pressed listener
     */
    interface OnBackClickListener {
        /**
         * Back pressed
         *
         * @return True if the event is handled and should not be passed further.
         */
        fun onBackClick(): Boolean
    }

    /**
     * Sets the listener for 'BACK pressed' key event. If null, this event will be ignored and can
     * be handled by another View in the hierarchy.
     */
    fun setOnBackClickListener(onBackClickListener: OnBackClickListener?) {
        mOnBackClickListener = onBackClickListener
        setupAccessibilityFocus()
    }

    /**
     * The owner view should call this method to forward BACK event to listener.
     * Generally, if method returns false, super method should be called.
     *
     * @return True if this event is handled and should not be passed further.
     */
    fun onKeyAction(keyCode: Int, event: KeyEvent): Boolean {
        if (mOnBackClickListener != null && keyCode == KeyEvent.KEYCODE_BACK) {
            if (event.action == KeyEvent.ACTION_DOWN && event.repeatCount == 0) {
                val state = mOwnerView.keyDispatcherState
                state?.startTracking(event, this)
                return true
            } else if (event.action == KeyEvent.ACTION_UP) {
                val dispatcherState = mOwnerView.keyDispatcherState
                dispatcherState?.handleUpEvent(event)
                if (event.isTracking && !event.isCanceled) {
                    return mOnBackClickListener!!.onBackClick()
                }
            }
        }
        return false
    }

    /**
     * Call this from [View.onWindowFocusChanged].
     */
    fun onWindowFocusChanged(hasWindowFocus: Boolean) {
        if (hasWindowFocus) {
            setupAccessibilityFocus()
        }
    }

    /**
     * Call this from [View.onVisibilityChanged].
     */
    fun onVisibilityChanged() {
        setupAccessibilityFocus()
    }

    private fun setupAccessibilityFocus() {
        if (mOnBackClickListener == null || !mOwnerView.hasWindowFocus()) return

        mOwnerView.apply {
            when {
                isShown -> gainAccessibilityFocus()
                isAccessibilityFocused -> rootView?.gainAccessibilityFocus()
            }
        }
    }
}
