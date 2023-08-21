package com.yandex.div.core.view2.divs

import android.widget.TextView
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.expression.variables.TwoWayStringVariableBinder
import com.yandex.div.core.util.toIntSafely
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.DivTypefaceResolver
import com.yandex.div.core.view2.DivViewBinder
import com.yandex.div.core.view2.animations.DEFAULT_CLICK_ANIMATION
import com.yandex.div.core.view2.divs.widgets.DivInputView
import com.yandex.div.core.view2.divs.widgets.DivSelectView
import com.yandex.div.core.view2.errors.ErrorCollector
import com.yandex.div.core.view2.errors.ErrorCollectors
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivInput
import com.yandex.div2.DivSelect
import javax.inject.Inject

@DivScope
internal class DivSelectBinder @Inject constructor(
    private val baseBinder: DivBaseBinder,
    private val typefaceResolver: DivTypefaceResolver,
    private val variableBinder: TwoWayStringVariableBinder,
    private val errorCollectors: ErrorCollectors
) : DivViewBinder<DivSelect, DivSelectView> {
    override fun bindView(view: DivSelectView, div: DivSelect, divView: Div2View) {
        val oldDiv = view.div
        if (div == oldDiv) return

        val expressionResolver = divView.expressionResolver
        view.closeAllSubscription()

        val errorCollector = errorCollectors.getOrCreate(divView.dataTag, divView.divData)

        view.div = div
        if (oldDiv != null) baseBinder.unbindExtensions(view, oldDiv, divView)

        baseBinder.bindView(view, div, oldDiv, divView)

        view.apply {
            textAlignment = TextView.TEXT_ALIGNMENT_VIEW_START

            applyOptions(div, divView)
            observeVariable(div, divView, errorCollector)

            observeFontSize(div, expressionResolver)
            observeTypeface(div, expressionResolver)
            observeTextColor(div, expressionResolver)
            observeLineHeight(div, expressionResolver)

            observeHintText(div, expressionResolver)
            observeHintColor(div, expressionResolver)
        }
    }

    private fun DivSelectView.applyOptions(div: DivSelect, divView: Div2View) {
        val resolver = divView.expressionResolver

        setAnimatedTouchListener(divView, DEFAULT_CLICK_ANIMATION, null)

        val itemList = createObservedItemList(div, divView.expressionResolver)

        setItems(itemList)

        onItemSelectedListener = { position ->
            text = itemList[position]
            valueUpdater?.invoke(div.options[position].value.evaluate(resolver))
        }
    }

    private fun DivSelectView.createObservedItemList(div: DivSelect, resolver: ExpressionResolver): List<String> {
        val itemList = mutableListOf<String>()

        div.options.forEachIndexed { index, option ->
            val item = option.run { text ?: value }

            itemList.add(item.evaluate(resolver))

            item.observe(resolver) {
                itemList[index] = it
                setItems(itemList)
            }
        }

        return itemList
    }

    private fun DivSelectView.observeVariable(div: DivSelect, divView: Div2View, errorCollector: ErrorCollector) {
        val resolver = divView.expressionResolver

        val subscription = variableBinder.bindVariable(
            divView,
            div.valueVariable,
            callbacks = object : TwoWayStringVariableBinder.Callbacks {
                override fun onVariableChanged(value: String?) {
                    val matchingOptionsSequence = div.options
                        .asSequence()
                        .filter { it.value.evaluate(resolver) == value }
                        .iterator()

                    text = if (!matchingOptionsSequence.hasNext()) {
                        errorCollector.logWarning(Throwable("No option found with value = \"$value\""))

                        ""
                    } else {
                        val option = matchingOptionsSequence.next()

                        if (matchingOptionsSequence.hasNext()) {
                            errorCollector.logWarning(Throwable("Multiple options found with value = \"$value\", selecting first one"))
                        }

                        option.let { it.text ?: it.value }.evaluate(resolver)
                    }
                }

                override fun setViewStateChangeListener(valueUpdater: (String) -> Unit) {
                    this@observeVariable.valueUpdater = valueUpdater
                }
            })

        addSubscription(subscription)
    }

    private fun DivSelectView.observeFontSize(div: DivSelect, resolver: ExpressionResolver) {
        val callback = { _: Any ->
            val fontSize = div.fontSize.evaluate(resolver).toIntSafely()

            applyFontSize(fontSize, div.fontSizeUnit.evaluate(resolver))
            applyLetterSpacing(div.letterSpacing.evaluate(resolver), fontSize)
        }

        addSubscription(div.fontSize.observeAndGet(resolver, callback))
        addSubscription(div.letterSpacing.observe(resolver, callback))
        addSubscription(div.fontSizeUnit.observe(resolver, callback))
    }

    private fun DivSelectView.observeTypeface(div: DivSelect, resolver: ExpressionResolver) {
        applyTypeface(div, resolver)
        val callback = { _: Any ->  applyTypeface(div, resolver) }
        div.fontFamily?.observeAndGet(resolver, callback)?.let { addSubscription(it) }
        addSubscription(div.fontWeight.observe(resolver, callback))
    }

    private fun DivSelectView.applyTypeface(div: DivSelect, resolver: ExpressionResolver) {
        typeface = typefaceResolver.getTypeface(
            div.fontFamily?.evaluate(resolver),
            div.fontWeight.evaluate(resolver)
        )
    }

    private fun DivSelectView.observeTextColor(div: DivSelect, resolver: ExpressionResolver) {
        addSubscription(div.textColor.observeAndGet(resolver) { textColor ->
            setTextColor(textColor)
        })
    }

    private fun DivSelectView.observeLineHeight(div: DivSelect, resolver: ExpressionResolver) {
        val lineHeightExpr = div.lineHeight ?: return applyLineHeight(null, div.fontSizeUnit.evaluate(resolver))

        val callback = { _: Any ->
            val height = lineHeightExpr.evaluate(resolver)
            val unit = div.fontSizeUnit.evaluate(resolver)

            lineHeight = height.unitToPx(resources.displayMetrics, unit)
            applyLineHeight(height, unit)
        }

        addSubscription(lineHeightExpr.observeAndGet(resolver, callback))
        addSubscription(div.fontSizeUnit.observe(resolver, callback))
    }

    private fun DivSelectView.observeHintText(div: DivSelect, resolver: ExpressionResolver) {
        val hintTextExpr = div.hintText ?: return

        addSubscription(hintTextExpr.observeAndGet(resolver) { hint ->
            setHint(hint)
        })
    }

    private fun DivSelectView.observeHintColor(div: DivSelect, resolver: ExpressionResolver) {
        addSubscription(div.hintColor.observeAndGet(resolver) { hintColor ->
            setHintTextColor(hintColor)
        })
    }
}
