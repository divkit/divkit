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
 * Can be created using the method [withDefault].
 */
@Generated
data object WithDefault : EnumWithDefaultType {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = mapOf("type" to "default")
}

@Generated
fun DivScope.withDefault(): WithDefault = WithDefault

@Generated
fun WithDefault.asList() = listOf(this)
