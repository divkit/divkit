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
 * [Calculated expression](../../expressions) filter.
 * 
 * Can be created using the method [inputFilterExpression].
 * 
 * Required parameters: `type, condition`.
 */
@Generated
data class InputFilterExpression internal constructor(
    @JsonIgnore
    val properties: Properties,
) : InputFilter {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "expression")
    )

    operator fun plus(additive: Properties): InputFilterExpression = InputFilterExpression(
        Properties(
            condition = additive.condition ?: properties.condition,
        )
    )

    data class Properties internal constructor(
        /**
         * [Calculated expression](../../expressions) used as a value validity condition.
         */
        val condition: Property<Boolean>?,
    ) {
        internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
            val result = mutableMapOf<String, Any>()
            result.putAll(properties)
            result.tryPutProperty("condition", condition)
            return result
        }
    }
}

/**
 * @param condition [Calculated expression](../../expressions) used as a value validity condition.
 */
@Generated
fun DivScope.inputFilterExpression(
    `use named arguments`: Guard = Guard.instance,
    condition: Boolean? = null,
): InputFilterExpression = InputFilterExpression(
    InputFilterExpression.Properties(
        condition = valueOrNull(condition),
    )
)

/**
 * @param condition [Calculated expression](../../expressions) used as a value validity condition.
 */
@Generated
fun DivScope.inputFilterExpressionProps(
    `use named arguments`: Guard = Guard.instance,
    condition: Boolean? = null,
) = InputFilterExpression.Properties(
    condition = valueOrNull(condition),
)

/**
 * @param condition [Calculated expression](../../expressions) used as a value validity condition.
 */
@Generated
fun TemplateScope.inputFilterExpressionRefs(
    `use named arguments`: Guard = Guard.instance,
    condition: ReferenceProperty<Boolean>? = null,
) = InputFilterExpression.Properties(
    condition = condition,
)

/**
 * @param condition [Calculated expression](../../expressions) used as a value validity condition.
 */
@Generated
fun InputFilterExpression.override(
    `use named arguments`: Guard = Guard.instance,
    condition: Boolean? = null,
): InputFilterExpression = InputFilterExpression(
    InputFilterExpression.Properties(
        condition = valueOrNull(condition) ?: properties.condition,
    )
)

/**
 * @param condition [Calculated expression](../../expressions) used as a value validity condition.
 */
@Generated
fun InputFilterExpression.defer(
    `use named arguments`: Guard = Guard.instance,
    condition: ReferenceProperty<Boolean>? = null,
): InputFilterExpression = InputFilterExpression(
    InputFilterExpression.Properties(
        condition = condition ?: properties.condition,
    )
)

/**
 * @param condition [Calculated expression](../../expressions) used as a value validity condition.
 */
@Generated
fun InputFilterExpression.evaluate(
    `use named arguments`: Guard = Guard.instance,
    condition: ExpressionProperty<Boolean>? = null,
): InputFilterExpression = InputFilterExpression(
    InputFilterExpression.Properties(
        condition = condition ?: properties.condition,
    )
)

@Generated
fun InputFilterExpression.asList() = listOf(this)
