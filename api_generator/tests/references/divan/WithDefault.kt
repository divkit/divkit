@file:Suppress(
    "unused",
    "UNUSED_PARAMETER",
)

package divan

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import divan.annotation.Generated
import divan.core.Guard
import divan.core.Property
import divan.core.ReferenceProperty
import divan.core.tryPutProperty
import divan.core.valueOrNull
import divan.scope.DivScope
import divan.scope.TemplateScope
import divan.serialization.*
import kotlin.Any
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.Map

/**
 * Can be created using the method [withDefault].
 */
@Generated
@JsonSerialize(using = WithDefaultSerializer::class)
data object WithDefault : EnumWithDefaultType

@Generated
fun DivScope.withDefault(): WithDefault = WithDefault

@Generated
fun WithDefault.asList() = listOf(this)
