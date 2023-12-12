// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import 'entity_with_array.dart';
import 'entity_with_array_of_enums.dart';
import 'entity_with_array_of_expressions.dart';
import 'entity_with_array_of_nested_items.dart';
import 'entity_with_array_with_transform.dart';
import 'entity_with_complex_property.dart';
import 'entity_with_complex_property_with_default_value.dart';
import 'entity_with_entity_property.dart';
import 'entity_with_optional_complex_property.dart';
import 'entity_with_optional_property.dart';
import 'entity_with_optional_string_enum_property.dart';
import 'entity_with_property_with_default_value.dart';
import 'entity_with_raw_array.dart';
import 'entity_with_required_property.dart';
import 'entity_with_simple_properties.dart';
import 'entity_with_string_array_property.dart';
import 'entity_with_string_enum_property.dart';
import 'entity_with_string_enum_property_with_default_value.dart';
import 'entity_without_properties.dart';

class Entity with EquatableMixin {
  const Entity(
    Object value
  ) : _value = value;

  final Object _value;

  @override
  List<Object?> get props => [_value];

  /// It may not work correctly so use [map] or [maybeMap]!
  Object get value {
    final value = _value;
    if(value is EntityWithArray) {
      return value;
    }
    if(value is EntityWithArrayOfEnums) {
      return value;
    }
    if(value is EntityWithArrayOfExpressions) {
      return value;
    }
    if(value is EntityWithArrayOfNestedItems) {
      return value;
    }
    if(value is EntityWithArrayWithTransform) {
      return value;
    }
    if(value is EntityWithComplexProperty) {
      return value;
    }
    if(value is EntityWithComplexPropertyWithDefaultValue) {
      return value;
    }
    if(value is EntityWithEntityProperty) {
      return value;
    }
    if(value is EntityWithOptionalComplexProperty) {
      return value;
    }
    if(value is EntityWithOptionalProperty) {
      return value;
    }
    if(value is EntityWithOptionalStringEnumProperty) {
      return value;
    }
    if(value is EntityWithPropertyWithDefaultValue) {
      return value;
    }
    if(value is EntityWithRawArray) {
      return value;
    }
    if(value is EntityWithRequiredProperty) {
      return value;
    }
    if(value is EntityWithSimpleProperties) {
      return value;
    }
    if(value is EntityWithStringArrayProperty) {
      return value;
    }
    if(value is EntityWithStringEnumProperty) {
      return value;
    }
    if(value is EntityWithStringEnumPropertyWithDefaultValue) {
      return value;
    }
    if(value is EntityWithoutProperties) {
      return value;
    }
    throw Exception("Type ${value.runtimeType.toString()} is not generalized in Entity");
  }

