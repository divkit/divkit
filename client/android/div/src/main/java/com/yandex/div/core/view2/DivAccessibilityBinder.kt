package com.yandex.div.core.view2

import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import com.yandex.div.core.annotations.Mockable
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.dagger.ExperimentFlag
import com.yandex.div.core.experiments.Experiment.ACCESSIBILITY_ENABLED
import com.yandex.div2.DivAccessibility
import com.yandex.div2.DivBase
import com.yandex.div2.DivContainer
import com.yandex.div2.DivGifImage
import com.yandex.div2.DivImage
import com.yandex.div2.DivInput
import com.yandex.div2.DivSeparator
import com.yandex.div2.DivTabs
import com.yandex.div2.DivText
import javax.inject.Inject

/**
 * Contains logic for applying [DivAccessibility]-related properties.
 */
@DivScope
@Mockable
internal class DivAccessibilityBinder @Inject constructor(
    @ExperimentFlag(ACCESSIBILITY_ENABLED) val enabled: Boolean
) {
    fun bindAccessibilityMode(
        view: View,
        divView: Div2View,
        mode: DivAccessibility.Mode,
    ) {
        if (!enabled) {
            return
        }
        val parentMode = (view.parent as? View)?.let {
            divView.getPropagatedAccessibilityMode(it)
        }

        if (parentMode != null) {
            val propagatedMode = getPropagatedMode(parentMode, mode)
            view.applyAccessibilityMode(
                propagatedMode,
                divView,
                isDescendant = parentMode == propagatedMode
            )
        } else {
            view.applyAccessibilityMode(mode, divView, isDescendant = false)
        }
    }

    fun bindType(view: View, type: DivAccessibility.Type) {
        if (!enabled) {
            return
        }
        val originalDelegate = ViewCompat.getAccessibilityDelegate(view)

        val accessibilityDelegate = AccessibilityDelegateWrapper(
            originalDelegate,
            initializeAccessibilityNodeInfo = { _, info -> info?.bindType(type) }
        )

        ViewCompat.setAccessibilityDelegate(view, accessibilityDelegate)
    }

    fun bindTypeAutomatically(view: View, div: DivBase) {
        if (!enabled) {
            return
        }
        if (div.actsAsButton) {
            bindType(view, DivAccessibility.Type.BUTTON)
            return
        }

        when (div) {
            is DivImage -> bindType(view, DivAccessibility.Type.IMAGE)
            is DivInput -> bindType(view, DivAccessibility.Type.EDIT_TEXT)
            is DivGifImage -> bindType(view, DivAccessibility.Type.IMAGE)
            is DivText -> bindType(view, DivAccessibility.Type.TEXT)
            is DivTabs -> bindType(view, DivAccessibility.Type.TAB_BAR)
            else -> bindType(view, DivAccessibility.Type.NONE)
        }
    }

    private val DivBase.actsAsButton: Boolean
        get() = when (this) {
            is DivContainer -> action != null ||
                    !actions.isNullOrEmpty() ||
                    !longtapActions.isNullOrEmpty() ||
                    !doubletapActions.isNullOrEmpty()
            is DivImage -> action != null ||
                    !actions.isNullOrEmpty() ||
                    !longtapActions.isNullOrEmpty() ||
                    !doubletapActions.isNullOrEmpty()
            is DivGifImage -> action != null ||
                    !actions.isNullOrEmpty() ||
                    !longtapActions.isNullOrEmpty() ||
                    !doubletapActions.isNullOrEmpty()
            is DivSeparator -> action != null ||
                    !actions.isNullOrEmpty() ||
                    !longtapActions.isNullOrEmpty() ||
                    !doubletapActions.isNullOrEmpty()
            is DivText -> action != null ||
                    !actions.isNullOrEmpty() ||
                    !longtapActions.isNullOrEmpty() ||
                    !doubletapActions.isNullOrEmpty()
            else -> false
        }

    /**
     * Sets [AccessibilityNodeInfoCompat]'s className so that TalkBack could
     * properly recognize role of View provided by [DivAccessibility.Type].
     * For example, if [type] is [DivAccessibility.Type.BUTTON], TalkBack announces View as "Button".
     */
    private fun AccessibilityNodeInfoCompat.bindType(type: DivAccessibility.Type) {
        this.className = when (type) {
            DivAccessibility.Type.NONE -> ""
            DivAccessibility.Type.BUTTON -> "android.widget.Button"
            DivAccessibility.Type.IMAGE -> "android.widget.ImageView"
            DivAccessibility.Type.TEXT -> "android.widget.TextView"
            DivAccessibility.Type.EDIT_TEXT -> "android.widget.EditText"
            DivAccessibility.Type.HEADER -> "android.widget.TextView"
            DivAccessibility.Type.TAB_BAR -> "android.widget.TabWidget"
            else -> ""
        }

        if (DivAccessibility.Type.HEADER == type) {
            this.isHeading = true
        }
    }

    private val DivAccessibility.Mode.priority
        get() = when (this) {
            DivAccessibility.Mode.EXCLUDE -> 0
            DivAccessibility.Mode.MERGE -> 1
            DivAccessibility.Mode.DEFAULT -> 2
        }

    private fun getPropagatedMode(
        parentMode: DivAccessibility.Mode,
        mode: DivAccessibility.Mode
    ): DivAccessibility.Mode {
        return if (parentMode.priority < mode.priority) parentMode else mode
    }

    private fun View.applyAccessibilityMode(
        mode: DivAccessibility.Mode,
        divView: Div2View,
        isDescendant: Boolean
    ) {
        when (mode) {
            DivAccessibility.Mode.MERGE -> {
                importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_YES
                if (!isDescendant) {
                    isFocusable = true
                } else {
                    setActionable(false)
                }
            }
            DivAccessibility.Mode.EXCLUDE -> {
                importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS
                isFocusable = false
            }
            DivAccessibility.Mode.DEFAULT -> {
                importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_AUTO
                isFocusable = true
            }
        }
        divView.setPropagatedAccessibilityMode(this, mode)
    }

    private fun View.setActionable(actionable: Boolean) {
        isClickable = actionable
        isLongClickable = actionable
        isFocusable = actionable
    }
}
