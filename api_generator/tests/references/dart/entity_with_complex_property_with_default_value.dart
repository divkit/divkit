// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import 'package:divkit/src/utils/parsing.dart';


class EntityWithComplexPropertyWithDefaultValue with EquatableMixin  {
  const EntityWithComplexPropertyWithDefaultValue({
    this.property = const EntityWithComplexPropertyWithDefaultValueProperty(value: ValueExpression("Default text",),),
  });

  static const type = "entity_with_complex_property_with_default_value";
   // default value: const EntityWithComplexPropertyWithDefaultValueProperty(value: ValueExpression("Default text",),)
  final EntityWithComplexPropertyWithDefaultValueProperty property;

  @override
  List<Object?> get props => [
        property,
      ];

  EntityWithComplexPropertyWithDefaultValue copyWith({
      EntityWithComplexPropertyWithDefaultValueProperty?  property,
  }) => EntityWithComplexPropertyWithDefaultValue(
      property: property ?? this.property,
    );

  static EntityWithComplexPropertyWithDefaultValue? fromJson(Map<String, dynamic>? json,) {
    if (json == null) {
      return null;
    }
    try {
      return EntityWithComplexPropertyWithDefaultValue(
        property: reqProp<EntityWithComplexPropertyWithDefaultValueProperty>(safeParseObject(json['property'], parse: EntityWithComplexPropertyWithDefaultValueProperty.fromJson, fallback: const EntityWithComplexPropertyWithDefaultValueProperty(value: ValueExpression("Default text",),),), name: 'property',),
      );
    } catch (e, st) {
      logger.warning("Parsing error", error: e, stackTrace: st);
      return null;
    }
  }
}


class EntityWithComplexPropertyWithDefaultValueProperty with EquatableMixin  {
  const EntityWithComplexPropertyWithDefaultValueProperty({
    required this.value,
  });

  final Expression<String> value;

  @override
  List<Object?> get props => [
        value,
      ];

  EntityWithComplexPropertyWithDefaultValueProperty copyWith({
      Expression<String>?  value,
  }) => EntityWithComplexPropertyWithDefaultValueProperty(
      value: value ?? this.value,
    );

  static EntityWithComplexPropertyWithDefaultValueProperty? fromJson(Map<String, dynamic>? json,) {
    if (json == null) {
      return null;
    }
    try {
      return EntityWithComplexPropertyWithDefaultValueProperty(
        value: reqVProp<String>(safeParseStrExpr(json['value'],), name: 'value',),
      );
    } catch (e, st) {
      logger.warning("Parsing error", error: e, stackTrace: st);
      return null;
    }
  }
}
