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
 * A trigger that causes an action when activated.
 * 
 * Can be created using the method [trigger].
 * 
 * Required parameters: `condition, actions`.
 */
@Generated
data class Trigger internal constructor(
    @JsonIgnore
    val properties: Properties,
) {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(emptyMap())

    operator fun plus(additive: Properties): Trigger = Trigger(
        Properties(
            actions = additive.actions ?: properties.actions,
            condition = additive.condition ?: properties.condition,
            mode = additive.mode ?: properties.mode,
        )
    )

    data class Properties internal constructor(
        /**
         * Action when a trigger is activated.
         */
        val actions: Property<List<Action>>?,
        /**
         * Condition for activating a trigger. For example, `liked && subscribed`.
         */
        val condition: Property<Boolean>?,
        /**
         * Trigger activation mode:<li>`on_condition` — a trigger is activated when the condition changes from `false` to `true`;</li><li>`on_variable` — a trigger is activated when the condition is met and the variable value changes.</li>
         * Default value: `on_condition`.
         */
        val mode: Property<Mode>?,
    ) {
        internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
            val result = mutableMapOf<String, Any>()
            result.putAll(properties)
            result.tryPutProperty("actions", actions)
            result.tryPutProperty("condition", condition)
            result.tryPutProperty("mode", mode)
            return result
        }
    }

    /**
     * Trigger activation mode:<li>`on_condition` — a trigger is activated when the condition changes from `false` to `true`;</li><li>`on_variable` — a trigger is activated when the condition is met and the variable value changes.</li>
     * 
     * Possible values: [on_condition], [on_variable].
     */
    @Generated
    sealed interface Mode
}

/**
 * @param actions Action when a trigger is activated.
 * @param condition Condition for activating a trigger. For example, `liked && subscribed`.
 * @param mode Trigger activation mode:<li>`on_condition` — a trigger is activated when the condition changes from `false` to `true`;</li><li>`on_variable` — a trigger is activated when the condition is met and the variable value changes.</li>
 */
@Generated
fun DivScope.trigger(
    `use named arguments`: Guard = Guard.instance,
    actions: List<Action>? = null,
    condition: Boolean? = null,
    mode: Trigger.Mode? = null,
): Trigger = Trigger(
    Trigger.Properties(
        actions = valueOrNull(actions),
        condition = valueOrNull(condition),
        mode = valueOrNull(mode),
    )
)

/**
 * @param actions Action when a trigger is activated.
 * @param condition Condition for activating a trigger. For example, `liked && subscribed`.
 * @param mode Trigger activation mode:<li>`on_condition` — a trigger is activated when the condition changes from `false` to `true`;</li><li>`on_variable` — a trigger is activated when the condition is met and the variable value changes.</li>
 */
@Generated
fun DivScope.triggerProps(
    `use named arguments`: Guard = Guard.instance,
    actions: List<Action>? = null,
    condition: Boolean? = null,
    mode: Trigger.Mode? = null,
) = Trigger.Properties(
    actions = valueOrNull(actions),
    condition = valueOrNull(condition),
    mode = valueOrNull(mode),
)

/**
 * @param actions Action when a trigger is activated.
 * @param condition Condition for activating a trigger. For example, `liked && subscribed`.
 * @param mode Trigger activation mode:<li>`on_condition` — a trigger is activated when the condition changes from `false` to `true`;</li><li>`on_variable` — a trigger is activated when the condition is met and the variable value changes.</li>
 */
@Generated
fun TemplateScope.triggerRefs(
    `use named arguments`: Guard = Guard.instance,
    actions: ReferenceProperty<List<Action>>? = null,
    condition: ReferenceProperty<Boolean>? = null,
    mode: ReferenceProperty<Trigger.Mode>? = null,
) = Trigger.Properties(
    actions = actions,
    condition = condition,
    mode = mode,
)

/**
 * @param actions Action when a trigger is activated.
 * @param condition Condition for activating a trigger. For example, `liked && subscribed`.
 * @param mode Trigger activation mode:<li>`on_condition` — a trigger is activated when the condition changes from `false` to `true`;</li><li>`on_variable` — a trigger is activated when the condition is met and the variable value changes.</li>
 */
@Generated
fun Trigger.override(
    `use named arguments`: Guard = Guard.instance,
    actions: List<Action>? = null,
    condition: Boolean? = null,
    mode: Trigger.Mode? = null,
): Trigger = Trigger(
    Trigger.Properties(
        actions = valueOrNull(actions) ?: properties.actions,
        condition = valueOrNull(condition) ?: properties.condition,
        mode = valueOrNull(mode) ?: properties.mode,
    )
)

/**
 * @param actions Action when a trigger is activated.
 * @param condition Condition for activating a trigger. For example, `liked && subscribed`.
 * @param mode Trigger activation mode:<li>`on_condition` — a trigger is activated when the condition changes from `false` to `true`;</li><li>`on_variable` — a trigger is activated when the condition is met and the variable value changes.</li>
 */
@Generated
fun Trigger.defer(
    `use named arguments`: Guard = Guard.instance,
    actions: ReferenceProperty<List<Action>>? = null,
    condition: ReferenceProperty<Boolean>? = null,
    mode: ReferenceProperty<Trigger.Mode>? = null,
): Trigger = Trigger(
    Trigger.Properties(
        actions = actions ?: properties.actions,
        condition = condition ?: properties.condition,
        mode = mode ?: properties.mode,
    )
)

/**
 * @param actions Action when a trigger is activated.
 * @param condition Condition for activating a trigger. For example, `liked && subscribed`.
 * @param mode Trigger activation mode:<li>`on_condition` — a trigger is activated when the condition changes from `false` to `true`;</li><li>`on_variable` — a trigger is activated when the condition is met and the variable value changes.</li>
 */
@Generated
fun Trigger.modify(
    `use named arguments`: Guard = Guard.instance,
    actions: Property<List<Action>>? = null,
    condition: Property<Boolean>? = null,
    mode: Property<Trigger.Mode>? = null,
): Trigger = Trigger(
    Trigger.Properties(
        actions = actions ?: properties.actions,
        condition = condition ?: properties.condition,
        mode = mode ?: properties.mode,
    )
)

/**
 * @param condition Condition for activating a trigger. For example, `liked && subscribed`.
 * @param mode Trigger activation mode:<li>`on_condition` — a trigger is activated when the condition changes from `false` to `true`;</li><li>`on_variable` — a trigger is activated when the condition is met and the variable value changes.</li>
 */
@Generated
fun Trigger.evaluate(
    `use named arguments`: Guard = Guard.instance,
    condition: ExpressionProperty<Boolean>? = null,
    mode: ExpressionProperty<Trigger.Mode>? = null,
): Trigger = Trigger(
    Trigger.Properties(
        actions = properties.actions,
        condition = condition ?: properties.condition,
        mode = mode ?: properties.mode,
    )
)

@Generated
fun Trigger.asList() = listOf(this)

@Generated
fun Trigger.Mode.asList() = listOf(this)
