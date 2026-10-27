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
 * Can be created using the method [withoutProperties].
 * 
 * Required parameters: `type`.
 */
@Generated
@JsonSerialize(using = WithoutPropertiesSerializer::class)
data object WithoutProperties : Entity

@Generated
fun DivScope.withoutProperties(): WithoutProperties = WithoutProperties

@Generated
fun WithoutProperties.asList() = listOf(this)
