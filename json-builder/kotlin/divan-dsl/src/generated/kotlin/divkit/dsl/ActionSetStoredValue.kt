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
 * Temporarily saves the variable in storage.
 * 
 * Can be created using the method [actionSetStoredValue].
 * 
 * Required parameters: `value, type, name, lifetime`.
 */
@Generated
@ExposedCopyVisibility
data class ActionSetStoredValue internal constructor(
    @JsonIgnore
    val properties: Properties,
) : ActionTyped {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "set_stored_value")
    )

    operator fun plus(additive: Properties): ActionSetStoredValue = ActionSetStoredValue(
        Properties(
            lifetime = additive.lifetime ?: properties.lifetime,
            name = additive.name ?: properties.name,
            scope = additive.scope ?: properties.scope,
            value = additive.value ?: properties.value,
        )
    )

    @ExposedCopyVisibility
    data class Properties internal constructor(
        /**
         * Duration of storage in seconds.
         */
        val lifetime: Property<Int>?,
        /**
         * Name of the saved variable.
         */
        val name: Property<String>?,
        /**
         * Scope of the stored variable:<li>`global` — not bound to a specific card (available for any card);</li><li>`card` — bound to the current card. On Android the card is identified by `DivDataTag`, on iOS by the `cardId` parameter, on Web the scope value is passed to `Store` and the developer integrating DivKit must implement saving variables for that scope.</li>/nDefault value for Android and iOS is `global'. For Web, the implementation depends entirely on the developer.
         */
        val scope: Property<Scope>?,
        /**
         * Saved value.
         */
        val value: Property<TypedValue>?,
    ) {
        internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
            val result = mutableMapOf<String, Any>()
            result.putAll(properties)
            result.tryPutProperty("lifetime", lifetime)
            result.tryPutProperty("name", name)
            result.tryPutProperty("scope", scope)
            result.tryPutProperty("value", value)
            return result
        }
    }

    /**
     * Scope of the stored variable:<li>`global` — not bound to a specific card (available for any card);</li><li>`card` — bound to the current card. On Android the card is identified by `DivDataTag`, on iOS by the `cardId` parameter, on Web the scope value is passed to `Store` and the developer integrating DivKit must implement saving variables for that scope.</li>/nDefault value for Android and iOS is `global'. For Web, the implementation depends entirely on the developer.
     * 
     * Possible values: [global], [card].
     */
    @Generated
    sealed interface Scope
}

/**
 * @param lifetime Duration of storage in seconds.
 * @param name Name of the saved variable.
 * @param scope Scope of the stored variable:<li>`global` — not bound to a specific card (available for any card);</li><li>`card` — bound to the current card. On Android the card is identified by `DivDataTag`, on iOS by the `cardId` parameter, on Web the scope value is passed to `Store` and the developer integrating DivKit must implement saving variables for that scope.</li>/nDefault value for Android and iOS is `global'. For Web, the implementation depends entirely on the developer.
 * @param value Saved value.
 */
@Generated
fun DivScope.actionSetStoredValue(
    `use named arguments`: Guard = Guard.instance,
    lifetime: Int? = null,
    name: String? = null,
    scope: ActionSetStoredValue.Scope? = null,
    value: TypedValue? = null,
): ActionSetStoredValue = ActionSetStoredValue(
    ActionSetStoredValue.Properties(
        lifetime = valueOrNull(lifetime),
        name = valueOrNull(name),
        scope = valueOrNull(scope),
        value = valueOrNull(value),
    )
)

/**
 * @param lifetime Duration of storage in seconds.
 * @param name Name of the saved variable.
 * @param scope Scope of the stored variable:<li>`global` — not bound to a specific card (available for any card);</li><li>`card` — bound to the current card. On Android the card is identified by `DivDataTag`, on iOS by the `cardId` parameter, on Web the scope value is passed to `Store` and the developer integrating DivKit must implement saving variables for that scope.</li>/nDefault value for Android and iOS is `global'. For Web, the implementation depends entirely on the developer.
 * @param value Saved value.
 */
@Generated
fun DivScope.actionSetStoredValueProps(
    `use named arguments`: Guard = Guard.instance,
    lifetime: Int? = null,
    name: String? = null,
    scope: ActionSetStoredValue.Scope? = null,
    value: TypedValue? = null,
) = ActionSetStoredValue.Properties(
    lifetime = valueOrNull(lifetime),
    name = valueOrNull(name),
    scope = valueOrNull(scope),
    value = valueOrNull(value),
)

/**
 * @param lifetime Duration of storage in seconds.
 * @param name Name of the saved variable.
 * @param scope Scope of the stored variable:<li>`global` — not bound to a specific card (available for any card);</li><li>`card` — bound to the current card. On Android the card is identified by `DivDataTag`, on iOS by the `cardId` parameter, on Web the scope value is passed to `Store` and the developer integrating DivKit must implement saving variables for that scope.</li>/nDefault value for Android and iOS is `global'. For Web, the implementation depends entirely on the developer.
 * @param value Saved value.
 */
