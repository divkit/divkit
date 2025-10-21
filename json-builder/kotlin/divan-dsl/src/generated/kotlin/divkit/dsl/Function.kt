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
 * User-defined function.
 * 
 * Can be created using the method [function].
 * 
 * Required parameters: `return_type, name, body, arguments`.
 */
@Generated
@ExposedCopyVisibility
data class Function internal constructor(
    @JsonIgnore
    val properties: Properties,
) {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(emptyMap())

    operator fun plus(additive: Properties): Function = Function(
        Properties(
            arguments = additive.arguments ?: properties.arguments,
            body = additive.body ?: properties.body,
            name = additive.name ?: properties.name,
            returnType = additive.returnType ?: properties.returnType,
        )
    )

    @ExposedCopyVisibility
    data class Properties internal constructor(
        /**
         * Function argument.
         */
        val arguments: Property<List<FunctionArgument>>?,
        /**
         * Function body. Evaluated as an expression using the passed arguments. Doesn't capture external variables.
         */
        val body: Property<String>?,
        /**
         * Function name.
         */
        val name: Property<String>?,
        /**
         * Return value type.
         */
        val returnType: Property<EvaluableType>?,
    ) {
        internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
            val result = mutableMapOf<String, Any>()
            result.putAll(properties)
            result.tryPutProperty("arguments", arguments)
            result.tryPutProperty("body", body)
            result.tryPutProperty("name", name)
            result.tryPutProperty("return_type", returnType)
            return result
        }
    }
}

/**
 * @param arguments Function argument.
 * @param body Function body. Evaluated as an expression using the passed arguments. Doesn't capture external variables.
 * @param name Function name.
 * @param returnType Return value type.
 */
@Generated
fun DivScope.function(
    `use named arguments`: Guard = Guard.instance,
    arguments: List<FunctionArgument>? = null,
    body: String? = null,
    name: String? = null,
    returnType: EvaluableType? = null,
): Function = Function(
    Function.Properties(
        arguments = valueOrNull(arguments),
        body = valueOrNull(body),
        name = valueOrNull(name),
        returnType = valueOrNull(returnType),
    )
)

/**
 * @param arguments Function argument.
 * @param body Function body. Evaluated as an expression using the passed arguments. Doesn't capture external variables.
 * @param name Function name.
 * @param returnType Return value type.
 */
@Generated
fun DivScope.functionProps(
    `use named arguments`: Guard = Guard.instance,
    arguments: List<FunctionArgument>? = null,
    body: String? = null,
    name: String? = null,
    returnType: EvaluableType? = null,
) = Function.Properties(
    arguments = valueOrNull(arguments),
    body = valueOrNull(body),
    name = valueOrNull(name),
    returnType = valueOrNull(returnType),
)

/**
 * @param arguments Function argument.
 * @param body Function body. Evaluated as an expression using the passed arguments. Doesn't capture external variables.
 * @param name Function name.
 * @param returnType Return value type.
 */
@Generated
fun TemplateScope.functionRefs(
    `use named arguments`: Guard = Guard.instance,
    arguments: ReferenceProperty<List<FunctionArgument>>? = null,
    body: ReferenceProperty<String>? = null,
    name: ReferenceProperty<String>? = null,
    returnType: ReferenceProperty<EvaluableType>? = null,
) = Function.Properties(
    arguments = arguments,
    body = body,
    name = name,
    returnType = returnType,
)

/**
 * @param arguments Function argument.
 * @param body Function body. Evaluated as an expression using the passed arguments. Doesn't capture external variables.
 * @param name Function name.
 * @param returnType Return value type.
 */
@Generated
fun Function.override(
    `use named arguments`: Guard = Guard.instance,
    arguments: List<FunctionArgument>? = null,
    body: String? = null,
    name: String? = null,
    returnType: EvaluableType? = null,
): Function = Function(
    Function.Properties(
        arguments = valueOrNull(arguments) ?: properties.arguments,
        body = valueOrNull(body) ?: properties.body,
        name = valueOrNull(name) ?: properties.name,
        returnType = valueOrNull(returnType) ?: properties.returnType,
    )
)

/**
 * @param arguments Function argument.
 * @param body Function body. Evaluated as an expression using the passed arguments. Doesn't capture external variables.
 * @param name Function name.
 * @param returnType Return value type.
 */
@Generated
fun Function.defer(
    `use named arguments`: Guard = Guard.instance,
    arguments: ReferenceProperty<List<FunctionArgument>>? = null,
    body: ReferenceProperty<String>? = null,
    name: ReferenceProperty<String>? = null,
    returnType: ReferenceProperty<EvaluableType>? = null,
): Function = Function(
    Function.Properties(
        arguments = arguments ?: properties.arguments,
        body = body ?: properties.body,
        name = name ?: properties.name,
        returnType = returnType ?: properties.returnType,
    )
)

/**
 * @param arguments Function argument.
 * @param body Function body. Evaluated as an expression using the passed arguments. Doesn't capture external variables.
 * @param name Function name.
 * @param returnType Return value type.
 */
@Generated
fun Function.modify(
    `use named arguments`: Guard = Guard.instance,
    arguments: Property<List<FunctionArgument>>? = null,
    body: Property<String>? = null,
    name: Property<String>? = null,
    returnType: Property<EvaluableType>? = null,
): Function = Function(
    Function.Properties(
        arguments = arguments ?: properties.arguments,
        body = body ?: properties.body,
        name = name ?: properties.name,
        returnType = returnType ?: properties.returnType,
    )
)

@Generated
fun Function.asList() = listOf(this)
