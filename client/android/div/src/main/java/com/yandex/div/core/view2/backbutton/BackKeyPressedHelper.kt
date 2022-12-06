// Copyright (c) 2018 Yandex LLC. All rights reserved.
// Author: Vasiliy Polikarpov <polikarpov@yandex-team.ru>
package com.yandex.div.core.view2.backbutton

import android.view.KeyEvent
import android.view.View
import com.yandex.div.core.view2.backbutton.BackKeyPressedHelper.OnBackClickListener

/**
 * This class helps to handle BACK key inside some View.
 * Set [OnBackClickListener] and call [.onKeyAction] to notify that listener
 * when a user taps BACK.
 */
internal class BackKeyPressedHelper(private val mOwnerView: View, private var mIsEnabled: Boolean) {
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
     * Enables or disables BACK key event processing.
     */
    fun setEnabled(enabled: Boolean) {
        mIsEnabled = enabled
        setupFocus()
    }

    /**
     * Sets the listener for 'BACK pressed' key event. If null, this event will be ignored and can
     * be handled by another View in the hierarchy.
     */
    fun setOnBackClickListener(onBackClickListener: OnBackClickListener?) {
        mOnBackClickListener = onBackClickListener
        setupFocus()
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
            setupFocus()
        }
    }

    /**
     * Call this from [View.onVisibilityChanged].
     */
    fun onVisibilityChanged() {
        setupFocus()
    }

    private fun setupFocus() {
        if (mOwnerView.hasWindowFocus()) {
            val enabled = mIsEnabled && mOwnerView.isShown && mOnBackClickListener != null
            val hasFocus = mOwnerView.hasFocus()
            mOwnerView.isFocusable = mIsEnabled
            mOwnerView.isFocusableInTouchMode = mIsEnabled
            if (enabled) {
                mOwnerView.requestFocus()
            } else if (hasFocus) {
                mOwnerView.rootView?.requestFocus(View.FOCUS_UP)
            }
        }
    }
}
