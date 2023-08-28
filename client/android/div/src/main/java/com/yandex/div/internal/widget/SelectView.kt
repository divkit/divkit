package com.yandex.div.internal.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.AbsListView.CHOICE_MODE_SINGLE
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.appcompat.widget.ListPopupWindow
import com.yandex.div.R
import com.yandex.div.core.annotations.Mockable
import com.yandex.div.core.view2.divs.dpToPx

private const val POPUP_ITEM_HEIGHT = 48

internal open class SelectView constructor(context: Context) : SuperLineHeightTextView(context) {
    init {
        this.setOnClickListener {
            popupWindow.resetPosition()
            popupWindow.show()
        }
    }

    var onItemSelectedListener: ((Int) -> Unit)? = null

    @SuppressLint("RestrictedApi")
    private val popupWindow = PopupWindow(context).apply {
        isModal = true
        anchorView = this@SelectView

        setOnItemClickListener { _, _, position, _ ->
            sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_SELECTED)
            onItemSelectedListener?.invoke(position)
            dismiss()
        }

        setOverlapAnchor(true)
        setBackgroundDrawable(ColorDrawable(Color.WHITE))
        setAdapter(adapter)
    }

    fun setItems(items: List<String>) {
        popupWindow.adapter.setItems(items)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        if (changed && popupWindow.isShowing) {
            popupWindow.show()
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()

        if (popupWindow.isShowing) {
            popupWindow.dismiss()
        }
    }

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        super.onVisibilityChanged(changedView, visibility)

        if (visibility != View.VISIBLE && popupWindow.isShowing) {
            popupWindow.dismiss()
        }
    }

    override fun onInitializeAccessibilityNodeInfo(info: AccessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(info)

        info.setCanOpenPopup(true)

        // When hint is shown, it's treated as text
        // We need to ignore hint here so it won't be announced twice
        info.text = text
    }

    @Mockable
    private class PopupWindow @JvmOverloads constructor(
        private val context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = R.attr.listPopupWindowStyle
    ) : ListPopupWindow(context, attrs, defStyleAttr) {
        val adapter = PopupAdapter()

        // Prevent ListPopupWindow from setting incorrect inputMethodMode when scrolled
        override fun getInputMethodMode(): Int = INPUT_METHOD_NEEDED

        override fun show() {
            // Popup height is calculated incorrectly on ListView creation
            // Call show() twice on listView creation to force recalculation
            if (listView == null) {
                super.show()
                // Make items reported as selectable for accessibility
                listView?.choiceMode = CHOICE_MODE_SINGLE
            }

            super.show()
        }

        fun resetPosition() {
            listView?.setSelectionAfterHeaderView()
        }

        inner class PopupAdapter : BaseAdapter() {
            private var items: List<String> = emptyList()

            fun setItems(newItems: List<String>) {
                items = newItems
                notifyDataSetChanged()
            }

            override fun getCount(): Int = items.size
            override fun getItem(position: Int): String = items[position]
            override fun getItemId(position: Int): Long = position.toLong()
            override fun getView(position: Int, convertView: View?, parent: ViewGroup?): TextView {
                val textView = (convertView ?: createView()) as TextView
                return textView.apply { text = getItem(position) }
            }

            private fun createView(): TextView {
                return TextView(context, null, android.R.attr.spinnerDropDownItemStyle).apply {
                    ellipsize = TextUtils.TruncateAt.END
                    isSingleLine = true
                    layoutParams = ViewGroup.LayoutParams(
                        MATCH_PARENT,
                        POPUP_ITEM_HEIGHT.dpToPx(resources.displayMetrics)
                    )
                    textAlignment = TextView.TEXT_ALIGNMENT_VIEW_START
                }
            }
        }
    }
}
