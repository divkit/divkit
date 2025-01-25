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
data class CollectionItemBuilder internal constructor(
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

    data class Properties internal constructor(
        /**
         * Data that will be used to create collection elements.
         */
        val data: Property<List<Any>>?,
        /**
         * Name for accessing the next `data` element in the prototype. Working with this element is the same as with dictionaries.
         * Default value: `it`.
         */
        val dataElementName: Property<String>?,
        /**
         * Array of `div` elements from which the collection elements will be created.
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
    data class Prototype internal constructor(
        @JsonIgnore
        val properties: Properties,
    ) {
        @JsonAnyGetter
        internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(emptyMap())

        operator fun plus(additive: Properties): Prototype = Prototype(
            Properties(
                div = additive.div ?: properties.div,
                id = additive.id ?: properties.id,
                selector = additive.selector ?: properties.selector,
            )
        )

        data class Properties internal constructor(
            /**
             * `Div` from which the collection elements will be created. In `Div`, you can use expressions using data from `data`. To access the next `data` element, you need to use the same prefix as in `data_element_prefix`.
             */
            val div: Property<Div>?,
            /**
             * `id` of the element to be created from the prototype. Unlike the `div-base.id` field, may contain expressions. Has a higher priority than `div-base.id`.
             */
            val id: Property<String>?,
            /**
             * A condition that is used to select the prototype for the next element in the collection. If there is more than 1 true condition, the earlier prototype is selected. If none of the conditions are met, the element from `data` is skipped.
             * Default value: `true`.
             */
            val selector: Property<Boolean>?,
        ) {
            internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
                val result = mutableMapOf<String, Any>()
                result.putAll(properties)
                result.tryPutProperty("div", div)
                result.tryPutProperty("id", id)
                result.tryPutProperty("selector", selector)
                return result
            }
        }
    }

}

/**
 * @param data Data that will be used to create collection elements.
 * @param dataElementName Name for accessing the next `data` element in the prototype. Working with this element is the same as with dictionaries.
 * @param prototypes Array of `div` elements from which the collection elements will be created.
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
 * @param data Data that will be used to create collection elements.
 * @param dataElementName Name for accessing the next `data` element in the prototype. Working with this element is the same as with dictionaries.
 * @param prototypes Array of `div` elements from which the collection elements will be created.
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
 * @param data Data that will be used to create collection elements.
 * @param dataElementName Name for accessing the next `data` element in the prototype. Working with this element is the same as with dictionaries.
 * @param prototypes Array of `div` elements from which the collection elements will be created.
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
 * @param data Data that will be used to create collection elements.
 * @param dataElementName Name for accessing the next `data` element in the prototype. Working with this element is the same as with dictionaries.
 * @param prototypes Array of `div` elements from which the collection elements will be created.
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
 * @param data Data that will be used to create collection elements.
 * @param dataElementName Name for accessing the next `data` element in the prototype. Working with this element is the same as with dictionaries.
 * @param prototypes Array of `div` elements from which the collection elements will be created.
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
 * @param data Data that will be used to create collection elements.
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
 * @param div `Div` from which the collection elements will be created. In `Div`, you can use expressions using data from `data`. To access the next `data` element, you need to use the same prefix as in `data_element_prefix`.
 * @param id `id` of the element to be created from the prototype. Unlike the `div-base.id` field, may contain expressions. Has a higher priority than `div-base.id`.
 * @param selector A condition that is used to select the prototype for the next element in the collection. If there is more than 1 true condition, the earlier prototype is selected. If none of the conditions are met, the element from `data` is skipped.
 */
@Generated
fun DivScope.collectionItemBuilderPrototype(
    `use named arguments`: Guard = Guard.instance,
    div: Div? = null,
    id: String? = null,
    selector: Boolean? = null,
): CollectionItemBuilder.Prototype = CollectionItemBuilder.Prototype(
    CollectionItemBuilder.Prototype.Properties(
        div = valueOrNull(div),
        id = valueOrNull(id),
        selector = valueOrNull(selector),
    )
)

/**
 * @param div `Div` from which the collection elements will be created. In `Div`, you can use expressions using data from `data`. To access the next `data` element, you need to use the same prefix as in `data_element_prefix`.
 * @param id `id` of the element to be created from the prototype. Unlike the `div-base.id` field, may contain expressions. Has a higher priority than `div-base.id`.
 * @param selector A condition that is used to select the prototype for the next element in the collection. If there is more than 1 true condition, the earlier prototype is selected. If none of the conditions are met, the element from `data` is skipped.
 */
