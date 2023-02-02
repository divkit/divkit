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

@Generated
sealed class EnumValue(
    @JsonValue
    val serialized: String,
)

@Generated
object FirstEnumValue : EnumValue("first"),
    EntityWithArrayOfEnums.Item,
    EntityWithOptionalStringEnumProperty.Property,
    EntityWithStringEnumProperty.Property,
    EntityWithStringEnumPropertyWithDefaultValue.Value

@Generated
object SecondEnumValue : EnumValue("second"),
    EntityWithArrayOfEnums.Item,
    EntityWithOptionalStringEnumProperty.Property,
    EntityWithStringEnumProperty.Property,
    EntityWithStringEnumPropertyWithDefaultValue.Value

@Generated
object ThirdEnumValue : EnumValue("third"),
    EntityWithStringEnumPropertyWithDefaultValue.Value

@Generated
val DivScope.first: FirstEnumValue
    get() = FirstEnumValue

@Generated
val DivScope.second: SecondEnumValue
    get() = SecondEnumValue

@Generated
val DivScope.third: ThirdEnumValue
    get() = ThirdEnumValue
