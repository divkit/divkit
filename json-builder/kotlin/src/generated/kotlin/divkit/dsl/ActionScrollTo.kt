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
 * Scrolls or switches container to given destination provided by `destination`.
 * 
 * Can be created using the method [actionScrollTo].
 * 
 * Required parameters: `type, id, destination`.
 */
@Generated
class ActionScrollTo internal constructor(
    @JsonIgnore
    val properties: Properties,
) : ActionTyped {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "scroll_to")
    )

    operator fun plus(additive: Properties): ActionScrollTo = ActionScrollTo(
        Properties(
            animated = additive.animated ?: properties.animated,
            destination = additive.destination ?: properties.destination,
            id = additive.id ?: properties.id,
        )
    )

    class Properties internal constructor(
        /**
         * If `true` (default value) scroll will be animated, else not.
         * Default value: `true`.
         */
        val animated: Property<Boolean>?,
        /**
         * Specifies destination of scroll:<li>`index` - scroll or switch to item with index provided by `value`;</li><li>`offset` - scroll to position measured in `dp` from container's start and provided by `value`. Applicable only in `gallery`;</li><li>`start` - scrolls to start of container;</li><li>`end` - scrolls to end of container.</li>.
         */
        val destination: Property<ActionScrollDestination>?,
        /**
         * Identifier of the view that is going to be manipulated.
         */
        val id: Property<String>?,
    ) {
        internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
            val result = mutableMapOf<String, Any>()
            result.putAll(properties)
            result.tryPutProperty("animated", animated)
            result.tryPutProperty("destination", destination)
            result.tryPutProperty("id", id)
            return result
        }
    }
}

/**
 * @param animated If `true` (default value) scroll will be animated, else not.
 * @param destination Specifies destination of scroll:<li>`index` - scroll or switch to item with index provided by `value`;</li><li>`offset` - scroll to position measured in `dp` from container's start and provided by `value`. Applicable only in `gallery`;</li><li>`start` - scrolls to start of container;</li><li>`end` - scrolls to end of container.</li>.
 * @param id Identifier of the view that is going to be manipulated.
 */
@Generated
fun DivScope.actionScrollTo(
    `use named arguments`: Guard = Guard.instance,
    animated: Boolean? = null,
    destination: ActionScrollDestination? = null,
    id: String? = null,
): ActionScrollTo = ActionScrollTo(
    ActionScrollTo.Properties(
        animated = valueOrNull(animated),
        destination = valueOrNull(destination),
        id = valueOrNull(id),
    )
)

/**
 * @param animated If `true` (default value) scroll will be animated, else not.
 * @param destination Specifies destination of scroll:<li>`index` - scroll or switch to item with index provided by `value`;</li><li>`offset` - scroll to position measured in `dp` from container's start and provided by `value`. Applicable only in `gallery`;</li><li>`start` - scrolls to start of container;</li><li>`end` - scrolls to end of container.</li>.
 * @param id Identifier of the view that is going to be manipulated.
 */
@Generated
fun DivScope.actionScrollToProps(
    `use named arguments`: Guard = Guard.instance,
    animated: Boolean? = null,
    destination: ActionScrollDestination? = null,
    id: String? = null,
) = ActionScrollTo.Properties(
    animated = valueOrNull(animated),
    destination = valueOrNull(destination),
    id = valueOrNull(id),
)

/**
 * @param animated If `true` (default value) scroll will be animated, else not.
 * @param destination Specifies destination of scroll:<li>`index` - scroll or switch to item with index provided by `value`;</li><li>`offset` - scroll to position measured in `dp` from container's start and provided by `value`. Applicable only in `gallery`;</li><li>`start` - scrolls to start of container;</li><li>`end` - scrolls to end of container.</li>.
 * @param id Identifier of the view that is going to be manipulated.
 */
@Generated
fun TemplateScope.actionScrollToRefs(
    `use named arguments`: Guard = Guard.instance,
    animated: ReferenceProperty<Boolean>? = null,
    destination: ReferenceProperty<ActionScrollDestination>? = null,
    id: ReferenceProperty<String>? = null,
) = ActionScrollTo.Properties(
    animated = animated,
    destination = destination,
    id = id,
)

/**
 * @param animated If `true` (default value) scroll will be animated, else not.
 * @param destination Specifies destination of scroll:<li>`index` - scroll or switch to item with index provided by `value`;</li><li>`offset` - scroll to position measured in `dp` from container's start and provided by `value`. Applicable only in `gallery`;</li><li>`start` - scrolls to start of container;</li><li>`end` - scrolls to end of container.</li>.
 * @param id Identifier of the view that is going to be manipulated.
 */
@Generated
fun ActionScrollTo.override(
    `use named arguments`: Guard = Guard.instance,
    animated: Boolean? = null,
    destination: ActionScrollDestination? = null,
    id: String? = null,
): ActionScrollTo = ActionScrollTo(
    ActionScrollTo.Properties(
        animated = valueOrNull(animated) ?: properties.animated,
        destination = valueOrNull(destination) ?: properties.destination,
        id = valueOrNull(id) ?: properties.id,
    )
)

/**
 * @param animated If `true` (default value) scroll will be animated, else not.
 * @param destination Specifies destination of scroll:<li>`index` - scroll or switch to item with index provided by `value`;</li><li>`offset` - scroll to position measured in `dp` from container's start and provided by `value`. Applicable only in `gallery`;</li><li>`start` - scrolls to start of container;</li><li>`end` - scrolls to end of container.</li>.
 * @param id Identifier of the view that is going to be manipulated.
 */
@Generated
fun ActionScrollTo.defer(
    `use named arguments`: Guard = Guard.instance,
    animated: ReferenceProperty<Boolean>? = null,
    destination: ReferenceProperty<ActionScrollDestination>? = null,
    id: ReferenceProperty<String>? = null,
): ActionScrollTo = ActionScrollTo(
    ActionScrollTo.Properties(
        animated = animated ?: properties.animated,
        destination = destination ?: properties.destination,
        id = id ?: properties.id,
    )
)

/**
 * @param animated If `true` (default value) scroll will be animated, else not.
 * @param id Identifier of the view that is going to be manipulated.
 */
@Generated
fun ActionScrollTo.evaluate(
    `use named arguments`: Guard = Guard.instance,
    animated: ExpressionProperty<Boolean>? = null,
    id: ExpressionProperty<String>? = null,
): ActionScrollTo = ActionScrollTo(
    ActionScrollTo.Properties(
        animated = animated ?: properties.animated,
        destination = properties.destination,
        id = id ?: properties.id,
    )
)

@Generated
fun ActionScrollTo.asList() = listOf(this)