  T map<T>({
    required T Function(EntityWithArray) entityWithArray,
    required T Function(EntityWithArrayOfEnums) entityWithArrayOfEnums,
    required T Function(EntityWithArrayOfExpressions) entityWithArrayOfExpressions,
    required T Function(EntityWithArrayOfNestedItems) entityWithArrayOfNestedItems,
    required T Function(EntityWithArrayWithTransform) entityWithArrayWithTransform,
    required T Function(EntityWithComplexProperty) entityWithComplexProperty,
    required T Function(EntityWithComplexPropertyWithDefaultValue) entityWithComplexPropertyWithDefaultValue,
    required T Function(EntityWithEntityProperty) entityWithEntityProperty,
    required T Function(EntityWithOptionalComplexProperty) entityWithOptionalComplexProperty,
    required T Function(EntityWithOptionalProperty) entityWithOptionalProperty,
    required T Function(EntityWithOptionalStringEnumProperty) entityWithOptionalStringEnumProperty,
    required T Function(EntityWithPropertyWithDefaultValue) entityWithPropertyWithDefaultValue,
    required T Function(EntityWithRawArray) entityWithRawArray,
    required T Function(EntityWithRequiredProperty) entityWithRequiredProperty,
    required T Function(EntityWithSimpleProperties) entityWithSimpleProperties,
    required T Function(EntityWithStringArrayProperty) entityWithStringArrayProperty,
    required T Function(EntityWithStringEnumProperty) entityWithStringEnumProperty,
    required T Function(EntityWithStringEnumPropertyWithDefaultValue) entityWithStringEnumPropertyWithDefaultValue,
    required T Function(EntityWithoutProperties) entityWithoutProperties,
  }) {
    final value = _value;
    if(value is EntityWithArray) {
      return entityWithArray(value);
    }
    if(value is EntityWithArrayOfEnums) {
      return entityWithArrayOfEnums(value);
    }
    if(value is EntityWithArrayOfExpressions) {
      return entityWithArrayOfExpressions(value);
    }
    if(value is EntityWithArrayOfNestedItems) {
      return entityWithArrayOfNestedItems(value);
    }
    if(value is EntityWithArrayWithTransform) {
      return entityWithArrayWithTransform(value);
    }
    if(value is EntityWithComplexProperty) {
      return entityWithComplexProperty(value);
    }
    if(value is EntityWithComplexPropertyWithDefaultValue) {
      return entityWithComplexPropertyWithDefaultValue(value);
    }
    if(value is EntityWithEntityProperty) {
      return entityWithEntityProperty(value);
    }
    if(value is EntityWithOptionalComplexProperty) {
      return entityWithOptionalComplexProperty(value);
    }
    if(value is EntityWithOptionalProperty) {
      return entityWithOptionalProperty(value);
    }
    if(value is EntityWithOptionalStringEnumProperty) {
      return entityWithOptionalStringEnumProperty(value);
    }
    if(value is EntityWithPropertyWithDefaultValue) {
      return entityWithPropertyWithDefaultValue(value);
    }
    if(value is EntityWithRawArray) {
      return entityWithRawArray(value);
    }
    if(value is EntityWithRequiredProperty) {
      return entityWithRequiredProperty(value);
    }
    if(value is EntityWithSimpleProperties) {
      return entityWithSimpleProperties(value);
    }
    if(value is EntityWithStringArrayProperty) {
      return entityWithStringArrayProperty(value);
    }
    if(value is EntityWithStringEnumProperty) {
      return entityWithStringEnumProperty(value);
    }
    if(value is EntityWithStringEnumPropertyWithDefaultValue) {
      return entityWithStringEnumPropertyWithDefaultValue(value);
    }
    if(value is EntityWithoutProperties) {
      return entityWithoutProperties(value);
    }
    throw Exception("Type ${value.runtimeType.toString()} is not generalized in Entity");
  }

