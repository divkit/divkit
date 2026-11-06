package com.yandex.div.core.view2

import android.view.View
import android.view.accessibility.AccessibilityEvent
import androidx.core.view.AccessibilityDelegateCompat
import androidx.core.view.ViewCompat
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat.AccessibilityActionCompat
import com.yandex.div.core.CompositeDisposable
import com.yandex.div.core.Disposable
import com.yandex.div.core.view2.divs.DivActionBinder
import com.yandex.div.internal.core.ExpressionSubscriber
import com.yandex.div.internal.widget.AccessibilityMenuAction
import com.yandex.div.internal.widget.SwitchView
import com.yandex.div.internal.widget.slider.SliderView
import com.yandex.div.json.expressions.Expression
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivAccessibility
import com.yandex.div2.DivAccessibilityMenuItem
import com.yandex.div2.DivBase
import com.yandex.div2.DivInput
import com.yandex.div2.DivSlider
import com.yandex.div2.DivSwitch
import com.yandex.div2.DivVisibility

internal class AccessibilityMenuBinding(
    private val target: View,
    private val data: DivBase,
    private val resolver: ExpressionResolver,
    private val divView: Div2View,
    private val actionBinder: DivActionBinder,
) {
    private var active = true
    private var delegate: AccessibilityMenuDelegate? = null
    private val menus = mutableListOf<BoundMenu>()

    fun bind(subscriber: ExpressionSubscriber) {
        data.accessibility?.let { addMenu(it, secondary = false) }
        if (data is DivSlider) {
            data.secondaryValueAccessibility?.let { addMenu(it, secondary = true) }
        }
        if (menus.isEmpty()) {
            return
        }
        if (target !is SliderView) {
            val original: AccessibilityDelegateCompat = ViewCompat.getAccessibilityDelegate(target)
                ?: (target as? SwitchView)?.createDefaultAccessibilityDelegate()
                ?: AccessibilityDelegateCompat()
            delegate = AccessibilityMenuDelegate(
                original,
            ).also { ViewCompat.setAccessibilityDelegate(target, it) }
        }
        val subscriptions: AccessibilityMenuSubscriptions =
            AccessibilityMenuSubscriptions(target, data, resolver, ::invalidate)
        subscriber.addSubscription(Disposable {
            if (!active) {
                return@Disposable
            }
            active = false
            subscriptions.close()
            if (target is SliderView) {
                publishSliderMenu(target, emptyList(), secondary = false)
                publishSliderMenu(target, emptyList(), secondary = true)
            } else {
                delegate?.commands = emptyList()
                if (ViewCompat.getAccessibilityDelegate(target) === delegate) {
                    ViewCompat.setAccessibilityDelegate(target, delegate?.original)
                }
            }
        })
        subscriptions.start()
    }

    private fun invalidate() {
        menus.forEach { menu ->
            if (target is SliderView) {
                publishSliderMenu(target, createCommands(menu), secondary = menu.secondary)
            } else {
                delegate?.commands = createCommands(menu)
                target.sendAccessibilityEvent(AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED)
            }
        }
    }

    private fun addMenu(accessibility: DivAccessibility, secondary: Boolean) {
        val menu: List<DivAccessibilityMenuItem> = accessibility.menu.orEmpty()
        if (menu.isEmpty()) {
            return
        }
        // ViewCompat.addAccessibilityAction registers on a View, not on the slider's virtual
        // thumb nodes. Build the same commands here and publish them on the actual target node.
        // generateViewId uses only the low 24 bits. Android treats IDs with a zero high byte as
        // legacy action bit flags and rejects multi-bit values. We choose 0x02 as the high byte
        // to avoid framework IDs (0x01) and app resource IDs (0x7f); Android does not require 0x02.
        val ids: List<Int> = menu.map { View.generateViewId() or 0x02000000 }
        menus.add(BoundMenu(accessibility, secondary = secondary, ids))
    }

    private fun createCommands(menu: BoundMenu): List<AccessibilityMenuAction> {
        return menu.accessibility.menu.orEmpty().mapIndexed { index, item ->
            createCommand(menu.accessibility, item, menu.ids[index])
        }
    }

    private fun createCommand(
        accessibility: DivAccessibility?,
        item: DivAccessibilityMenuItem,
        id: Int,
    ): AccessibilityMenuAction {
        return AccessibilityMenuAction(
            AccessibilityActionCompat(id, item.label.evaluate(resolver)),
            isAvailable = { active && isAvailable(accessibility, item) },
        ) { perform(accessibility, item) }
    }

    private fun perform(accessibility: DivAccessibility?, item: DivAccessibilityMenuItem): Boolean {
        if (!active || !target.isShown || !target.isEnabled) {
            return false
        }
        if (!isAvailable(accessibility, item)) {
            return false
        }
        actionBinder.handleAccessibilityClick(
            target, item.actions, resolver, divView,
        )
        return true
    }

    private fun isAvailable(accessibility: DivAccessibility?, item: DivAccessibilityMenuItem): Boolean {
        if (data.visibility.evaluate(resolver) != DivVisibility.VISIBLE) {
            return false
        }
        val enabled: Boolean = when (data) {
            is DivSlider -> data.isEnabled.evaluate(resolver)
            is DivSwitch -> data.isEnabled.evaluate(resolver)
            is DivInput -> data.isEnabled.evaluate(resolver)
            else -> true
        }
        if (!enabled || target.isExcluded() || target.hasSuppressingAncestor()) {
            return false
        }
        return accessibility?.mode?.evaluate(resolver) != DivAccessibility.Mode.EXCLUDE &&
            data.accessibility?.mode?.evaluate(resolver) != DivAccessibility.Mode.EXCLUDE &&
            item.actions.any { it.isEnabled.evaluate(resolver) }
    }

    private fun publishSliderMenu(
        slider: SliderView,
        commands: List<AccessibilityMenuAction>,
        secondary: Boolean,
    ) {
        if (secondary) {
            slider.a11yHelper.setMenuSecondary(commands)
        } else {
            slider.a11yHelper.setMenu(commands)
        }
    }

    private class BoundMenu(
        val accessibility: DivAccessibility,
        val secondary: Boolean,
        val ids: List<Int>,
    )
}

