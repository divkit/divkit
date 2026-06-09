// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import 'package:divkit/src/utils/parsing.dart';


class EntityWithComplexPropertyWithDefaultValue with EquatableMixin  {
  const EntityWithComplexPropertyWithDefaultValue({
    this.property = const EntityWithComplexPropertyWithDefaultValueComplexProperty(value: ValueExpression("Default text",),),
  });

  static const type = "entity_with_complex_property_with_default_value";
   // default value: const EntityWithComplexPropertyWithDefaultValueComplexProperty(value: ValueExpression("Default text",),)
  final EntityWithComplexPropertyWithDefaultValueComplexProperty property;

  @override
  List<Object?> get props => [
        property,
      ];

  EntityWithComplexPropertyWithDefaultValue copyWith({
      EntityWithComplexPropertyWithDefaultValueComplexProperty?  property,
  }) => EntityWithComplexPropertyWithDefaultValue(
      property: property ?? this.property,
    );

  static EntityWithComplexPropertyWithDefaultValue? fromJson(Map<String, dynamic>? json,) {
    if (json == null) {
      return null;
    }
    try {
      return EntityWithComplexPropertyWithDefaultValue(
        property: reqProp<EntityWithComplexPropertyWithDefaultValueComplexProperty>(safeParseObject(json['property'], parse: EntityWithComplexPropertyWithDefaultValueComplexProperty.fromJson, fallback: const EntityWithComplexPropertyWithDefaultValueComplexProperty(value: ValueExpression("Default text",),),), name: 'property',),
      );
    } catch (e, st) {
      logger.warning("Parsing error", error: e, stackTrace: st);
      return null;
    }
  }
}


class EntityWithComplexPropertyWithDefaultValueComplexProperty with EquatableMixin  {
  const EntityWithComplexPropertyWithDefaultValueComplexProperty({
    required this.value,
  });

  final Expression<String> value;

  @override
  List<Object?> get props => [
        value,
      ];

  EntityWithComplexPropertyWithDefaultValueComplexProperty copyWith({
      Expression<String>?  value,
  }) => EntityWithComplexPropertyWithDefaultValueComplexProperty(
      value: value ?? this.value,
    );

  static EntityWithComplexPropertyWithDefaultValueComplexProperty? fromJson(Map<String, dynamic>? json,) {
    if (json == null) {
      return null;
    }
    try {
      return EntityWithComplexPropertyWithDefaultValueComplexProperty(
        value: reqVProp<String>(safeParseStrExpr(json['value'],), name: 'value',),
      );
    } catch (e, st) {
      logger.warning("Parsing error", error: e, stackTrace: st);
      return null;
    }
  }
}
