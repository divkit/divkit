@file:Suppress("unused")

package divan

import com.fasterxml.jackson.annotation.JsonValue
import divan.annotation.Generated
import divan.scope.DivScope
import kotlin.String
import kotlin.Suppress

@Generated
sealed class EnumValue(
    @JsonValue
    val serialized: String,
)

@Generated
object FirstEnumValue : EnumValue("first"),
        Entity_with_optional_string_enum_property.StringEnumProperty,
        Entity_with_string_enum_property.StringEnumProperty,
        Entity_with_string_enum_property_with_default_value.Value,
        Entity_with_array_of_enums.Items.Items

@Generated
object SecondEnumValue : EnumValue("second"),
        Entity_with_optional_string_enum_property.StringEnumProperty,
        Entity_with_string_enum_property.StringEnumProperty,
        Entity_with_string_enum_property_with_default_value.Value,
        Entity_with_array_of_enums.Items.Items

@Generated
object ThirdEnumValue : EnumValue("third"),
        Entity_with_string_enum_property_with_default_value.Value

@Generated
val DivScope.first: FirstEnumValue
    get() = FirstEnumValue

@Generated
val DivScope.second: SecondEnumValue
    get() = SecondEnumValue

@Generated
val DivScope.third: ThirdEnumValue
    get() = ThirdEnumValue