@Generated
fun TemplateScope.actionSetStoredValueRefs(
    `use named arguments`: Guard = Guard.instance,
    lifetime: ReferenceProperty<Int>? = null,
    name: ReferenceProperty<String>? = null,
    scope: ReferenceProperty<ActionSetStoredValue.Scope>? = null,
    value: ReferenceProperty<TypedValue>? = null,
) = ActionSetStoredValue.Properties(
    lifetime = lifetime,
    name = name,
    scope = scope,
    value = value,
)

/**
 * @param lifetime Duration of storage in seconds.
 * @param name Name of the saved variable.
 * @param scope Scope of the stored variable:<li>`global` — not bound to a specific card (available for any card);</li><li>`card` — bound to the current card. On Android the card is identified by `DivDataTag`, on iOS by the `cardId` parameter, on Web the scope value is passed to `Store` and the developer integrating DivKit must implement saving variables for that scope.</li>/nDefault value for Android and iOS is `global'. For Web, the implementation depends entirely on the developer.
 * @param value Saved value.
 */
@Generated
fun ActionSetStoredValue.override(
    `use named arguments`: Guard = Guard.instance,
    lifetime: Int? = null,
    name: String? = null,
    scope: ActionSetStoredValue.Scope? = null,
    value: TypedValue? = null,
): ActionSetStoredValue = ActionSetStoredValue(
    ActionSetStoredValue.Properties(
        lifetime = valueOrNull(lifetime) ?: properties.lifetime,
        name = valueOrNull(name) ?: properties.name,
        scope = valueOrNull(scope) ?: properties.scope,
        value = valueOrNull(value) ?: properties.value,
    )
)

/**
 * @param lifetime Duration of storage in seconds.
 * @param name Name of the saved variable.
 * @param scope Scope of the stored variable:<li>`global` — not bound to a specific card (available for any card);</li><li>`card` — bound to the current card. On Android the card is identified by `DivDataTag`, on iOS by the `cardId` parameter, on Web the scope value is passed to `Store` and the developer integrating DivKit must implement saving variables for that scope.</li>/nDefault value for Android and iOS is `global'. For Web, the implementation depends entirely on the developer.
 * @param value Saved value.
 */
@Generated
fun ActionSetStoredValue.defer(
    `use named arguments`: Guard = Guard.instance,
    lifetime: ReferenceProperty<Int>? = null,
    name: ReferenceProperty<String>? = null,
    scope: ReferenceProperty<ActionSetStoredValue.Scope>? = null,
    value: ReferenceProperty<TypedValue>? = null,
): ActionSetStoredValue = ActionSetStoredValue(
    ActionSetStoredValue.Properties(
        lifetime = lifetime ?: properties.lifetime,
        name = name ?: properties.name,
        scope = scope ?: properties.scope,
        value = value ?: properties.value,
    )
)

/**
 * @param lifetime Duration of storage in seconds.
 * @param name Name of the saved variable.
 * @param scope Scope of the stored variable:<li>`global` — not bound to a specific card (available for any card);</li><li>`card` — bound to the current card. On Android the card is identified by `DivDataTag`, on iOS by the `cardId` parameter, on Web the scope value is passed to `Store` and the developer integrating DivKit must implement saving variables for that scope.</li>/nDefault value for Android and iOS is `global'. For Web, the implementation depends entirely on the developer.
 * @param value Saved value.
 */
@Generated
fun ActionSetStoredValue.modify(
    `use named arguments`: Guard = Guard.instance,
    lifetime: Property<Int>? = null,
    name: Property<String>? = null,
    scope: Property<ActionSetStoredValue.Scope>? = null,
    value: Property<TypedValue>? = null,
): ActionSetStoredValue = ActionSetStoredValue(
    ActionSetStoredValue.Properties(
        lifetime = lifetime ?: properties.lifetime,
        name = name ?: properties.name,
        scope = scope ?: properties.scope,
        value = value ?: properties.value,
    )
)

/**
 * @param lifetime Duration of storage in seconds.
 * @param name Name of the saved variable.
 * @param scope Scope of the stored variable:<li>`global` — not bound to a specific card (available for any card);</li><li>`card` — bound to the current card. On Android the card is identified by `DivDataTag`, on iOS by the `cardId` parameter, on Web the scope value is passed to `Store` and the developer integrating DivKit must implement saving variables for that scope.</li>/nDefault value for Android and iOS is `global'. For Web, the implementation depends entirely on the developer.
 */
@Generated
fun ActionSetStoredValue.evaluate(
    `use named arguments`: Guard = Guard.instance,
    lifetime: ExpressionProperty<Int>? = null,
    name: ExpressionProperty<String>? = null,
    scope: ExpressionProperty<ActionSetStoredValue.Scope>? = null,
): ActionSetStoredValue = ActionSetStoredValue(
    ActionSetStoredValue.Properties(
        lifetime = lifetime ?: properties.lifetime,
        name = name ?: properties.name,
        scope = scope ?: properties.scope,
        value = properties.value,
    )
)

@Generated
fun ActionSetStoredValue.asList() = listOf(this)

@Generated
fun ActionSetStoredValue.Scope.asList() = listOf(this)
