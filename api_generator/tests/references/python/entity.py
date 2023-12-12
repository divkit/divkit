# Generated code. Do not modify.

from __future__ import annotations
from pydivkit.core import BaseDiv, Field, Expr
import enum
import typing

from . import entity_with_array
from . import entity_with_array_of_enums
from . import entity_with_array_of_expressions
from . import entity_with_array_of_nested_items
from . import entity_with_array_with_transform
from . import entity_with_complex_property
from . import entity_with_complex_property_with_default_value
from . import entity_with_entity_property
from . import entity_with_optional_complex_property
from . import entity_with_optional_property
from . import entity_with_optional_string_enum_property
from . import entity_with_property_with_default_value
from . import entity_with_raw_array
from . import entity_with_required_property
from . import entity_with_simple_properties
from . import entity_with_string_array_property
from . import entity_with_string_enum_property
from . import entity_with_string_enum_property_with_default_value
from . import entity_without_properties

from typing import Union

Entity = Union[
    entity_with_array.EntityWithArray,
    entity_with_array_of_enums.EntityWithArrayOfEnums,
    entity_with_array_of_expressions.EntityWithArrayOfExpressions,
    entity_with_array_of_nested_items.EntityWithArrayOfNestedItems,
    entity_with_array_with_transform.EntityWithArrayWithTransform,
    entity_with_complex_property.EntityWithComplexProperty,
    entity_with_complex_property_with_default_value.EntityWithComplexPropertyWithDefaultValue,
    entity_with_entity_property.EntityWithEntityProperty,
    entity_with_optional_complex_property.EntityWithOptionalComplexProperty,
    entity_with_optional_property.EntityWithOptionalProperty,
    entity_with_optional_string_enum_property.EntityWithOptionalStringEnumProperty,
    entity_with_property_with_default_value.EntityWithPropertyWithDefaultValue,
    entity_with_raw_array.EntityWithRawArray,
    entity_with_required_property.EntityWithRequiredProperty,
    entity_with_simple_properties.EntityWithSimpleProperties,
    entity_with_string_array_property.EntityWithStringArrayProperty,
    entity_with_string_enum_property.EntityWithStringEnumProperty,
    entity_with_string_enum_property_with_default_value.EntityWithStringEnumPropertyWithDefaultValue,
    entity_without_properties.EntityWithoutProperties,
]
