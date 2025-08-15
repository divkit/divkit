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
 * Non-modal mode. Clicks outside pass through to underlying elements. Does not restrict focus to the tooltip. Back button on Android and back gesture on iOS do not close the tooltip.
 * 
 * Can be created using the method [tooltipModeNonModal].
 * 
 * Required parameters: `type`.
 */
@Generated
data object TooltipModeNonModal : TooltipMode {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = mapOf("type" to "non_modal")
}

@Generated
fun DivScope.tooltipModeNonModal(): TooltipModeNonModal = TooltipModeNonModal

@Generated
fun TooltipModeNonModal.asList() = listOf(this)
