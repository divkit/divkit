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
import 'entity_with_required_property.dart';
import 'entity_with_simple_properties.dart';
import 'entity_with_strict_array.dart';
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
    if(value is EntityWithRequiredProperty) {
      return value;
    }
    if(value is EntityWithSimpleProperties) {
      return value;
    }
    if(value is EntityWithStrictArray) {
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

  void map({
    required Function(EntityWithArray) entityWithArray,
    required Function(EntityWithArrayOfEnums) entityWithArrayOfEnums,
    required Function(EntityWithArrayOfExpressions) entityWithArrayOfExpressions,
    required Function(EntityWithArrayOfNestedItems) entityWithArrayOfNestedItems,
    required Function(EntityWithArrayWithTransform) entityWithArrayWithTransform,
    required Function(EntityWithComplexProperty) entityWithComplexProperty,
    required Function(EntityWithComplexPropertyWithDefaultValue) entityWithComplexPropertyWithDefaultValue,
    required Function(EntityWithEntityProperty) entityWithEntityProperty,
    required Function(EntityWithOptionalComplexProperty) entityWithOptionalComplexProperty,
    required Function(EntityWithOptionalProperty) entityWithOptionalProperty,
    required Function(EntityWithOptionalStringEnumProperty) entityWithOptionalStringEnumProperty,
    required Function(EntityWithPropertyWithDefaultValue) entityWithPropertyWithDefaultValue,
    required Function(EntityWithRequiredProperty) entityWithRequiredProperty,
    required Function(EntityWithSimpleProperties) entityWithSimpleProperties,
    required Function(EntityWithStrictArray) entityWithStrictArray,
    required Function(EntityWithStringArrayProperty) entityWithStringArrayProperty,
    required Function(EntityWithStringEnumProperty) entityWithStringEnumProperty,
    required Function(EntityWithStringEnumPropertyWithDefaultValue) entityWithStringEnumPropertyWithDefaultValue,
    required Function(EntityWithoutProperties) entityWithoutProperties,
  }) {
    final value = _value;
    if(value is EntityWithArray) {
      entityWithArray(value);
      return;
    }
    if(value is EntityWithArrayOfEnums) {
      entityWithArrayOfEnums(value);
      return;
    }
    if(value is EntityWithArrayOfExpressions) {
      entityWithArrayOfExpressions(value);
      return;
    }
    if(value is EntityWithArrayOfNestedItems) {
      entityWithArrayOfNestedItems(value);
      return;
    }
    if(value is EntityWithArrayWithTransform) {
      entityWithArrayWithTransform(value);
      return;
    }
    if(value is EntityWithComplexProperty) {
      entityWithComplexProperty(value);
      return;
    }
    if(value is EntityWithComplexPropertyWithDefaultValue) {
      entityWithComplexPropertyWithDefaultValue(value);
      return;
    }
    if(value is EntityWithEntityProperty) {
      entityWithEntityProperty(value);
      return;
    }
    if(value is EntityWithOptionalComplexProperty) {
      entityWithOptionalComplexProperty(value);
      return;
    }
    if(value is EntityWithOptionalProperty) {
      entityWithOptionalProperty(value);
      return;
    }
    if(value is EntityWithOptionalStringEnumProperty) {
      entityWithOptionalStringEnumProperty(value);
      return;
    }
    if(value is EntityWithPropertyWithDefaultValue) {
      entityWithPropertyWithDefaultValue(value);
      return;
    }
    if(value is EntityWithRequiredProperty) {
      entityWithRequiredProperty(value);
      return;
    }
    if(value is EntityWithSimpleProperties) {
      entityWithSimpleProperties(value);
      return;
    }
    if(value is EntityWithStrictArray) {
      entityWithStrictArray(value);
      return;
    }
    if(value is EntityWithStringArrayProperty) {
      entityWithStringArrayProperty(value);
      return;
    }
    if(value is EntityWithStringEnumProperty) {
      entityWithStringEnumProperty(value);
      return;
    }
    if(value is EntityWithStringEnumPropertyWithDefaultValue) {
      entityWithStringEnumPropertyWithDefaultValue(value);
      return;
    }
    if(value is EntityWithoutProperties) {
      entityWithoutProperties(value);
      return;
    }
    throw Exception("Type ${value.runtimeType.toString()} is not generalized in Entity");
  }

  void maybeMap({
    Function(EntityWithArray)? entityWithArray,
    Function(EntityWithArrayOfEnums)? entityWithArrayOfEnums,
    Function(EntityWithArrayOfExpressions)? entityWithArrayOfExpressions,
    Function(EntityWithArrayOfNestedItems)? entityWithArrayOfNestedItems,
    Function(EntityWithArrayWithTransform)? entityWithArrayWithTransform,
    Function(EntityWithComplexProperty)? entityWithComplexProperty,
    Function(EntityWithComplexPropertyWithDefaultValue)? entityWithComplexPropertyWithDefaultValue,
    Function(EntityWithEntityProperty)? entityWithEntityProperty,
    Function(EntityWithOptionalComplexProperty)? entityWithOptionalComplexProperty,
    Function(EntityWithOptionalProperty)? entityWithOptionalProperty,
    Function(EntityWithOptionalStringEnumProperty)? entityWithOptionalStringEnumProperty,
    Function(EntityWithPropertyWithDefaultValue)? entityWithPropertyWithDefaultValue,
    Function(EntityWithRequiredProperty)? entityWithRequiredProperty,
    Function(EntityWithSimpleProperties)? entityWithSimpleProperties,
    Function(EntityWithStrictArray)? entityWithStrictArray,
    Function(EntityWithStringArrayProperty)? entityWithStringArrayProperty,
    Function(EntityWithStringEnumProperty)? entityWithStringEnumProperty,
    Function(EntityWithStringEnumPropertyWithDefaultValue)? entityWithStringEnumPropertyWithDefaultValue,
    Function(EntityWithoutProperties)? entityWithoutProperties,
    required Function() orElse,
  }) {
    final value = _value;
    if(value is EntityWithArray && entityWithArray != null) {
      entityWithArray(value);
      return;
    }
    if(value is EntityWithArrayOfEnums && entityWithArrayOfEnums != null) {
      entityWithArrayOfEnums(value);
      return;
    }
    if(value is EntityWithArrayOfExpressions && entityWithArrayOfExpressions != null) {
      entityWithArrayOfExpressions(value);
      return;
    }
    if(value is EntityWithArrayOfNestedItems && entityWithArrayOfNestedItems != null) {
      entityWithArrayOfNestedItems(value);
      return;
    }
    if(value is EntityWithArrayWithTransform && entityWithArrayWithTransform != null) {
      entityWithArrayWithTransform(value);
      return;
    }
    if(value is EntityWithComplexProperty && entityWithComplexProperty != null) {
      entityWithComplexProperty(value);
      return;
    }
    if(value is EntityWithComplexPropertyWithDefaultValue && entityWithComplexPropertyWithDefaultValue != null) {
      entityWithComplexPropertyWithDefaultValue(value);
      return;
    }
    if(value is EntityWithEntityProperty && entityWithEntityProperty != null) {
      entityWithEntityProperty(value);
      return;
    }
    if(value is EntityWithOptionalComplexProperty && entityWithOptionalComplexProperty != null) {
      entityWithOptionalComplexProperty(value);
      return;
    }
    if(value is EntityWithOptionalProperty && entityWithOptionalProperty != null) {
      entityWithOptionalProperty(value);
      return;
    }
    if(value is EntityWithOptionalStringEnumProperty && entityWithOptionalStringEnumProperty != null) {
      entityWithOptionalStringEnumProperty(value);
      return;
    }
    if(value is EntityWithPropertyWithDefaultValue && entityWithPropertyWithDefaultValue != null) {
      entityWithPropertyWithDefaultValue(value);
      return;
    }
    if(value is EntityWithRequiredProperty && entityWithRequiredProperty != null) {
      entityWithRequiredProperty(value);
      return;
    }
    if(value is EntityWithSimpleProperties && entityWithSimpleProperties != null) {
      entityWithSimpleProperties(value);
      return;
    }
    if(value is EntityWithStrictArray && entityWithStrictArray != null) {
      entityWithStrictArray(value);
      return;
    }
    if(value is EntityWithStringArrayProperty && entityWithStringArrayProperty != null) {
      entityWithStringArrayProperty(value);
      return;
    }
    if(value is EntityWithStringEnumProperty && entityWithStringEnumProperty != null) {
      entityWithStringEnumProperty(value);
      return;
    }
    if(value is EntityWithStringEnumPropertyWithDefaultValue && entityWithStringEnumPropertyWithDefaultValue != null) {
      entityWithStringEnumPropertyWithDefaultValue(value);
      return;
    }
    if(value is EntityWithoutProperties && entityWithoutProperties != null) {
      entityWithoutProperties(value);
      return;
    }
    orElse();
  }

  const Entity.entityWithArray(
    EntityWithArray value,
  ) : _value = value;

  const Entity.entityWithArrayOfEnums(
    EntityWithArrayOfEnums value,
  ) : _value = value;

  const Entity.entityWithArrayOfExpressions(
    EntityWithArrayOfExpressions value,
  ) : _value = value;

  const Entity.entityWithArrayOfNestedItems(
    EntityWithArrayOfNestedItems value,
  ) : _value = value;

  const Entity.entityWithArrayWithTransform(
    EntityWithArrayWithTransform value,
  ) : _value = value;

  const Entity.entityWithComplexProperty(
    EntityWithComplexProperty value,
  ) : _value = value;

  const Entity.entityWithComplexPropertyWithDefaultValue(
    EntityWithComplexPropertyWithDefaultValue value,
  ) : _value = value;

  const Entity.entityWithEntityProperty(
    EntityWithEntityProperty value,
  ) : _value = value;

  const Entity.entityWithOptionalComplexProperty(
    EntityWithOptionalComplexProperty value,
  ) : _value = value;

  const Entity.entityWithOptionalProperty(
    EntityWithOptionalProperty value,
  ) : _value = value;

  const Entity.entityWithOptionalStringEnumProperty(
    EntityWithOptionalStringEnumProperty value,
  ) : _value = value;

  const Entity.entityWithPropertyWithDefaultValue(
    EntityWithPropertyWithDefaultValue value,
  ) : _value = value;

  const Entity.entityWithRequiredProperty(
    EntityWithRequiredProperty value,
  ) : _value = value;

  const Entity.entityWithSimpleProperties(
    EntityWithSimpleProperties value,
  ) : _value = value;

  const Entity.entityWithStrictArray(
    EntityWithStrictArray value,
  ) : _value = value;

  const Entity.entityWithStringArrayProperty(
    EntityWithStringArrayProperty value,
  ) : _value = value;

  const Entity.entityWithStringEnumProperty(
    EntityWithStringEnumProperty value,
  ) : _value = value;

  const Entity.entityWithStringEnumPropertyWithDefaultValue(
    EntityWithStringEnumPropertyWithDefaultValue value,
  ) : _value = value;

  const Entity.entityWithoutProperties(
    EntityWithoutProperties value,
  ) : _value = value;


  static Entity? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    switch (json['type']) {
      case EntityWithArray.type :
        return Entity.entityWithArray(EntityWithArray.fromJson(json)!);
      case EntityWithArrayOfEnums.type :
        return Entity.entityWithArrayOfEnums(EntityWithArrayOfEnums.fromJson(json)!);
      case EntityWithArrayOfExpressions.type :
        return Entity.entityWithArrayOfExpressions(EntityWithArrayOfExpressions.fromJson(json)!);
      case EntityWithArrayOfNestedItems.type :
        return Entity.entityWithArrayOfNestedItems(EntityWithArrayOfNestedItems.fromJson(json)!);
      case EntityWithArrayWithTransform.type :
        return Entity.entityWithArrayWithTransform(EntityWithArrayWithTransform.fromJson(json)!);
      case EntityWithComplexProperty.type :
        return Entity.entityWithComplexProperty(EntityWithComplexProperty.fromJson(json)!);
      case EntityWithComplexPropertyWithDefaultValue.type :
        return Entity.entityWithComplexPropertyWithDefaultValue(EntityWithComplexPropertyWithDefaultValue.fromJson(json)!);
      case EntityWithEntityProperty.type :
        return Entity.entityWithEntityProperty(EntityWithEntityProperty.fromJson(json)!);
      case EntityWithOptionalComplexProperty.type :
        return Entity.entityWithOptionalComplexProperty(EntityWithOptionalComplexProperty.fromJson(json)!);
      case EntityWithOptionalProperty.type :
        return Entity.entityWithOptionalProperty(EntityWithOptionalProperty.fromJson(json)!);
      case EntityWithOptionalStringEnumProperty.type :
        return Entity.entityWithOptionalStringEnumProperty(EntityWithOptionalStringEnumProperty.fromJson(json)!);
      case EntityWithPropertyWithDefaultValue.type :
        return Entity.entityWithPropertyWithDefaultValue(EntityWithPropertyWithDefaultValue.fromJson(json)!);
      case EntityWithRequiredProperty.type :
        return Entity.entityWithRequiredProperty(EntityWithRequiredProperty.fromJson(json)!);
      case EntityWithSimpleProperties.type :
        return Entity.entityWithSimpleProperties(EntityWithSimpleProperties.fromJson(json)!);
      case EntityWithStrictArray.type :
        return Entity.entityWithStrictArray(EntityWithStrictArray.fromJson(json)!);
      case EntityWithStringArrayProperty.type :
        return Entity.entityWithStringArrayProperty(EntityWithStringArrayProperty.fromJson(json)!);
      case EntityWithStringEnumProperty.type :
        return Entity.entityWithStringEnumProperty(EntityWithStringEnumProperty.fromJson(json)!);
      case EntityWithStringEnumPropertyWithDefaultValue.type :
        return Entity.entityWithStringEnumPropertyWithDefaultValue(EntityWithStringEnumPropertyWithDefaultValue.fromJson(json)!);
      case EntityWithoutProperties.type :
        return Entity.entityWithoutProperties(EntityWithoutProperties.fromJson(json)!);
    }
    return null;
  }
}
