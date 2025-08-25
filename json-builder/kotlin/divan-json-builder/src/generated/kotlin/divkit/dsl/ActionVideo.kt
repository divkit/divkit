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
 * Manages video playback.
 * 
 * Can be created using the method [actionVideo].
 * 
 * Required parameters: `type, id, action`.
 */
@Generated
@ExposedCopyVisibility
data class ActionVideo internal constructor(
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

    @ExposedCopyVisibility
    data class Properties internal constructor(
        /**
         * Defines the action for the video: <li>`start` — starts playing the video if the video is ready to be played, or schedules playback</li><li>`pause' — stops the video playback</li>
         */
        val action: Property<Action>?,
        /**
         * Video ID.
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
     * Defines the action for the video: <li>`start` — starts playing the video if the video is ready to be played, or schedules playback</li><li>`pause' — stops the video playback</li>
     * 
     * Possible values: [start], [pause].
     */
    @Generated
    sealed interface Action
}

/**
 * @param action Defines the action for the video: <li>`start` — starts playing the video if the video is ready to be played, or schedules playback</li><li>`pause' — stops the video playback</li>
 * @param id Video ID.
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
 * @param action Defines the action for the video: <li>`start` — starts playing the video if the video is ready to be played, or schedules playback</li><li>`pause' — stops the video playback</li>
 * @param id Video ID.
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
 * @param action Defines the action for the video: <li>`start` — starts playing the video if the video is ready to be played, or schedules playback</li><li>`pause' — stops the video playback</li>
 * @param id Video ID.
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
 * @param action Defines the action for the video: <li>`start` — starts playing the video if the video is ready to be played, or schedules playback</li><li>`pause' — stops the video playback</li>
 * @param id Video ID.
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
 * @param action Defines the action for the video: <li>`start` — starts playing the video if the video is ready to be played, or schedules playback</li><li>`pause' — stops the video playback</li>
 * @param id Video ID.
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
 * @param action Defines the action for the video: <li>`start` — starts playing the video if the video is ready to be played, or schedules playback</li><li>`pause' — stops the video playback</li>
 * @param id Video ID.
 */
@Generated
fun ActionVideo.modify(
    `use named arguments`: Guard = Guard.instance,
    action: Property<ActionVideo.Action>? = null,
    id: Property<String>? = null,
): ActionVideo = ActionVideo(
    ActionVideo.Properties(
        action = action ?: properties.action,
        id = id ?: properties.id,
    )
)

/**
 * @param action Defines the action for the video: <li>`start` — starts playing the video if the video is ready to be played, or schedules playback</li><li>`pause' — stops the video playback</li>
 * @param id Video ID.
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