private class AccessibilityMenuSubscriptions(
    private val target: View,
    private val data: DivBase,
    private val resolver: ExpressionResolver,
    private val onInvalidate: () -> Unit,
) : Disposable {
    private val subscriptions = CompositeDisposable()
    private var started = false
    private var closed = false
    private val onExpressionChanged: (Any) -> Unit = { invalidate() }
    private val attachListener = object : View.OnAttachStateChangeListener {
        override fun onViewAttachedToWindow(view: View) = invalidate()
        override fun onViewDetachedFromWindow(view: View) = Unit
    }

    fun start() {
        if (started || closed) {
            return
        }
        started = true
        target.addOnAttachStateChangeListener(attachListener)
        data.accessibility?.let { observeMenu(it) }
        if (data is DivSlider) {
            data.secondaryValueAccessibility?.let { observeMenu(it) }
            if (data.accessibility?.menu.isNullOrEmpty()) {
                data.accessibility?.mode?.let {
                    subscriptions.add(it.observe(resolver, onExpressionChanged))
                }
            }
        }
        val isEnabled: Expression<Boolean>? = when (data) {
            is DivSlider -> data.isEnabled
            is DivSwitch -> data.isEnabled
            is DivInput -> data.isEnabled
            else -> null
        }
        isEnabled?.let { subscriptions.add(it.observe(resolver, onExpressionChanged)) }
        invalidate()
    }

    fun invalidate() {
        if (!started || closed) {
            return
        }
        onInvalidate()
    }

    override fun close() {
        if (closed) {
            return
        }
        closed = true
        subscriptions.close()
        target.removeOnAttachStateChangeListener(attachListener)
    }

    private fun observeMenu(accessibility: DivAccessibility) {
        val menu: List<DivAccessibilityMenuItem> = accessibility.menu.orEmpty()
        if (menu.isEmpty()) {
            return
        }
        menu.forEach { item ->
            subscriptions.add(item.label.observe(resolver, onExpressionChanged))
            item.actions.forEach {
                subscriptions.add(it.isEnabled.observe(resolver, onExpressionChanged))
            }
        }
        accessibility.mode?.let { subscriptions.add(it.observe(resolver, onExpressionChanged)) }
    }
}

private fun View.hasSuppressingAncestor(): Boolean {
    val ancestor: View = parent as? View ?: return false
    if (ancestor.visibility != View.VISIBLE || ViewCompat.isScreenReaderFocusable(ancestor) ||
        ancestor.importantForAccessibility == View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS) {
        return true
    }
    return ancestor.hasSuppressingAncestor()
}

private fun View.isExcluded(): Boolean {
    if (importantForAccessibility == View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS) {
        return true
    }
    return (parent as? View)?.isExcluded() == true
}
