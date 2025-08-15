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
 * Possible values: [WithArray], [WithArrayOfEnums], [WithArrayOfExpressions], [WithArrayOfNestedItems], [WithArrayWithTransform], [WithComplexProperty], [WithComplexPropertyWithDefaultValue], [WithEntityProperty], [WithOptionalComplexProperty], [WithOptionalProperty], [WithOptionalStringEnumProperty], [WithPropertyWithDefaultValue], [WithRawArray], [WithRequiredProperty], [WithSimpleProperties], [WithStringArrayProperty], [WithStringEnumProperty], [WithStringEnumPropertyWithDefaultValue], [WithoutProperties].
 */
@Generated
sealed interface Entity

@Generated
fun Entity.asList() = listOf(this)
