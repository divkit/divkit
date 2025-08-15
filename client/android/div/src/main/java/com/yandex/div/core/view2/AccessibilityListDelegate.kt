package com.yandex.div.core.view2

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.Button
import androidx.core.view.AccessibilityDelegateCompat
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import androidx.core.view.children
import androidx.core.view.forEach
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerViewAccessibilityDelegate
import com.yandex.div.core.view2.backbutton.BackHandlingRecyclerView
import com.yandex.div.core.view2.backbutton.BackKeyPressedHelper
import com.yandex.div.core.view2.divs.gainAccessibilityFocus
import com.yandex.div.core.widget.DivViewWrapper
import java.lang.ref.WeakReference

/**
 * Implements accessibility logic of wrapping list control in dedicated item; when user iterates
 * over views in accessibility mode, this list treats as single element. To get into it and iterate
 * over its list items, user have to "tap" on this list. After that it can iterate only over items
 * and can't het out until back button will be pressed.
 */
internal class AccessibilityListDelegate(
    private val recyclerView: BackHandlingRecyclerView,
) : RecyclerViewAccessibilityDelegate(recyclerView) {
    private val list = arrayListOf<ViewAccessibilityState>()
    private val visibilityListener = ViewTreeObserver.OnGlobalLayoutListener {
        if (isItemsFocusActive && !recyclerView.isVisible) {
            clearItemsFocus()
        }
    }

    private var itemDelegate : AccessibilityDelegateCompat? = null
    private var isItemsFocusActive = false
        set(value) {
            if (field == value) return
            field = value
            recyclerView.forEach { it.updateItemAccessibility() }
        }

    init {
        if (recyclerView.isAttachedToWindow) {
            recyclerView.viewTreeObserver.addOnGlobalLayoutListener(visibilityListener)
        }
        recyclerView.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(view: View) {
                recyclerView.viewTreeObserver.addOnGlobalLayoutListener(visibilityListener)
            }

            override fun onViewDetachedFromWindow(view: View) {
                recyclerView.viewTreeObserver.removeOnGlobalLayoutListener(visibilityListener)
                clearItemsFocus()
            }
        })
        recyclerView.forEach { it.updateItemAccessibility() }
        recyclerView.setOnBackClickListener(object :
            BackKeyPressedHelper.OnBackClickListener {
            override fun onBackClick(): Boolean {
                return onBackPressed()
            }
        })
    }

    override fun getItemDelegate(): AccessibilityDelegateCompat {
        // This method can be called from parent's constructor, so do in-place object creation.
        return itemDelegate ?: ItemAccessibilityDelegate().apply {
            itemDelegate = this
        }
    }

    override fun onInitializeAccessibilityNodeInfo(host: View, info: AccessibilityNodeInfoCompat) {
        super.onInitializeAccessibilityNodeInfo(host, info)

        info.apply {
            className = if (isItemsFocusActive) {
                RecyclerView::class.qualifiedName
            } else {
                Button::class.qualifiedName
            }

            addAction(AccessibilityNodeInfoCompat.ACTION_CLICK)

            isClickable = true
            isImportantForAccessibility = true
            isScreenReaderFocusable = true
            recyclerView.forEach { it.updateItemAccessibility() }
        }
    }

    override fun performAccessibilityAction(host: View, action: Int, args: Bundle?): Boolean {
        val performed = when (action) {
            AccessibilityNodeInfoCompat.ACTION_CLICK -> {
                focusChildren()
                true
            }
            else -> false
        }
        return super.performAccessibilityAction(host, action, args) || performed
    }

    private fun focusChildren() {
        isItemsFocusActive = true
        recyclerView.makeInaccessibleAllOtherViews()
        recyclerView.firstChild?.unwrap.run { this?.gainAccessibilityFocus() }
    }

    private fun focusContainer() {
        recyclerView.gainAccessibilityFocus()
        clearItemsFocus()
    }

    private fun clearItemsFocus() {
        isItemsFocusActive = false
        restoreAccessibilityState()
    }

    private fun onBackPressed(): Boolean {
        if (isItemsFocusActive) {
            focusContainer()
            return true
        }
        return false
    }

    /**
     * Accessibility delegate for item of list.
     */
    inner class ItemAccessibilityDelegate : ItemDelegate(this) {

        override fun onInitializeAccessibilityNodeInfo(
            host: View,
            info: AccessibilityNodeInfoCompat
        ) {
            super.onInitializeAccessibilityNodeInfo(host, info)

            info.className = Button::class.qualifiedName
            host.updateItemAccessibility()
        }
    }

    private fun View.updateItemAccessibility() {
        importantForAccessibility = if (isItemsFocusActive) {
            View.IMPORTANT_FOR_ACCESSIBILITY_YES
        } else {
            View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS
        }
    }

    private fun ViewGroup.makeInaccessibleAllOtherViews() {
        val parent = this.parent as? ViewGroup ?: return
        if (this == parent.rootView) {
            return
        }
        parent.children.forEach { child ->
            if (child != this && child.importantForAccessibility !=
                    View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS) {
                list.add(
                    ViewAccessibilityState(
                        WeakReference(child),
                        child.importantForAccessibility
                    )
                )
                child.importantForAccessibility =
                    View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS
            }
        }
        parent.makeInaccessibleAllOtherViews()
    }

    private fun restoreAccessibilityState() {
        list.forEach {
            it.view.get()?.importantForAccessibility = it.accessibilityState
        }
        list.clear()
    }

    private val View.unwrap: View
        get() = if (this is DivViewWrapper) {
            this.child ?: this
        } else {
            this
        }

    private val ViewGroup.firstChild: View?
        // We find the first child by placement like accessibility service does.
        // Cause [ViewGroup.getChildAt(0)] doesn't return the first element on the screen.
        get() = children.minWithOrNull(compareBy(View::getTop, View::getLeft))

    private class ViewAccessibilityState(val view: WeakReference<View>, val accessibilityState: Int)
}
