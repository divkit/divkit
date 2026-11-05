@file:Suppress(
    "unused",
    "UNUSED_PARAMETER",
)

package divkit.dsl

import com.fasterxml.jackson.annotation.JsonValue
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import divkit.dsl.annotation.*
import divkit.dsl.core.*
import divkit.dsl.scope.*
import divkit.dsl.serialization.*
import kotlin.Any
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.Map

/**
 * A menu item available through the accessibility service. Selecting it executes `actions`.
 * 
 * Can be created using the method [accessibilityMenuItem].
 * 
 * Required parameters: `label, actions`.
 */
@Generated
@JsonSerialize(using = AccessibilityMenuItemSerializer::class)
@ExposedCopyVisibility
data class AccessibilityMenuItem internal constructor(
    val properties: Properties,
) {
    operator fun plus(additive: Properties): AccessibilityMenuItem = AccessibilityMenuItem(
        Properties(
            actions = additive.actions ?: properties.actions,
            label = additive.label ?: properties.label,
        )
    )

    @ExposedCopyVisibility
    data class Properties internal constructor(
        /**
         * Actions executed when this menu item is selected. If this list is empty or all its actions are disabled, the item is excluded from the commands available for execution.
         */
        val actions: Property<List<Action>>?,
        /**
         * Text read aloud by the accessibility service when navigating to the menu item.
         */
        val label: Property<String>?,
    ) {
        internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
            val result = mutableMapOf<String, Any>()
            result.putAll(properties)
            result.tryPutProperty("actions", actions)
            result.tryPutProperty("label", label)
            return result
        }
    }
}

/**
 * @param actions Actions executed when this menu item is selected. If this list is empty or all its actions are disabled, the item is excluded from the commands available for execution.
 * @param label Text read aloud by the accessibility service when navigating to the menu item.
 */
@Generated
fun DivScope.accessibilityMenuItem(
    `use named arguments`: Guard = Guard.instance,
    actions: List<Action>? = null,
    label: String? = null,
): AccessibilityMenuItem = AccessibilityMenuItem(
    AccessibilityMenuItem.Properties(
        actions = valueOrNull(actions),
        label = valueOrNull(label),
    )
)

/**
 * @param actions Actions executed when this menu item is selected. If this list is empty or all its actions are disabled, the item is excluded from the commands available for execution.
 * @param label Text read aloud by the accessibility service when navigating to the menu item.
 */
@Generated
fun DivScope.accessibilityMenuItemProps(
    `use named arguments`: Guard = Guard.instance,
    actions: List<Action>? = null,
    label: String? = null,
) = AccessibilityMenuItem.Properties(
    actions = valueOrNull(actions),
    label = valueOrNull(label),
)

/**
 * @param actions Actions executed when this menu item is selected. If this list is empty or all its actions are disabled, the item is excluded from the commands available for execution.
 * @param label Text read aloud by the accessibility service when navigating to the menu item.
 */
@Generated
fun TemplateScope.accessibilityMenuItemRefs(
    `use named arguments`: Guard = Guard.instance,
    actions: ReferenceProperty<List<Action>>? = null,
    label: ReferenceProperty<String>? = null,
) = AccessibilityMenuItem.Properties(
    actions = actions,
    label = label,
)

/**
 * @param actions Actions executed when this menu item is selected. If this list is empty or all its actions are disabled, the item is excluded from the commands available for execution.
 * @param label Text read aloud by the accessibility service when navigating to the menu item.
 */
@Generated
fun AccessibilityMenuItem.override(
    `use named arguments`: Guard = Guard.instance,
    actions: List<Action>? = null,
    label: String? = null,
): AccessibilityMenuItem = AccessibilityMenuItem(
    AccessibilityMenuItem.Properties(
        actions = valueOrNull(actions) ?: properties.actions,
        label = valueOrNull(label) ?: properties.label,
    )
)

/**
 * @param actions Actions executed when this menu item is selected. If this list is empty or all its actions are disabled, the item is excluded from the commands available for execution.
 * @param label Text read aloud by the accessibility service when navigating to the menu item.
 */
@Generated
fun AccessibilityMenuItem.defer(
    `use named arguments`: Guard = Guard.instance,
    actions: ReferenceProperty<List<Action>>? = null,
    label: ReferenceProperty<String>? = null,
): AccessibilityMenuItem = AccessibilityMenuItem(
    AccessibilityMenuItem.Properties(
        actions = actions ?: properties.actions,
        label = label ?: properties.label,
    )
)

/**
 * @param actions Actions executed when this menu item is selected. If this list is empty or all its actions are disabled, the item is excluded from the commands available for execution.
 * @param label Text read aloud by the accessibility service when navigating to the menu item.
 */
@Generated
fun AccessibilityMenuItem.modify(
    `use named arguments`: Guard = Guard.instance,
    actions: Property<List<Action>>? = null,
    label: Property<String>? = null,
): AccessibilityMenuItem = AccessibilityMenuItem(
    AccessibilityMenuItem.Properties(
        actions = actions ?: properties.actions,
        label = label ?: properties.label,
    )
)

/**
 * @param label Text read aloud by the accessibility service when navigating to the menu item.
 */
@Generated
fun AccessibilityMenuItem.evaluate(
    `use named arguments`: Guard = Guard.instance,
    label: ExpressionProperty<String>? = null,
): AccessibilityMenuItem = AccessibilityMenuItem(
    AccessibilityMenuItem.Properties(
        actions = properties.actions,
        label = label ?: properties.label,
    )
)

@Generated
fun AccessibilityMenuItem.asList() = listOf(this)
