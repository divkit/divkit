package com.yandex.div.markdown

import android.content.Context
import android.view.View
import android.widget.TextView
import com.yandex.div.core.extension.DivExtensionHandler
import com.yandex.div.core.view2.Div2View
import com.yandex.div.internal.core.ExpressionSubscriber
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivBase
import com.yandex.div2.DivText
import io.noties.markwon.Markwon
import io.noties.markwon.MarkwonPlugin
import io.noties.markwon.core.CorePlugin

internal const val EXTENSION_ID = "markdown"

/**
 * Supports Markdown for DivText.
 *
 * Example of usage:
 * ``` (json)
 * {
 *   "type": "text",
 *   "text": "# Header 1\n## Header 2",
 *   "extensions": [
 *     {
 *       "id": "markdown"
 *     }
 *   ]
 * }
 * ```
 */
public class DivMarkdownExtensionHandler(
    context: Context,
    markwonPlugins: List<MarkwonPlugin> = emptyList(),
) : DivExtensionHandler {
    private val markwon: Markwon = Markwon.builder(context)
        .usePlugins(markwonPlugins)
        .usePlugin(CorePlugin.create())
        .build()

    override fun matches(div: DivBase): Boolean {
        return div.extensions?.any { it.id == EXTENSION_ID } ?: false
    }

    override fun bindView(
        divView: Div2View,
        expressionResolver: ExpressionResolver,
        view: View,
        div: DivBase
    ) {
        if (view is TextView && view is ExpressionSubscriber && div is DivText) {
            view.subscribeOnText(div, divView.expressionResolver)
        }
    }

    override fun unbindView(
        divView: Div2View,
        expressionResolver: ExpressionResolver,
        view: View,
        div: DivBase
    ): Unit = Unit

    private fun <T> T.subscribeOnText(
        div: DivText,
        resolver: ExpressionResolver,
    ) where T : TextView, T : ExpressionSubscriber {
        addSubscription(div.text.observeAndGet(resolver) { applyMarkdownText(it) })
    }

    private fun TextView.applyMarkdownText(text: String) {
        val node = markwon.parse(text)
        val markdown = markwon.render(node)
        markwon.setParsedMarkdown(this, markdown)
    }
}
