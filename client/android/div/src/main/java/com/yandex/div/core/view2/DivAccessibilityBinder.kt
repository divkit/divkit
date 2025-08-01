package com.yandex.div.core.view2

import android.os.Build
import android.view.View
import androidx.core.view.AccessibilityDelegateCompat
import androidx.core.view.ViewCompat
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import com.yandex.div.core.annotations.Mockable
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.dagger.ExperimentFlag
import com.yandex.div.core.experiments.Experiment.ACCESSIBILITY_ENABLED
import com.yandex.div.core.util.AccessibilityStateProvider
import com.yandex.div.core.view2.backbutton.BackHandlingRecyclerView
import com.yandex.div.internal.core.ExpressionSubscriber
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div.json.expressions.equalsToConstant
import com.yandex.div.json.expressions.isConstantOrNull
import com.yandex.div2.DivAccessibility
import com.yandex.div2.DivBase
import com.yandex.div2.DivContainer
import com.yandex.div2.DivGallery
import com.yandex.div2.DivGifImage
import com.yandex.div2.DivImage
import com.yandex.div2.DivInput
import com.yandex.div2.DivSelect
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

    fun bind(
        view: View,
        newDiv: DivBase,
        oldDiv: DivBase?,
        resolver: ExpressionResolver,
        subscriber: ExpressionSubscriber,
    ) {
        if (newDiv.accessibility == null && oldDiv?.accessibility == null) {
            // Shortcut for empty accessibility binding
            if (enabled) {
                view.applyMode()
            }
            return
        }

        view.bindType(newDiv, oldDiv)
        view.bindDescriptionAndHint(newDiv, oldDiv, resolver, subscriber)
        view.bindMode(newDiv, oldDiv, resolver, subscriber)
        view.bindStateDescription(newDiv, oldDiv, resolver, subscriber)
        //TODO: bind 'muteAfterAction' property
    }

    // region Type

    private fun View.bindType(newDiv: DivBase, oldDiv: DivBase?) {
        if (!accessibilityStateProvider.isAccessibilityEnabled(context)) return

        if (oldDiv != null && newDiv.accessibility?.type == oldDiv.accessibility?.type) return
        applyType(newDiv, newDiv.accessibility?.type)
    }

    private fun View.applyType(divBase: DivBase, accessibilityType: DivAccessibility.Type? = null) {
        val type = accessibilityType ?: DivAccessibility.Type.AUTO
        getAccessibilityDelegate(this, type.toAccessibilityType(divBase))?.let {
            ViewCompat.setAccessibilityDelegate(this, it)
        }
    }

    private fun getAccessibilityDelegate(view: View, type: AccessibilityType): AccessibilityDelegateCompat? {
        if (type == AccessibilityType.LIST && view is BackHandlingRecyclerView) {
            return AccessibilityListDelegate(view)
        }

        val className = type.toClassName
        val heading = type == AccessibilityType.HEADER
        val autoClassName = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) view.accessibilityClassName else null
        if ((className.isEmpty() || className == autoClassName) && !heading) return null

        return object : AccessibilityDelegateCompat() {
            override fun onInitializeAccessibilityNodeInfo(host: View, info: AccessibilityNodeInfoCompat) {
                super.onInitializeAccessibilityNodeInfo(host, info)
                if (className.isNotEmpty()) {
                    info.className = className
                }
                info.isHeading = heading
            }
        }
    }

    private fun DivAccessibility.Type.toAccessibilityType(div: DivBase): AccessibilityType {
        return when (this) {
            DivAccessibility.Type.AUTO -> when {
                div is DivInput -> AccessibilityType.EDIT_TEXT
                div is DivText -> AccessibilityType.TEXT
                div is DivTabs -> AccessibilityType.TAB_WIDGET
                div is DivSelect -> AccessibilityType.SELECT
                div is DivSlider -> AccessibilityType.SLIDER
                div is DivImage -> AccessibilityType.IMAGE
                div is DivGifImage -> AccessibilityType.IMAGE
                div is DivGallery && div.accessibility?.description != null -> AccessibilityType.PAGER
                div is DivContainer -> AccessibilityType.CONTAINER
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
            DivAccessibility.Type.RADIO -> AccessibilityType.RADIO_BUTTON
            DivAccessibility.Type.CHECKBOX -> AccessibilityType.CHECK_BOX
        }
    }

    private val AccessibilityType.toClassName: String get() {
        return when (this) {
            AccessibilityType.NONE -> ""
            AccessibilityType.BUTTON -> "android.widget.Button"
            AccessibilityType.EDIT_TEXT -> "android.widget.EditText"
            AccessibilityType.HEADER -> ""
            AccessibilityType.IMAGE -> "android.widget.ImageView"
            AccessibilityType.LIST -> ""
            AccessibilityType.PAGER -> "androidx.viewpager.widget.ViewPager"
            AccessibilityType.SLIDER -> ""
            AccessibilityType.SELECT -> "android.widget.Spinner"
            AccessibilityType.TAB_WIDGET -> "android.widget.TabWidget"
            AccessibilityType.TEXT -> "android.widget.TextView"
            AccessibilityType.CHECK_BOX -> "android.widget.CheckBox"
            AccessibilityType.RADIO_BUTTON -> "android.widget.RadioButton"
            AccessibilityType.CONTAINER -> "android.view.ViewGroup"
        }
    }

    // endregion

    // region Description and  Hint

    private fun View.bindDescriptionAndHint(
        newDiv: DivBase,
        oldDiv: DivBase?,
        resolver: ExpressionResolver,
        subscriber: ExpressionSubscriber
    ) {
        val newDescription = newDiv.accessibility?.description
        val newHint = newDiv.accessibility?.hint
        if (newDescription.equalsToConstant(oldDiv?.accessibility?.description) &&
            newHint.equalsToConstant(oldDiv?.accessibility?.hint)) {
            return
        }

        applyDescriptionAndHint(newDescription?.evaluate(resolver), newHint?.evaluate(resolver))

        if (newDescription.isConstantOrNull() && newHint.isConstantOrNull()) return

        val callback = { _: Any ->
            applyDescriptionAndHint(newDescription?.evaluate(resolver), newHint?.evaluate(resolver))
            applyMode(newDiv.accessibility?.mode?.evaluate(resolver))
        }
        subscriber.addSubscription(newDescription?.observe(resolver, callback))
        subscriber.addSubscription(newHint?.observe(resolver, callback))
    }

    private fun View.applyDescriptionAndHint(description: String?, hint: String?) {
        contentDescription = when {
            description == null -> hint
            hint == null -> description
            description == hint -> description
            else -> "$description\n$hint"
        }
    }

    // endregion

    // region Mode

    private fun View.bindMode(
        newDiv: DivBase,
        oldDiv: DivBase?,
        resolver: ExpressionResolver,
        subscriber: ExpressionSubscriber
    ) {
        if (!enabled) return

        val newMode = newDiv.accessibility?.mode
        if (newMode.equalsToConstant(oldDiv?.accessibility?.mode)) return

        applyMode(newMode?.evaluate(resolver))

        if (newMode.isConstantOrNull()) return

        subscriber.addSubscription(newMode?.observe(resolver) { applyMode(it) })
    }

    private fun View.applyMode(mode: DivAccessibility.Mode? = null) {
        ViewCompat.setScreenReaderFocusable(this, mode == DivAccessibility.Mode.MERGE)
        importantForAccessibility = when {
            mode == DivAccessibility.Mode.EXCLUDE -> View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS
            !contentDescription.isNullOrBlank() -> View.IMPORTANT_FOR_ACCESSIBILITY_YES
            else -> View.IMPORTANT_FOR_ACCESSIBILITY_AUTO
        }
    }

    // endregion

    // region State description

    private fun View.bindStateDescription(
        newDiv: DivBase,
        oldDiv: DivBase?,
        resolver: ExpressionResolver,
        subscriber: ExpressionSubscriber
    ) {
        val newStateDescription = newDiv.accessibility?.stateDescription
        if (newStateDescription.equalsToConstant(oldDiv?.accessibility?.stateDescription)) return

        applyStateDescription(newStateDescription?.evaluate(resolver))

        if (newStateDescription.isConstantOrNull()) return

        subscriber.addSubscription(newStateDescription?.observe(resolver) { applyStateDescription(it) })
    }

    private fun View.applyStateDescription(stateDescription: String?) =
        ViewCompat.setStateDescription(this, stateDescription)

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
        RADIO_BUTTON,
        CHECK_BOX,
        CONTAINER,
    }
}
