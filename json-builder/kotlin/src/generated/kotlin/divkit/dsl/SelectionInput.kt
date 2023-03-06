@file:Suppress(
    "unused",
    "UNUSED_PARAMETER",
)

package divkit.dsl

import com.fasterxml.jackson.annotation.JsonAnyGetter
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonValue
import divkit.dsl.annotation.*
import divkit.dsl.core.*
import divkit.dsl.scope.*
import kotlin.Any
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.Map

/**
 * The input method using the selection menu. The text in the input corresponds to the selected item from the menu. If the value has not yet been selected, then the value that matches the value of the text_variable variable will be taken.
 * 
 * Can be created using the method [selectionInput].
 * 
 * Required properties: `type, items`.
 */
@Generated
class SelectionInput internal constructor(
    @JsonIgnore
    val properties: Properties,
) : InputMethod {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "selection")
    )

    operator fun plus(additive: Properties): SelectionInput = SelectionInput(
        Properties(
            items = additive.items ?: properties.items,
        )
    )

    class Properties internal constructor(
        val items: Property<List<Item>>?,
    ) {
        internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
            val result = mutableMapOf<String, Any>()
            result.putAll(properties)
            result.tryPutProperty("items", items)
            return result
        }
    }

    /**
     * Selection item from the menu.
     * 
     * Can be created using the method [selectionInputItem].
     * 
     * Required properties: `text`.
     */
    @Generated
    class Item internal constructor(
        @JsonIgnore
        val properties: Properties,
    ) {
        @JsonAnyGetter
        internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(emptyMap())

        operator fun plus(additive: Properties): Item = Item(
            Properties(
                text = additive.text ?: properties.text,
                value = additive.value ?: properties.value,
            )
        )

        class Properties internal constructor(
            /**
             * The text that is displayed in the input. If the value is missing, the text from div_selection_input_item_value will appear in the input.
             */
            val text: Property<String>?,
            /**
             * Value of the item that is displayed in the selection menu.
             */
            val value: Property<String>?,
        ) {
            internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
                val result = mutableMapOf<String, Any>()
                result.putAll(properties)
                result.tryPutProperty("text", text)
                result.tryPutProperty("value", value)
                return result
            }
        }
    }

}

@Generated
fun DivScope.selectionInput(
    `use named arguments`: Guard = Guard.instance,
    items: List<SelectionInput.Item>? = null,
): SelectionInput = SelectionInput(
    SelectionInput.Properties(
        items = valueOrNull(items),
    )
)

@Generated
fun DivScope.selectionInputProps(
    `use named arguments`: Guard = Guard.instance,
    items: List<SelectionInput.Item>? = null,
) = SelectionInput.Properties(
    items = valueOrNull(items),
)

@Generated
fun TemplateScope.selectionInputRefs(
    `use named arguments`: Guard = Guard.instance,
    items: ReferenceProperty<List<SelectionInput.Item>>? = null,
) = SelectionInput.Properties(
    items = items,
)

@Generated
fun SelectionInput.override(
    `use named arguments`: Guard = Guard.instance,
    items: List<SelectionInput.Item>? = null,
): SelectionInput = SelectionInput(
    SelectionInput.Properties(
        items = valueOrNull(items) ?: properties.items,
    )
)

@Generated
fun SelectionInput.defer(
    `use named arguments`: Guard = Guard.instance,
    items: ReferenceProperty<List<SelectionInput.Item>>? = null,
): SelectionInput = SelectionInput(
    SelectionInput.Properties(
        items = items ?: properties.items,
    )
)

@Generated
fun SelectionInput.asList() = listOf(this)

/**
 * @param text The text that is displayed in the input. If the value is missing, the text from div_selection_input_item_value will appear in the input.
 * @param value Value of the item that is displayed in the selection menu.
 */
@Generated
fun DivScope.selectionInputItem(
    `use named arguments`: Guard = Guard.instance,
    text: String? = null,
    value: String? = null,
): SelectionInput.Item = SelectionInput.Item(
    SelectionInput.Item.Properties(
        text = valueOrNull(text),
        value = valueOrNull(value),
    )
)

/**
 * @param text The text that is displayed in the input. If the value is missing, the text from div_selection_input_item_value will appear in the input.
 * @param value Value of the item that is displayed in the selection menu.
 */
@Generated
fun DivScope.selectionInputItemProps(
    `use named arguments`: Guard = Guard.instance,
    text: String? = null,
    value: String? = null,
) = SelectionInput.Item.Properties(
    text = valueOrNull(text),
    value = valueOrNull(value),
)

/**
 * @param text The text that is displayed in the input. If the value is missing, the text from div_selection_input_item_value will appear in the input.
 * @param value Value of the item that is displayed in the selection menu.
 */
@Generated
fun TemplateScope.selectionInputItemRefs(
    `use named arguments`: Guard = Guard.instance,
    text: ReferenceProperty<String>? = null,
    value: ReferenceProperty<String>? = null,
) = SelectionInput.Item.Properties(
    text = text,
    value = value,
)

/**
 * @param text The text that is displayed in the input. If the value is missing, the text from div_selection_input_item_value will appear in the input.
 * @param value Value of the item that is displayed in the selection menu.
 */
@Generated
fun SelectionInput.Item.override(
    `use named arguments`: Guard = Guard.instance,
    text: String? = null,
    value: String? = null,
): SelectionInput.Item = SelectionInput.Item(
    SelectionInput.Item.Properties(
        text = valueOrNull(text) ?: properties.text,
        value = valueOrNull(value) ?: properties.value,
    )
)

/**
 * @param text The text that is displayed in the input. If the value is missing, the text from div_selection_input_item_value will appear in the input.
 * @param value Value of the item that is displayed in the selection menu.
 */
@Generated
fun SelectionInput.Item.defer(
    `use named arguments`: Guard = Guard.instance,
    text: ReferenceProperty<String>? = null,
    value: ReferenceProperty<String>? = null,
): SelectionInput.Item = SelectionInput.Item(
    SelectionInput.Item.Properties(
        text = text ?: properties.text,
        value = value ?: properties.value,
    )
)

/**
 * @param text The text that is displayed in the input. If the value is missing, the text from div_selection_input_item_value will appear in the input.
 * @param value Value of the item that is displayed in the selection menu.
 */
@Generated
fun SelectionInput.Item.evaluate(
    `use named arguments`: Guard = Guard.instance,
    text: ExpressionProperty<String>? = null,
    value: ExpressionProperty<String>? = null,
): SelectionInput.Item = SelectionInput.Item(
    SelectionInput.Item.Properties(
        text = text ?: properties.text,
        value = value ?: properties.value,
    )
)

@Generated
fun SelectionInput.Item.asList() = listOf(this)