@Generated
fun DivScope.collectionItemBuilderPrototypeProps(
    `use named arguments`: Guard = Guard.instance,
    div: Div? = null,
    id: String? = null,
    selector: Boolean? = null,
) = CollectionItemBuilder.Prototype.Properties(
    div = valueOrNull(div),
    id = valueOrNull(id),
    selector = valueOrNull(selector),
)

/**
 * @param div `Div` from which the collection elements will be created. In `Div`, you can use expressions using data from `data`. To access the next `data` element, you need to use the same prefix as in `data_element_prefix`.
 * @param id `id` of the element to be created from the prototype. Unlike the `div-base.id` field, may contain expressions. Has a higher priority than `div-base.id`.
 * @param selector A condition that is used to select the prototype for the next element in the collection. If there is more than 1 true condition, the earlier prototype is selected. If none of the conditions are met, the element from `data` is skipped.
 */
@Generated
fun TemplateScope.collectionItemBuilderPrototypeRefs(
    `use named arguments`: Guard = Guard.instance,
    div: ReferenceProperty<Div>? = null,
    id: ReferenceProperty<String>? = null,
    selector: ReferenceProperty<Boolean>? = null,
) = CollectionItemBuilder.Prototype.Properties(
    div = div,
    id = id,
    selector = selector,
)

/**
 * @param div `Div` from which the collection elements will be created. In `Div`, you can use expressions using data from `data`. To access the next `data` element, you need to use the same prefix as in `data_element_prefix`.
 * @param id `id` of the element to be created from the prototype. Unlike the `div-base.id` field, may contain expressions. Has a higher priority than `div-base.id`.
 * @param selector A condition that is used to select the prototype for the next element in the collection. If there is more than 1 true condition, the earlier prototype is selected. If none of the conditions are met, the element from `data` is skipped.
 */
@Generated
fun CollectionItemBuilder.Prototype.override(
    `use named arguments`: Guard = Guard.instance,
    div: Div? = null,
    id: String? = null,
    selector: Boolean? = null,
): CollectionItemBuilder.Prototype = CollectionItemBuilder.Prototype(
    CollectionItemBuilder.Prototype.Properties(
        div = valueOrNull(div) ?: properties.div,
        id = valueOrNull(id) ?: properties.id,
        selector = valueOrNull(selector) ?: properties.selector,
    )
)

/**
 * @param div `Div` from which the collection elements will be created. In `Div`, you can use expressions using data from `data`. To access the next `data` element, you need to use the same prefix as in `data_element_prefix`.
 * @param id `id` of the element to be created from the prototype. Unlike the `div-base.id` field, may contain expressions. Has a higher priority than `div-base.id`.
 * @param selector A condition that is used to select the prototype for the next element in the collection. If there is more than 1 true condition, the earlier prototype is selected. If none of the conditions are met, the element from `data` is skipped.
 */
@Generated
fun CollectionItemBuilder.Prototype.defer(
    `use named arguments`: Guard = Guard.instance,
    div: ReferenceProperty<Div>? = null,
    id: ReferenceProperty<String>? = null,
    selector: ReferenceProperty<Boolean>? = null,
): CollectionItemBuilder.Prototype = CollectionItemBuilder.Prototype(
    CollectionItemBuilder.Prototype.Properties(
        div = div ?: properties.div,
        id = id ?: properties.id,
        selector = selector ?: properties.selector,
    )
)

/**
 * @param id `id` of the element to be created from the prototype. Unlike the `div-base.id` field, may contain expressions. Has a higher priority than `div-base.id`.
 * @param selector A condition that is used to select the prototype for the next element in the collection. If there is more than 1 true condition, the earlier prototype is selected. If none of the conditions are met, the element from `data` is skipped.
 */
@Generated
fun CollectionItemBuilder.Prototype.evaluate(
    `use named arguments`: Guard = Guard.instance,
    id: ExpressionProperty<String>? = null,
    selector: ExpressionProperty<Boolean>? = null,
): CollectionItemBuilder.Prototype = CollectionItemBuilder.Prototype(
    CollectionItemBuilder.Prototype.Properties(
        div = properties.div,
        id = id ?: properties.id,
        selector = selector ?: properties.selector,
    )
)

@Generated
fun CollectionItemBuilder.Prototype.asList() = listOf(this)