  T maybeMap<T>({
    T Function(EntityWithArray)? entityWithArray,
    T Function(EntityWithArrayOfEnums)? entityWithArrayOfEnums,
    T Function(EntityWithArrayOfExpressions)? entityWithArrayOfExpressions,
    T Function(EntityWithArrayOfNestedItems)? entityWithArrayOfNestedItems,
    T Function(EntityWithArrayWithTransform)? entityWithArrayWithTransform,
    T Function(EntityWithComplexProperty)? entityWithComplexProperty,
    T Function(EntityWithComplexPropertyWithDefaultValue)? entityWithComplexPropertyWithDefaultValue,
    T Function(EntityWithEntityProperty)? entityWithEntityProperty,
    T Function(EntityWithOptionalComplexProperty)? entityWithOptionalComplexProperty,
    T Function(EntityWithOptionalProperty)? entityWithOptionalProperty,
    T Function(EntityWithOptionalStringEnumProperty)? entityWithOptionalStringEnumProperty,
    T Function(EntityWithPropertyWithDefaultValue)? entityWithPropertyWithDefaultValue,
    T Function(EntityWithRawArray)? entityWithRawArray,
    T Function(EntityWithRequiredProperty)? entityWithRequiredProperty,
    T Function(EntityWithSimpleProperties)? entityWithSimpleProperties,
    T Function(EntityWithStringArrayProperty)? entityWithStringArrayProperty,
    T Function(EntityWithStringEnumProperty)? entityWithStringEnumProperty,
    T Function(EntityWithStringEnumPropertyWithDefaultValue)? entityWithStringEnumPropertyWithDefaultValue,
    T Function(EntityWithoutProperties)? entityWithoutProperties,
    required T Function() orElse,
  }) {
    final value = _value;
    if(value is EntityWithArray && entityWithArray != null) {
     return entityWithArray(value);
    }
    if(value is EntityWithArrayOfEnums && entityWithArrayOfEnums != null) {
     return entityWithArrayOfEnums(value);
    }
    if(value is EntityWithArrayOfExpressions && entityWithArrayOfExpressions != null) {
     return entityWithArrayOfExpressions(value);
    }
    if(value is EntityWithArrayOfNestedItems && entityWithArrayOfNestedItems != null) {
     return entityWithArrayOfNestedItems(value);
    }
    if(value is EntityWithArrayWithTransform && entityWithArrayWithTransform != null) {
     return entityWithArrayWithTransform(value);
    }
    if(value is EntityWithComplexProperty && entityWithComplexProperty != null) {
     return entityWithComplexProperty(value);
    }
    if(value is EntityWithComplexPropertyWithDefaultValue && entityWithComplexPropertyWithDefaultValue != null) {
     return entityWithComplexPropertyWithDefaultValue(value);
    }
    if(value is EntityWithEntityProperty && entityWithEntityProperty != null) {
     return entityWithEntityProperty(value);
    }
    if(value is EntityWithOptionalComplexProperty && entityWithOptionalComplexProperty != null) {
     return entityWithOptionalComplexProperty(value);
    }
    if(value is EntityWithOptionalProperty && entityWithOptionalProperty != null) {
     return entityWithOptionalProperty(value);
    }
    if(value is EntityWithOptionalStringEnumProperty && entityWithOptionalStringEnumProperty != null) {
     return entityWithOptionalStringEnumProperty(value);
    }
    if(value is EntityWithPropertyWithDefaultValue && entityWithPropertyWithDefaultValue != null) {
     return entityWithPropertyWithDefaultValue(value);
    }
    if(value is EntityWithRawArray && entityWithRawArray != null) {
     return entityWithRawArray(value);
    }
    if(value is EntityWithRequiredProperty && entityWithRequiredProperty != null) {
     return entityWithRequiredProperty(value);
    }
    if(value is EntityWithSimpleProperties && entityWithSimpleProperties != null) {
     return entityWithSimpleProperties(value);
    }
    if(value is EntityWithStringArrayProperty && entityWithStringArrayProperty != null) {
     return entityWithStringArrayProperty(value);
    }
    if(value is EntityWithStringEnumProperty && entityWithStringEnumProperty != null) {
     return entityWithStringEnumProperty(value);
    }
    if(value is EntityWithStringEnumPropertyWithDefaultValue && entityWithStringEnumPropertyWithDefaultValue != null) {
     return entityWithStringEnumPropertyWithDefaultValue(value);
    }
    if(value is EntityWithoutProperties && entityWithoutProperties != null) {
     return entityWithoutProperties(value);
    }
    return orElse();
  }

  const Entity.entityWithArray(
    EntityWithArray value,
  ) :
 _value = value;

  const Entity.entityWithArrayOfEnums(
    EntityWithArrayOfEnums value,
  ) :
 _value = value;

  const Entity.entityWithArrayOfExpressions(
    EntityWithArrayOfExpressions value,
  ) :
 _value = value;

  const Entity.entityWithArrayOfNestedItems(
    EntityWithArrayOfNestedItems value,
  ) :
 _value = value;

  const Entity.entityWithArrayWithTransform(
    EntityWithArrayWithTransform value,
  ) :
 _value = value;

  const Entity.entityWithComplexProperty(
    EntityWithComplexProperty value,
  ) :
 _value = value;

  const Entity.entityWithComplexPropertyWithDefaultValue(
    EntityWithComplexPropertyWithDefaultValue value,
  ) :
 _value = value;

