package com.yandex.div.core.view2

import android.view.View
import android.widget.ImageView
import androidx.core.view.ViewCompat
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import com.yandex.div.core.annotations.Mockable
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.dagger.ExperimentFlag
import com.yandex.div.core.experiments.Experiment.ACCESSIBILITY_ENABLED
import com.yandex.div.core.util.AccessibilityStateProvider
import com.yandex.div.core.view2.backbutton.BackHandlingRecyclerView
import com.yandex.div.core.view2.divs.widgets.DivSliderView
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivAccessibility
import com.yandex.div2.DivBase
import com.yandex.div2.DivContainer
import com.yandex.div2.DivGallery
import com.yandex.div2.DivGifImage
import com.yandex.div2.DivImage
import com.yandex.div2.DivInput
import com.yandex.div2.DivSelect
import com.yandex.div2.DivSeparator
import com.yandex.div2.DivSlider
import com.yandex.div2.DivTabs
import com.yandex.div2.DivText
import javax.inject.Inject

/**
 * Contains logic for applying [DivAccessibility]-related properties.
 */
@DivScope
@Mockable
internal class DivAccessibilityBinder @Inject constructor(
    @ExperimentFlag(ACCESSIBILITY_ENABLED) val enabled: Boolean,
    private val accessibilityStateProvider: AccessibilityStateProvider,
) {
    fun bindAccessibilityMode(
        view: View,
        divView: Div2View,
        mode: DivAccessibility.Mode?,
        divBase: DivBase,
    ) {
        if (!enabled) {
            return
        }
        val parentMode = (view.parent as? View)?.let {
            divView.getPropagatedAccessibilityMode(it)
        }

        if (parentMode != null) {
            val propagatedMode = getPropagatedMode(parentMode, mode ?: divBase.getDefaultAccessibilityMode)
            view.applyAccessibilityMode(
                propagatedMode,
                divView,
                isDescendant = parentMode == propagatedMode
            )
        } else {
            view.applyAccessibilityMode(mode ?: divBase.getDefaultAccessibilityMode, divView, isDescendant = false)
        }
    }

    fun bindType(view: View, divBase: DivBase, type: DivAccessibility.Type, resolver: ExpressionResolver) {
        if (!accessibilityStateProvider.isAccessibilityEnabled(view.context)) {
            return
        }

        val originalDelegate = ViewCompat.getAccessibilityDelegate(view)
        val accessibilityType = type.toAccessibilityType(divBase, resolver)

        val accessibilityDelegate =
            if (accessibilityType == AccessibilityType.LIST && view is BackHandlingRecyclerView) {
                AccessibilityListDelegate(view)
            } else if (originalDelegate is AccessibilityDelegateWrapper) {
                originalDelegate.apply {
                    initializeAccessibilityNodeInfo = { _, info ->
                        info?.bindType(accessibilityType)
                    }
                }
            } else {
                AccessibilityDelegateWrapper(
                    originalDelegate,
                    initializeAccessibilityNodeInfo = { _, info ->
                        info?.bindType(accessibilityType)
                    })
            }

        ViewCompat.setAccessibilityDelegate(view, accessibilityDelegate)
    }

    private val DivBase.getDefaultAccessibilityMode: DivAccessibility.Mode
        get() = when (this) {
            is DivImage -> if (accessibility == null && doubletapActions.isNullOrEmpty() &&
                actions.isNullOrEmpty() && longtapActions.isNullOrEmpty()) {
                DivAccessibility.Mode.EXCLUDE
            } else {
                DivAccessibility.Mode.DEFAULT
            }

            is DivSeparator -> if (accessibility == null && doubletapActions.isNullOrEmpty() &&
                actions.isNullOrEmpty() && longtapActions.isNullOrEmpty()) {
                DivAccessibility.Mode.EXCLUDE
            } else {
                DivAccessibility.Mode.DEFAULT
            }

            else -> DivAccessibility.Mode.DEFAULT
        }

    /**
     * Sets [AccessibilityNodeInfoCompat]'s className so that TalkBack could
     * properly recognize role of View provided by [DivAccessibility.Type].
     * For example, if [type] is [DivAccessibility.Type.BUTTON], TalkBack announces View as "Button".
     */
    private fun AccessibilityNodeInfoCompat.bindType(type: AccessibilityType) {
        this.className = when (type) {
            AccessibilityType.NONE -> ""
            AccessibilityType.BUTTON -> "android.widget.Button"
            AccessibilityType.EDIT_TEXT -> "android.widget.EditText"
            AccessibilityType.HEADER -> "android.widget.TextView"
            AccessibilityType.IMAGE -> "android.widget.ImageView"
            AccessibilityType.LIST -> ""
            AccessibilityType.PAGER -> "androidx.viewpager.widget.ViewPager"
            AccessibilityType.SLIDER -> "android.widget.SeekBar"
            AccessibilityType.SELECT -> "android.widget.Spinner"
            AccessibilityType.TAB_WIDGET -> "android.widget.TabWidget"
            AccessibilityType.TEXT -> "android.widget.TextView"
        }

        if (AccessibilityType.HEADER == type) {
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
                    isFocusable = this !is DivSliderView
                } else {
                    setActionable(false)
                }
            }
            DivAccessibility.Mode.EXCLUDE -> {
                importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS
                isFocusable = false
                if (this is DivSliderView) isFocusableInTouchMode = true
            }
            DivAccessibility.Mode.DEFAULT -> {
                importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_AUTO
                isFocusable = this !is DivSliderView
            }
        }
        divView.setPropagatedAccessibilityMode(this, mode)
    }

    private fun View.setActionable(actionable: Boolean) {
        isClickable = actionable
        isLongClickable = actionable
        isFocusable = actionable
    }

    private fun DivImage.isClickable(resolver: ExpressionResolver): Boolean =
        if (action != null && action?.isEnabled?.evaluate(resolver) == true) {
            true
        } else if (actions != null && actions?.any { it.isEnabled.evaluate(resolver) } == true) {
            true
        } else longtapActions != null && longtapActions?.any { it.isEnabled.evaluate(resolver) } == true

    private fun DivAccessibility.Type.toAccessibilityType(
        div: DivBase, resolver: ExpressionResolver
    ): AccessibilityType = when (this) {
            DivAccessibility.Type.AUTO -> when {
                div is DivInput -> AccessibilityType.EDIT_TEXT
                div is DivText -> AccessibilityType.TEXT
                div is DivTabs -> AccessibilityType.TAB_WIDGET
                div is DivSelect -> AccessibilityType.SELECT
                div is DivSlider -> AccessibilityType.SLIDER
                div is DivImage && (div.accessibility != null || div.isClickable(resolver))-> AccessibilityType.IMAGE
                div is DivGallery && div.accessibility?.description != null -> AccessibilityType.PAGER
                else -> AccessibilityType.NONE
            }
            DivAccessibility.Type.NONE -> AccessibilityType.NONE
            DivAccessibility.Type.BUTTON -> AccessibilityType.BUTTON
            DivAccessibility.Type.IMAGE -> AccessibilityType.IMAGE
            DivAccessibility.Type.TEXT -> AccessibilityType.TEXT
            DivAccessibility.Type.EDIT_TEXT -> AccessibilityType.EDIT_TEXT
            DivAccessibility.Type.HEADER -> AccessibilityType.HEADER
            DivAccessibility.Type.LIST -> AccessibilityType.LIST
            DivAccessibility.Type.SELECT -> AccessibilityType.SELECT
            DivAccessibility.Type.TAB_BAR -> AccessibilityType.TAB_WIDGET
        }

    private enum class AccessibilityType {
        NONE,
        BUTTON,
        EDIT_TEXT,
        HEADER,
        IMAGE,
        LIST,
        SLIDER,
        SELECT,
        TAB_WIDGET,
        PAGER,
        TEXT,
    }
}
