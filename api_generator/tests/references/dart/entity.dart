// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import 'package:divkit/src/utils/parsing.dart';

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
  final Object value;
  final int _index;

  @override
  List<Object?> get props => [value];

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
    switch(_index!) {
      case 0: return entityWithArray(value as EntityWithArray,);
      case 1: return entityWithArrayOfEnums(value as EntityWithArrayOfEnums,);
      case 2: return entityWithArrayOfExpressions(value as EntityWithArrayOfExpressions,);
      case 3: return entityWithArrayOfNestedItems(value as EntityWithArrayOfNestedItems,);
      case 4: return entityWithArrayWithTransform(value as EntityWithArrayWithTransform,);
      case 5: return entityWithComplexProperty(value as EntityWithComplexProperty,);
      case 6: return entityWithComplexPropertyWithDefaultValue(value as EntityWithComplexPropertyWithDefaultValue,);
      case 7: return entityWithEntityProperty(value as EntityWithEntityProperty,);
      case 8: return entityWithOptionalComplexProperty(value as EntityWithOptionalComplexProperty,);
      case 9: return entityWithOptionalProperty(value as EntityWithOptionalProperty,);
      case 10: return entityWithOptionalStringEnumProperty(value as EntityWithOptionalStringEnumProperty,);
      case 11: return entityWithPropertyWithDefaultValue(value as EntityWithPropertyWithDefaultValue,);
      case 12: return entityWithRawArray(value as EntityWithRawArray,);
      case 13: return entityWithRequiredProperty(value as EntityWithRequiredProperty,);
      case 14: return entityWithSimpleProperties(value as EntityWithSimpleProperties,);
      case 15: return entityWithStringArrayProperty(value as EntityWithStringArrayProperty,);
      case 16: return entityWithStringEnumProperty(value as EntityWithStringEnumProperty,);
      case 17: return entityWithStringEnumPropertyWithDefaultValue(value as EntityWithStringEnumPropertyWithDefaultValue,);
      case 18: return entityWithoutProperties(value as EntityWithoutProperties,);
    }
    throw Exception("Type ${value.runtimeType.toString()} is not generalized in Entity",);
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
    switch(_index!) {
    case 0:
      if (entityWithArray != null) {
        return entityWithArray(value as EntityWithArray,);
      }
      break;
    case 1:
      if (entityWithArrayOfEnums != null) {
        return entityWithArrayOfEnums(value as EntityWithArrayOfEnums,);
      }
      break;
    case 2:
      if (entityWithArrayOfExpressions != null) {
        return entityWithArrayOfExpressions(value as EntityWithArrayOfExpressions,);
      }
      break;
    case 3:
      if (entityWithArrayOfNestedItems != null) {
        return entityWithArrayOfNestedItems(value as EntityWithArrayOfNestedItems,);
      }
      break;
    case 4:
      if (entityWithArrayWithTransform != null) {
        return entityWithArrayWithTransform(value as EntityWithArrayWithTransform,);
      }
      break;
    case 5:
      if (entityWithComplexProperty != null) {
        return entityWithComplexProperty(value as EntityWithComplexProperty,);
      }
      break;
    case 6:
      if (entityWithComplexPropertyWithDefaultValue != null) {
        return entityWithComplexPropertyWithDefaultValue(value as EntityWithComplexPropertyWithDefaultValue,);
      }
      break;
    case 7:
      if (entityWithEntityProperty != null) {
        return entityWithEntityProperty(value as EntityWithEntityProperty,);
      }
      break;
    case 8:
      if (entityWithOptionalComplexProperty != null) {
        return entityWithOptionalComplexProperty(value as EntityWithOptionalComplexProperty,);
      }
      break;
    case 9:
      if (entityWithOptionalProperty != null) {
        return entityWithOptionalProperty(value as EntityWithOptionalProperty,);
      }
      break;
    case 10:
      if (entityWithOptionalStringEnumProperty != null) {
        return entityWithOptionalStringEnumProperty(value as EntityWithOptionalStringEnumProperty,);
      }
      break;
    case 11:
      if (entityWithPropertyWithDefaultValue != null) {
        return entityWithPropertyWithDefaultValue(value as EntityWithPropertyWithDefaultValue,);
      }
      break;
    case 12:
      if (entityWithRawArray != null) {
        return entityWithRawArray(value as EntityWithRawArray,);
      }
      break;
    case 13:
      if (entityWithRequiredProperty != null) {
        return entityWithRequiredProperty(value as EntityWithRequiredProperty,);
      }
      break;
    case 14:
      if (entityWithSimpleProperties != null) {
        return entityWithSimpleProperties(value as EntityWithSimpleProperties,);
      }
      break;
    case 15:
      if (entityWithStringArrayProperty != null) {
        return entityWithStringArrayProperty(value as EntityWithStringArrayProperty,);
      }
      break;
    case 16:
      if (entityWithStringEnumProperty != null) {
        return entityWithStringEnumProperty(value as EntityWithStringEnumProperty,);
      }
      break;
    case 17:
      if (entityWithStringEnumPropertyWithDefaultValue != null) {
        return entityWithStringEnumPropertyWithDefaultValue(value as EntityWithStringEnumPropertyWithDefaultValue,);
      }
      break;
    case 18:
      if (entityWithoutProperties != null) {
        return entityWithoutProperties(value as EntityWithoutProperties,);
      }
      break;
    }
    return orElse();
  }

  const Entity.entityWithArray(
    EntityWithArray obj,
  ) : value = obj,
      _index = 0;

  const Entity.entityWithArrayOfEnums(
    EntityWithArrayOfEnums obj,
  ) : value = obj,
      _index = 1;

  const Entity.entityWithArrayOfExpressions(
    EntityWithArrayOfExpressions obj,
  ) : value = obj,
      _index = 2;

  const Entity.entityWithArrayOfNestedItems(
    EntityWithArrayOfNestedItems obj,
  ) : value = obj,
      _index = 3;

  const Entity.entityWithArrayWithTransform(
    EntityWithArrayWithTransform obj,
  ) : value = obj,
      _index = 4;

  const Entity.entityWithComplexProperty(
    EntityWithComplexProperty obj,
  ) : value = obj,
      _index = 5;

  const Entity.entityWithComplexPropertyWithDefaultValue(
    EntityWithComplexPropertyWithDefaultValue obj,
  ) : value = obj,
      _index = 6;

  const Entity.entityWithEntityProperty(
    EntityWithEntityProperty obj,
  ) : value = obj,
      _index = 7;

  const Entity.entityWithOptionalComplexProperty(
    EntityWithOptionalComplexProperty obj,
  ) : value = obj,
      _index = 8;

  const Entity.entityWithOptionalProperty(
    EntityWithOptionalProperty obj,
  ) : value = obj,
      _index = 9;

  const Entity.entityWithOptionalStringEnumProperty(
    EntityWithOptionalStringEnumProperty obj,
  ) : value = obj,
      _index = 10;

  const Entity.entityWithPropertyWithDefaultValue(
    EntityWithPropertyWithDefaultValue obj,
  ) : value = obj,
      _index = 11;

  const Entity.entityWithRawArray(
    EntityWithRawArray obj,
  ) : value = obj,
      _index = 12;

  const Entity.entityWithRequiredProperty(
    EntityWithRequiredProperty obj,
  ) : value = obj,
      _index = 13;

  const Entity.entityWithSimpleProperties(
    EntityWithSimpleProperties obj,
  ) : value = obj,
      _index = 14;

  const Entity.entityWithStringArrayProperty(
    EntityWithStringArrayProperty obj,
  ) : value = obj,
      _index = 15;

  const Entity.entityWithStringEnumProperty(
    EntityWithStringEnumProperty obj,
  ) : value = obj,
      _index = 16;

  const Entity.entityWithStringEnumPropertyWithDefaultValue(
    EntityWithStringEnumPropertyWithDefaultValue obj,
  ) : value = obj,
      _index = 17;

  const Entity.entityWithoutProperties(
    EntityWithoutProperties obj,
  ) : value = obj,
      _index = 18;

  bool get isEntityWithArray => _index == 0;

  bool get isEntityWithArrayOfEnums => _index == 1;

  bool get isEntityWithArrayOfExpressions => _index == 2;

  bool get isEntityWithArrayOfNestedItems => _index == 3;

  bool get isEntityWithArrayWithTransform => _index == 4;

  bool get isEntityWithComplexProperty => _index == 5;

  bool get isEntityWithComplexPropertyWithDefaultValue => _index == 6;

  bool get isEntityWithEntityProperty => _index == 7;

  bool get isEntityWithOptionalComplexProperty => _index == 8;

  bool get isEntityWithOptionalProperty => _index == 9;

  bool get isEntityWithOptionalStringEnumProperty => _index == 10;

  bool get isEntityWithPropertyWithDefaultValue => _index == 11;

  bool get isEntityWithRawArray => _index == 12;

  bool get isEntityWithRequiredProperty => _index == 13;

  bool get isEntityWithSimpleProperties => _index == 14;

  bool get isEntityWithStringArrayProperty => _index == 15;

  bool get isEntityWithStringEnumProperty => _index == 16;

  bool get isEntityWithStringEnumPropertyWithDefaultValue => _index == 17;

  bool get isEntityWithoutProperties => _index == 18;


  static Entity? fromJson(Map<String, dynamic>? json,) {
    if (json == null) {
      return null;
    }
    try {
      switch (json['type']) {
      case EntityWithArray.type :
        return Entity.entityWithArray(EntityWithArray.fromJson(json)!,);
      case EntityWithArrayOfEnums.type :
        return Entity.entityWithArrayOfEnums(EntityWithArrayOfEnums.fromJson(json)!,);
      case EntityWithArrayOfExpressions.type :
        return Entity.entityWithArrayOfExpressions(EntityWithArrayOfExpressions.fromJson(json)!,);
      case EntityWithArrayOfNestedItems.type :
        return Entity.entityWithArrayOfNestedItems(EntityWithArrayOfNestedItems.fromJson(json)!,);
      case EntityWithArrayWithTransform.type :
        return Entity.entityWithArrayWithTransform(EntityWithArrayWithTransform.fromJson(json)!,);
      case EntityWithComplexProperty.type :
        return Entity.entityWithComplexProperty(EntityWithComplexProperty.fromJson(json)!,);
      case EntityWithComplexPropertyWithDefaultValue.type :
        return Entity.entityWithComplexPropertyWithDefaultValue(EntityWithComplexPropertyWithDefaultValue.fromJson(json)!,);
      case EntityWithEntityProperty.type :
        return Entity.entityWithEntityProperty(EntityWithEntityProperty.fromJson(json)!,);
      case EntityWithOptionalComplexProperty.type :
        return Entity.entityWithOptionalComplexProperty(EntityWithOptionalComplexProperty.fromJson(json)!,);
      case EntityWithOptionalProperty.type :
        return Entity.entityWithOptionalProperty(EntityWithOptionalProperty.fromJson(json)!,);
      case EntityWithOptionalStringEnumProperty.type :
        return Entity.entityWithOptionalStringEnumProperty(EntityWithOptionalStringEnumProperty.fromJson(json)!,);
      case EntityWithPropertyWithDefaultValue.type :
        return Entity.entityWithPropertyWithDefaultValue(EntityWithPropertyWithDefaultValue.fromJson(json)!,);
      case EntityWithRawArray.type :
        return Entity.entityWithRawArray(EntityWithRawArray.fromJson(json)!,);
      case EntityWithRequiredProperty.type :
        return Entity.entityWithRequiredProperty(EntityWithRequiredProperty.fromJson(json)!,);
      case EntityWithSimpleProperties.type :
        return Entity.entityWithSimpleProperties(EntityWithSimpleProperties.fromJson(json)!,);
      case EntityWithStringArrayProperty.type :
        return Entity.entityWithStringArrayProperty(EntityWithStringArrayProperty.fromJson(json)!,);
      case EntityWithStringEnumProperty.type :
        return Entity.entityWithStringEnumProperty(EntityWithStringEnumProperty.fromJson(json)!,);
      case EntityWithStringEnumPropertyWithDefaultValue.type :
        return Entity.entityWithStringEnumPropertyWithDefaultValue(EntityWithStringEnumPropertyWithDefaultValue.fromJson(json)!,);
      case EntityWithoutProperties.type :
        return Entity.entityWithoutProperties(EntityWithoutProperties.fromJson(json)!,);
      }
      return null;
    } catch (_) {
      return null;
    }
  }
}
