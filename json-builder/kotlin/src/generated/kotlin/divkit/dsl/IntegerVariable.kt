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
 * An integer variable.
 * 
 * Can be created using the method [integerVariable].
 * 
 * Required parameters: `value, type, name`.
 */
@Generated
data class IntegerVariable internal constructor(
    @JsonIgnore
    val properties: Properties,
) : Variable {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "integer")
    )

    operator fun plus(additive: Properties): IntegerVariable = IntegerVariable(
        Properties(
            name = additive.name ?: properties.name,
            value = additive.value ?: properties.value,
        )
    )

    data class Properties internal constructor(
        /**
         * Variable name.
         */
        val name: Property<String>?,
        /**
         * Value.
         */
        val value: Property<Long>?,
    ) {
        internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
            val result = mutableMapOf<String, Any>()
            result.putAll(properties)
            result.tryPutProperty("name", name)
            result.tryPutProperty("value", value)
            return result
        }
    }
}

/**
 * @param name Variable name.
 * @param value Value.
 */
@Generated
fun DivScope.integerVariable(
    `use named arguments`: Guard = Guard.instance,
    name: String? = null,
    value: Long? = null,
): IntegerVariable = IntegerVariable(
    IntegerVariable.Properties(
        name = valueOrNull(name),
        value = valueOrNull(value),
    )
)

/**
 * @param name Variable name.
 * @param value Value.
 */
@Generated
fun DivScope.integerVariableProps(
    `use named arguments`: Guard = Guard.instance,
    name: String? = null,
    value: Long? = null,
) = IntegerVariable.Properties(
    name = valueOrNull(name),
    value = valueOrNull(value),
)

/**
 * @param name Variable name.
 * @param value Value.
 */
@Generated
fun TemplateScope.integerVariableRefs(
    `use named arguments`: Guard = Guard.instance,
    name: ReferenceProperty<String>? = null,
    value: ReferenceProperty<Long>? = null,
) = IntegerVariable.Properties(
    name = name,
    value = value,
)

/**
 * @param name Variable name.
 * @param value Value.
 */
@Generated
fun IntegerVariable.override(
    `use named arguments`: Guard = Guard.instance,
    name: String? = null,
    value: Long? = null,
): IntegerVariable = IntegerVariable(
    IntegerVariable.Properties(
        name = valueOrNull(name) ?: properties.name,
        value = valueOrNull(value) ?: properties.value,
    )
)

/**
 * @param name Variable name.
 * @param value Value.
 */
@Generated
fun IntegerVariable.defer(
    `use named arguments`: Guard = Guard.instance,
    name: ReferenceProperty<String>? = null,
    value: ReferenceProperty<Long>? = null,
): IntegerVariable = IntegerVariable(
    IntegerVariable.Properties(
        name = name ?: properties.name,
        value = value ?: properties.value,
    )
)

/**
 * @param value Value.
 */
@Generated
fun IntegerVariable.evaluate(
    `use named arguments`: Guard = Guard.instance,
    value: ExpressionProperty<Long>? = null,
): IntegerVariable = IntegerVariable(
    IntegerVariable.Properties(
        name = properties.name,
        value = value ?: properties.value,
    )
)

@Generated
fun IntegerVariable.asList() = listOf(this)
