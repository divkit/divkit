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
 * Modal mode. Clicks outside do not pass through to underlying elements. Restricts focus to the tooltip. Back button on Android and back gesture on iOS close the tooltip.
 * 
 * Can be created using the method [tooltipModeModal].
 * 
 * Required parameters: `type`.
 */
@Generated
data object TooltipModeModal : TooltipMode {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = mapOf("type" to "modal")
}

@Generated
fun DivScope.tooltipModeModal(): TooltipModeModal = TooltipModeModal

@Generated
fun TooltipModeModal.asList() = listOf(this)
