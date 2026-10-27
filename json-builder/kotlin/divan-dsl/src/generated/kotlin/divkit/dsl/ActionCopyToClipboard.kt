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
 * Copies data to the clipboard.
 * 
 * Can be created using the method [actionCopyToClipboard].
 * 
 * Required parameters: `type, content`.
 */
@Generated
@JsonSerialize(using = ActionCopyToClipboardSerializer::class)
@ExposedCopyVisibility
data class ActionCopyToClipboard internal constructor(
    val properties: Properties,
) : ActionTyped {
    operator fun plus(additive: Properties): ActionCopyToClipboard = ActionCopyToClipboard(
        Properties(
            content = additive.content ?: properties.content,
        )
    )

    @ExposedCopyVisibility
    data class Properties internal constructor(
        val content: Property<ActionCopyToClipboardContent>?,
    ) {
        internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
            val result = mutableMapOf<String, Any>()
            result.putAll(properties)
            result.tryPutProperty("content", content)
            return result
        }
    }
}

@Generated
fun DivScope.actionCopyToClipboard(
    `use named arguments`: Guard = Guard.instance,
    content: ActionCopyToClipboardContent? = null,
): ActionCopyToClipboard = ActionCopyToClipboard(
    ActionCopyToClipboard.Properties(
        content = valueOrNull(content),
    )
)

@Generated
fun DivScope.actionCopyToClipboardProps(
    `use named arguments`: Guard = Guard.instance,
    content: ActionCopyToClipboardContent? = null,
) = ActionCopyToClipboard.Properties(
    content = valueOrNull(content),
)

@Generated
fun TemplateScope.actionCopyToClipboardRefs(
    `use named arguments`: Guard = Guard.instance,
    content: ReferenceProperty<ActionCopyToClipboardContent>? = null,
) = ActionCopyToClipboard.Properties(
    content = content,
)

@Generated
fun ActionCopyToClipboard.override(
    `use named arguments`: Guard = Guard.instance,
    content: ActionCopyToClipboardContent? = null,
): ActionCopyToClipboard = ActionCopyToClipboard(
    ActionCopyToClipboard.Properties(
        content = valueOrNull(content) ?: properties.content,
    )
)

@Generated
fun ActionCopyToClipboard.defer(
    `use named arguments`: Guard = Guard.instance,
    content: ReferenceProperty<ActionCopyToClipboardContent>? = null,
): ActionCopyToClipboard = ActionCopyToClipboard(
    ActionCopyToClipboard.Properties(
        content = content ?: properties.content,
    )
)

@Generated
fun ActionCopyToClipboard.modify(
    `use named arguments`: Guard = Guard.instance,
    content: Property<ActionCopyToClipboardContent>? = null,
): ActionCopyToClipboard = ActionCopyToClipboard(
    ActionCopyToClipboard.Properties(
        content = content ?: properties.content,
    )
)

@Generated
fun ActionCopyToClipboard.asList() = listOf(this)
