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
 * Possible values: [ActionAnimatorStart], [ActionAnimatorStop], [ActionArrayInsertValue], [ActionArrayRemoveValue], [ActionArraySetValue], [ActionClearFocus], [ActionCopyToClipboard], [ActionDictSetValue], [ActionDownload], [ActionFocusElement], [ActionHaptic], [ActionHideTooltip], [ActionScrollBy], [ActionScrollTo], [ActionSetState], [ActionSetStoredValue], [ActionSetVariable], [ActionShowTooltip], [ActionSubmit], [ActionTimer], [ActionUpdateStructure], [ActionVideo], [ActionCustom], [ActionExtensionAction], [ActionSetCursorPosition].
 */
@Generated
sealed interface ActionTyped

@Generated
fun ActionTyped.asList() = listOf(this)
