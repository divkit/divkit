@file:Suppress(
    "unused",
    "UNUSED_PARAMETER",
)

package divan

import com.fasterxml.jackson.annotation.JsonAnyGetter
import com.fasterxml.jackson.annotation.JsonIgnore
import divan.annotation.Generated
import divan.core.Guard
import divan.core.Property
import divan.core.ReferenceProperty
import divan.core.tryPutProperty
import divan.core.valueOrNull
import divan.scope.DivScope
import divan.scope.TemplateScope
import kotlin.Any
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.Map

/**
 * Can be created using the method [withoutDefault].
 * 
 * Required parameters: `type`.
 */
@Generated
data object WithoutDefault : EnumWithDefaultType {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = mapOf("type" to "non_default")
}

@Generated
fun DivScope.withoutDefault(): WithoutDefault = WithoutDefault

@Generated
fun WithoutDefault.asList() = listOf(this)
