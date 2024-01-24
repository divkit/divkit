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
 * Can be created using the method [collectionItemBuilder].
 * 
 * Required parameters: `prototypes, data`.
 */
@Generated
class CollectionItemBuilder internal constructor(
    @JsonIgnore
    val properties: Properties,
) {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(emptyMap())

    operator fun plus(additive: Properties): CollectionItemBuilder = CollectionItemBuilder(
        Properties(
            data = additive.data ?: properties.data,
            dataElementName = additive.dataElementName ?: properties.dataElementName,
            prototypes = additive.prototypes ?: properties.prototypes,
        )
    )

    class Properties internal constructor(
        /**
         * Data that will be used to create collection items.
         */
        val data: Property<List<Any>>?,
        /**
         * Name for accessing the next `data` element in the prototype. Working with this element is like working with DivKit dictionaries.
         * Default value: `it`.
         */
        val dataElementName: Property<String>?,
        /**
         * Array of `div` from which the collection items will be created.
         */
        val prototypes: Property<List<Prototype>>?,
    ) {
        internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
            val result = mutableMapOf<String, Any>()
            result.putAll(properties)
            result.tryPutProperty("data", data)
            result.tryPutProperty("data_element_name", dataElementName)
            result.tryPutProperty("prototypes", prototypes)
            return result
        }
    }

    /**
     * Can be created using the method [collectionItemBuilderPrototype].
     * 
     * Required parameters: `div`.
     */
    @Generated
    class Prototype internal constructor(
        @JsonIgnore
        val properties: Properties,
    ) {
        @JsonAnyGetter
        internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(emptyMap())

        operator fun plus(additive: Properties): Prototype = Prototype(
            Properties(
                div = additive.div ?: properties.div,
                selector = additive.selector ?: properties.selector,
            )
        )

        class Properties internal constructor(
            /**
             * `Div` from which the collection items will be created. In `Div`, you can use expressions using data from `data`, to access the next `data` element, you need to use the same prefix as in `data_element_prefix`.
             */
            val div: Property<Div>?,
            /**
             * A condition that is used to select a prototype for the next item in the collection. If there is more than 1 true condition, the prototype that is earlier will be selected. If none of the conditions are met, the data element will be skipped.
             * Default value: `true`.
             */
            val selector: Property<Boolean>?,
        ) {
            internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
                val result = mutableMapOf<String, Any>()
                result.putAll(properties)
                result.tryPutProperty("div", div)
                result.tryPutProperty("selector", selector)
                return result
            }
        }
    }

}

/**
 * @param data Data that will be used to create collection items.
 * @param dataElementName Name for accessing the next `data` element in the prototype. Working with this element is like working with DivKit dictionaries.
 * @param prototypes Array of `div` from which the collection items will be created.
 */
@Generated
fun DivScope.collectionItemBuilder(
    `use named arguments`: Guard = Guard.instance,
    data: List<Any>? = null,
    dataElementName: String? = null,
    prototypes: List<CollectionItemBuilder.Prototype>? = null,
): CollectionItemBuilder = CollectionItemBuilder(
    CollectionItemBuilder.Properties(
        data = valueOrNull(data),
        dataElementName = valueOrNull(dataElementName),
        prototypes = valueOrNull(prototypes),
    )
)

/**
 * @param data Data that will be used to create collection items.
 * @param dataElementName Name for accessing the next `data` element in the prototype. Working with this element is like working with DivKit dictionaries.
 * @param prototypes Array of `div` from which the collection items will be created.
 */
@Generated
fun DivScope.collectionItemBuilderProps(
    `use named arguments`: Guard = Guard.instance,
    data: List<Any>? = null,
    dataElementName: String? = null,
    prototypes: List<CollectionItemBuilder.Prototype>? = null,
) = CollectionItemBuilder.Properties(
    data = valueOrNull(data),
    dataElementName = valueOrNull(dataElementName),
    prototypes = valueOrNull(prototypes),
)

/**
 * @param data Data that will be used to create collection items.
 * @param dataElementName Name for accessing the next `data` element in the prototype. Working with this element is like working with DivKit dictionaries.
 * @param prototypes Array of `div` from which the collection items will be created.
 */
@Generated
fun TemplateScope.collectionItemBuilderRefs(
    `use named arguments`: Guard = Guard.instance,
    data: ReferenceProperty<List<Any>>? = null,
    dataElementName: ReferenceProperty<String>? = null,
    prototypes: ReferenceProperty<List<CollectionItemBuilder.Prototype>>? = null,
) = CollectionItemBuilder.Properties(
    data = data,
    dataElementName = dataElementName,
    prototypes = prototypes,
)

/**
 * @param data Data that will be used to create collection items.
 * @param dataElementName Name for accessing the next `data` element in the prototype. Working with this element is like working with DivKit dictionaries.
 * @param prototypes Array of `div` from which the collection items will be created.
 */
@Generated
fun CollectionItemBuilder.override(
    `use named arguments`: Guard = Guard.instance,
    data: List<Any>? = null,
    dataElementName: String? = null,
    prototypes: List<CollectionItemBuilder.Prototype>? = null,
): CollectionItemBuilder = CollectionItemBuilder(
    CollectionItemBuilder.Properties(
        data = valueOrNull(data) ?: properties.data,
        dataElementName = valueOrNull(dataElementName) ?: properties.dataElementName,
        prototypes = valueOrNull(prototypes) ?: properties.prototypes,
    )
)

/**
 * @param data Data that will be used to create collection items.
 * @param dataElementName Name for accessing the next `data` element in the prototype. Working with this element is like working with DivKit dictionaries.
 * @param prototypes Array of `div` from which the collection items will be created.
 */
