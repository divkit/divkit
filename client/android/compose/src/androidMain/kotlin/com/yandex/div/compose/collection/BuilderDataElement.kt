package com.yandex.div.compose.collection

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import com.yandex.div.compose.context.LocalDivViewContext
import com.yandex.div.compose.di.DivLocalComponent
import com.yandex.div.compose.utils.reportError
import com.yandex.div.compose.utils.reportWarning
import com.yandex.div.internal.core.INDEX_VARIABLE_NAME
import com.yandex.div.json.typeMismatch
import org.json.JSONArray
import org.json.JSONObject

/**
 * An element of a builder's `data` with the scope its prototypes are resolved in: the element is
 * exposed under the builder's `data_element_name`, and its position as `index`.
 */
internal class BuilderDataElement(
    val key: String,
    val constants: Map<String, Any>,
    val component: DivLocalComponent,
)

/**
 * Builds a value from every element of [elements] with [build], called in the scope of that
 * element. Elements that are not dictionaries are reported and skipped, as are elements for which
 * [build] returns `null`. The index shadows a data element named `index`, as in the View renderer;
 * the warning names the builder by its JSON [propertyName]. Each builder type calls this from its
 * own entry point, which passes its fields.
 */
@Composable
internal inline fun <T : Any> observeBuilderDataElements(
    propertyName: String,
    elements: JSONArray,
    dataElementName: String,
    parent: DivLocalComponent,
    crossinline build: @Composable (BuilderDataElement) -> T?,
): List<T> {
    if (dataElementName == INDEX_VARIABLE_NAME) {
        reportWarning(
            "$propertyName data element name 'index' is reserved for the element index"
        )
    }
    val elementKeys = remember(elements) {
        List(elements.length()) { index -> "${elements.get(index)}:$index" }
    }
    val results = ArrayList<T>(elements.length())
    for (index in 0 until elements.length()) {
        val elementKey = elementKeys[index]
        val result = key(elementKey) {
            val element = observeBuilderDataElement(
                parent = parent,
                dataElementName = dataElementName,
                element = elements.get(index),
                index = index,
                elementKey = elementKey,
            )
            if (element == null) null else build(element)
        }
        if (result != null) {
            results += result
        }
    }
    return results
}

@Composable
private fun observeBuilderDataElement(
    parent: DivLocalComponent,
    dataElementName: String,
    element: Any,
    index: Int,
    elementKey: String,
): BuilderDataElement? {
    if (element !is JSONObject) {
        reportError(remember(index, element) { typeMismatch(index, element) })
        return null
    }
    val viewContext = LocalDivViewContext.current
    val constants = remember(elementKey, dataElementName) {
        mapOf(dataElementName to element, INDEX_VARIABLE_NAME to index.toLong())
    }
    val component = remember(viewContext, parent, constants) {
        viewContext.createLocalComponent(
            parent,
            ItemVariableProvider(parent.variableProvider, constants),
        )
    }
    DisposableEffect(component) {
        onDispose { component.expressionResolver.clearObservers() }
    }
    return remember(component) { BuilderDataElement(elementKey, constants, component) }
}