  const Entity.entityWithEntityProperty(
    EntityWithEntityProperty value,
  ) :
 _value = value;

  const Entity.entityWithOptionalComplexProperty(
    EntityWithOptionalComplexProperty value,
  ) :
 _value = value;

  const Entity.entityWithOptionalProperty(
    EntityWithOptionalProperty value,
  ) :
 _value = value;

  const Entity.entityWithOptionalStringEnumProperty(
    EntityWithOptionalStringEnumProperty value,
  ) :
 _value = value;

  const Entity.entityWithPropertyWithDefaultValue(
    EntityWithPropertyWithDefaultValue value,
  ) :
 _value = value;

  const Entity.entityWithRawArray(
    EntityWithRawArray value,
  ) :
 _value = value;

  const Entity.entityWithRequiredProperty(
    EntityWithRequiredProperty value,
  ) :
 _value = value;

  const Entity.entityWithSimpleProperties(
    EntityWithSimpleProperties value,
  ) :
 _value = value;

  const Entity.entityWithStringArrayProperty(
    EntityWithStringArrayProperty value,
  ) :
 _value = value;

  const Entity.entityWithStringEnumProperty(
    EntityWithStringEnumProperty value,
  ) :
 _value = value;

  const Entity.entityWithStringEnumPropertyWithDefaultValue(
    EntityWithStringEnumPropertyWithDefaultValue value,
  ) :
 _value = value;

  const Entity.entityWithoutProperties(
    EntityWithoutProperties value,
  ) :
 _value = value;


  static Entity? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    switch (json['type']) {
      case EntityWithArray.type :
        return Entity(EntityWithArray.fromJson(json)!);
      case EntityWithArrayOfEnums.type :
        return Entity(EntityWithArrayOfEnums.fromJson(json)!);
      case EntityWithArrayOfExpressions.type :
        return Entity(EntityWithArrayOfExpressions.fromJson(json)!);
      case EntityWithArrayOfNestedItems.type :
        return Entity(EntityWithArrayOfNestedItems.fromJson(json)!);
      case EntityWithArrayWithTransform.type :
        return Entity(EntityWithArrayWithTransform.fromJson(json)!);
      case EntityWithComplexProperty.type :
        return Entity(EntityWithComplexProperty.fromJson(json)!);
      case EntityWithComplexPropertyWithDefaultValue.type :
        return Entity(EntityWithComplexPropertyWithDefaultValue.fromJson(json)!);
      case EntityWithEntityProperty.type :
        return Entity(EntityWithEntityProperty.fromJson(json)!);
      case EntityWithOptionalComplexProperty.type :
        return Entity(EntityWithOptionalComplexProperty.fromJson(json)!);
      case EntityWithOptionalProperty.type :
        return Entity(EntityWithOptionalProperty.fromJson(json)!);
      case EntityWithOptionalStringEnumProperty.type :
        return Entity(EntityWithOptionalStringEnumProperty.fromJson(json)!);
      case EntityWithPropertyWithDefaultValue.type :
        return Entity(EntityWithPropertyWithDefaultValue.fromJson(json)!);
      case EntityWithRawArray.type :
        return Entity(EntityWithRawArray.fromJson(json)!);
      case EntityWithRequiredProperty.type :
        return Entity(EntityWithRequiredProperty.fromJson(json)!);
      case EntityWithSimpleProperties.type :
        return Entity(EntityWithSimpleProperties.fromJson(json)!);
      case EntityWithStringArrayProperty.type :
        return Entity(EntityWithStringArrayProperty.fromJson(json)!);
      case EntityWithStringEnumProperty.type :
        return Entity(EntityWithStringEnumProperty.fromJson(json)!);
      case EntityWithStringEnumPropertyWithDefaultValue.type :
        return Entity(EntityWithStringEnumPropertyWithDefaultValue.fromJson(json)!);
      case EntityWithoutProperties.type :
        return Entity(EntityWithoutProperties.fromJson(json)!);
    }
    return null;
  }
}