@Generated
fun CollectionItemBuilder.defer(
    `use named arguments`: Guard = Guard.instance,
    data: ReferenceProperty<List<Any>>? = null,
    dataElementName: ReferenceProperty<String>? = null,
    prototypes: ReferenceProperty<List<CollectionItemBuilder.Prototype>>? = null,
): CollectionItemBuilder = CollectionItemBuilder(
    CollectionItemBuilder.Properties(
        data = data ?: properties.data,
        dataElementName = dataElementName ?: properties.dataElementName,
        prototypes = prototypes ?: properties.prototypes,
    )
)

/**
 * @param data Data that will be used to create collection items.
 */
@Generated
fun CollectionItemBuilder.evaluate(
    `use named arguments`: Guard = Guard.instance,
    data: ExpressionProperty<List<Any>>? = null,
): CollectionItemBuilder = CollectionItemBuilder(
    CollectionItemBuilder.Properties(
        data = data ?: properties.data,
        dataElementName = properties.dataElementName,
        prototypes = properties.prototypes,
    )
)

@Generated
fun CollectionItemBuilder.asList() = listOf(this)

/**
 * @param div `Div` from which the collection items will be created. In `Div`, you can use expressions using data from `data`, to access the next `data` element, you need to use the same prefix as in `data_element_prefix`.
 * @param selector A condition that is used to select a prototype for the next item in the collection. If there is more than 1 true condition, the prototype that is earlier will be selected. If none of the conditions are met, the data element will be skipped.
 */
@Generated
fun DivScope.collectionItemBuilderPrototype(
    `use named arguments`: Guard = Guard.instance,
    div: Div? = null,
    selector: Boolean? = null,
): CollectionItemBuilder.Prototype = CollectionItemBuilder.Prototype(
    CollectionItemBuilder.Prototype.Properties(
        div = valueOrNull(div),
        selector = valueOrNull(selector),
    )
)

/**
 * @param div `Div` from which the collection items will be created. In `Div`, you can use expressions using data from `data`, to access the next `data` element, you need to use the same prefix as in `data_element_prefix`.
 * @param selector A condition that is used to select a prototype for the next item in the collection. If there is more than 1 true condition, the prototype that is earlier will be selected. If none of the conditions are met, the data element will be skipped.
 */
@Generated
fun DivScope.collectionItemBuilderPrototypeProps(
    `use named arguments`: Guard = Guard.instance,
    div: Div? = null,
    selector: Boolean? = null,
) = CollectionItemBuilder.Prototype.Properties(
    div = valueOrNull(div),
    selector = valueOrNull(selector),
)

/**
 * @param div `Div` from which the collection items will be created. In `Div`, you can use expressions using data from `data`, to access the next `data` element, you need to use the same prefix as in `data_element_prefix`.
 * @param selector A condition that is used to select a prototype for the next item in the collection. If there is more than 1 true condition, the prototype that is earlier will be selected. If none of the conditions are met, the data element will be skipped.
 */
@Generated
fun TemplateScope.collectionItemBuilderPrototypeRefs(
    `use named arguments`: Guard = Guard.instance,
    div: ReferenceProperty<Div>? = null,
    selector: ReferenceProperty<Boolean>? = null,
) = CollectionItemBuilder.Prototype.Properties(
    div = div,
    selector = selector,
)

/**
 * @param div `Div` from which the collection items will be created. In `Div`, you can use expressions using data from `data`, to access the next `data` element, you need to use the same prefix as in `data_element_prefix`.
 * @param selector A condition that is used to select a prototype for the next item in the collection. If there is more than 1 true condition, the prototype that is earlier will be selected. If none of the conditions are met, the data element will be skipped.
 */
@Generated
fun CollectionItemBuilder.Prototype.override(
    `use named arguments`: Guard = Guard.instance,
    div: Div? = null,
    selector: Boolean? = null,
): CollectionItemBuilder.Prototype = CollectionItemBuilder.Prototype(
    CollectionItemBuilder.Prototype.Properties(
        div = valueOrNull(div) ?: properties.div,
        selector = valueOrNull(selector) ?: properties.selector,
    )
)

/**
 * @param div `Div` from which the collection items will be created. In `Div`, you can use expressions using data from `data`, to access the next `data` element, you need to use the same prefix as in `data_element_prefix`.
 * @param selector A condition that is used to select a prototype for the next item in the collection. If there is more than 1 true condition, the prototype that is earlier will be selected. If none of the conditions are met, the data element will be skipped.
 */
@Generated
fun CollectionItemBuilder.Prototype.defer(
    `use named arguments`: Guard = Guard.instance,
    div: ReferenceProperty<Div>? = null,
    selector: ReferenceProperty<Boolean>? = null,
): CollectionItemBuilder.Prototype = CollectionItemBuilder.Prototype(
    CollectionItemBuilder.Prototype.Properties(
        div = div ?: properties.div,
        selector = selector ?: properties.selector,
    )
)

/**
 * @param selector A condition that is used to select a prototype for the next item in the collection. If there is more than 1 true condition, the prototype that is earlier will be selected. If none of the conditions are met, the data element will be skipped.
 */
@Generated
fun CollectionItemBuilder.Prototype.evaluate(
    `use named arguments`: Guard = Guard.instance,
    selector: ExpressionProperty<Boolean>? = null,
): CollectionItemBuilder.Prototype = CollectionItemBuilder.Prototype(
    CollectionItemBuilder.Prototype.Properties(
        div = properties.div,
        selector = selector ?: properties.selector,
    )
)

@Generated
fun CollectionItemBuilder.Prototype.asList() = listOf(this)
