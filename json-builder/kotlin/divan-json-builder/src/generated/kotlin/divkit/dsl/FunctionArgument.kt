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
 * Function argument.
 * 
 * Can be created using the method [functionArgument].
 * 
 * Required parameters: `type, name`.
 */
@Generated
@ExposedCopyVisibility
data class FunctionArgument internal constructor(
    @JsonIgnore
    val properties: Properties,
) {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(emptyMap())

    operator fun plus(additive: Properties): FunctionArgument = FunctionArgument(
        Properties(
            name = additive.name ?: properties.name,
            type = additive.type ?: properties.type,
        )
    )

    @ExposedCopyVisibility
    data class Properties internal constructor(
        /**
         * Function argument name.
         */
        val name: Property<String>?,
        /**
         * Function argument type.
         */
        val type: Property<EvaluableType>?,
    ) {
        internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
            val result = mutableMapOf<String, Any>()
            result.putAll(properties)
            result.tryPutProperty("name", name)
            result.tryPutProperty("type", type)
            return result
        }
    }
}

/**
 * @param name Function argument name.
 * @param type Function argument type.
 */
@Generated
fun DivScope.functionArgument(
    `use named arguments`: Guard = Guard.instance,
    name: String? = null,
    type: EvaluableType? = null,
): FunctionArgument = FunctionArgument(
    FunctionArgument.Properties(
        name = valueOrNull(name),
        type = valueOrNull(type),
    )
)

/**
 * @param name Function argument name.
 * @param type Function argument type.
 */
@Generated
fun DivScope.functionArgumentProps(
    `use named arguments`: Guard = Guard.instance,
    name: String? = null,
    type: EvaluableType? = null,
) = FunctionArgument.Properties(
    name = valueOrNull(name),
    type = valueOrNull(type),
)

/**
 * @param name Function argument name.
 * @param type Function argument type.
 */
@Generated
fun TemplateScope.functionArgumentRefs(
    `use named arguments`: Guard = Guard.instance,
    name: ReferenceProperty<String>? = null,
    type: ReferenceProperty<EvaluableType>? = null,
) = FunctionArgument.Properties(
    name = name,
    type = type,
)

/**
 * @param name Function argument name.
 * @param type Function argument type.
 */
@Generated
fun FunctionArgument.override(
    `use named arguments`: Guard = Guard.instance,
    name: String? = null,
    type: EvaluableType? = null,
): FunctionArgument = FunctionArgument(
    FunctionArgument.Properties(
        name = valueOrNull(name) ?: properties.name,
        type = valueOrNull(type) ?: properties.type,
    )
)

/**
 * @param name Function argument name.
 * @param type Function argument type.
 */
@Generated
fun FunctionArgument.defer(
    `use named arguments`: Guard = Guard.instance,
    name: ReferenceProperty<String>? = null,
    type: ReferenceProperty<EvaluableType>? = null,
): FunctionArgument = FunctionArgument(
    FunctionArgument.Properties(
        name = name ?: properties.name,
        type = type ?: properties.type,
    )
)

/**
 * @param name Function argument name.
 * @param type Function argument type.
 */
@Generated
fun FunctionArgument.modify(
    `use named arguments`: Guard = Guard.instance,
    name: Property<String>? = null,
    type: Property<EvaluableType>? = null,
): FunctionArgument = FunctionArgument(
    FunctionArgument.Properties(
        name = name ?: properties.name,
        type = type ?: properties.type,
    )
)

@Generated
fun FunctionArgument.asList() = listOf(this)
