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
 * Controls given video.
 * 
 * Can be created using the method [actionVideo].
 * 
 * Required parameters: `type, id, action`.
 */
@Generated
class ActionVideo internal constructor(
    @JsonIgnore
    val properties: Properties,
) : ActionTyped {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "video")
    )

    operator fun plus(additive: Properties): ActionVideo = ActionVideo(
        Properties(
            action = additive.action ?: properties.action,
            id = additive.id ?: properties.id,
        )
    )

    class Properties internal constructor(
        /**
         * Defines video action:<li>`start` - play if it is ready or plans to play when video becomes ready;</li><li>`pause` - pauses video playback.</li>
         */
        val action: Property<Action>?,
        /**
         * Video identifier.
         */
        val id: Property<String>?,
    ) {
        internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
            val result = mutableMapOf<String, Any>()
            result.putAll(properties)
            result.tryPutProperty("action", action)
            result.tryPutProperty("id", id)
            return result
        }
    }

    /**
     * Defines video action:<li>`start` - play if it is ready or plans to play when video becomes ready;</li><li>`pause` - pauses video playback.</li>
     * 
     * Possible values: [start], [pause].
     */
    @Generated
    sealed interface Action
}

/**
 * @param action Defines video action:<li>`start` - play if it is ready or plans to play when video becomes ready;</li><li>`pause` - pauses video playback.</li>
 * @param id Video identifier.
 */
@Generated
fun DivScope.actionVideo(
    `use named arguments`: Guard = Guard.instance,
    action: ActionVideo.Action? = null,
    id: String? = null,
): ActionVideo = ActionVideo(
    ActionVideo.Properties(
        action = valueOrNull(action),
        id = valueOrNull(id),
    )
)

/**
 * @param action Defines video action:<li>`start` - play if it is ready or plans to play when video becomes ready;</li><li>`pause` - pauses video playback.</li>
 * @param id Video identifier.
 */
@Generated
fun DivScope.actionVideoProps(
    `use named arguments`: Guard = Guard.instance,
    action: ActionVideo.Action? = null,
    id: String? = null,
) = ActionVideo.Properties(
    action = valueOrNull(action),
    id = valueOrNull(id),
)

/**
 * @param action Defines video action:<li>`start` - play if it is ready or plans to play when video becomes ready;</li><li>`pause` - pauses video playback.</li>
 * @param id Video identifier.
 */
@Generated
fun TemplateScope.actionVideoRefs(
    `use named arguments`: Guard = Guard.instance,
    action: ReferenceProperty<ActionVideo.Action>? = null,
    id: ReferenceProperty<String>? = null,
) = ActionVideo.Properties(
    action = action,
    id = id,
)

/**
 * @param action Defines video action:<li>`start` - play if it is ready or plans to play when video becomes ready;</li><li>`pause` - pauses video playback.</li>
 * @param id Video identifier.
 */
@Generated
fun ActionVideo.override(
    `use named arguments`: Guard = Guard.instance,
    action: ActionVideo.Action? = null,
    id: String? = null,
): ActionVideo = ActionVideo(
    ActionVideo.Properties(
        action = valueOrNull(action) ?: properties.action,
        id = valueOrNull(id) ?: properties.id,
    )
)

/**
 * @param action Defines video action:<li>`start` - play if it is ready or plans to play when video becomes ready;</li><li>`pause` - pauses video playback.</li>
 * @param id Video identifier.
 */
@Generated
fun ActionVideo.defer(
    `use named arguments`: Guard = Guard.instance,
    action: ReferenceProperty<ActionVideo.Action>? = null,
    id: ReferenceProperty<String>? = null,
): ActionVideo = ActionVideo(
    ActionVideo.Properties(
        action = action ?: properties.action,
        id = id ?: properties.id,
    )
)

/**
 * @param action Defines video action:<li>`start` - play if it is ready or plans to play when video becomes ready;</li><li>`pause` - pauses video playback.</li>
 * @param id Video identifier.
 */
@Generated
fun ActionVideo.evaluate(
    `use named arguments`: Guard = Guard.instance,
    action: ExpressionProperty<ActionVideo.Action>? = null,
    id: ExpressionProperty<String>? = null,
): ActionVideo = ActionVideo(
    ActionVideo.Properties(
        action = action ?: properties.action,
        id = id ?: properties.id,
    )
)

@Generated
fun ActionVideo.asList() = listOf(this)

@Generated
fun ActionVideo.Action.asList() = listOf(this)
