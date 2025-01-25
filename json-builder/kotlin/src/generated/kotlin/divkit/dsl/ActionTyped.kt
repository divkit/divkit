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
 * Possible values: [ActionAnimatorStart], [ActionAnimatorStop], [ActionArrayInsertValue], [ActionArrayRemoveValue], [ActionArraySetValue], [ActionClearFocus], [ActionCopyToClipboard], [ActionDictSetValue], [ActionDownload], [ActionFocusElement], [ActionHideTooltip], [ActionScrollBy], [ActionScrollTo], [ActionSetState], [ActionSetStoredValue], [ActionSetVariable], [ActionShowTooltip], [ActionSubmit], [ActionTimer], [ActionVideo].
 */
@Generated
sealed interface ActionTyped

@Generated
fun ActionTyped.asList() = listOf(this)
