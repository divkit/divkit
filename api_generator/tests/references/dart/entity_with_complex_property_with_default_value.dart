// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import '../utils/parsing_utils.dart';

class EntityWithComplexPropertyWithDefaultValue with EquatableMixin {
  const EntityWithComplexPropertyWithDefaultValue({
    this.property = const EntityWithComplexPropertyWithDefaultValueProperty(value: ValueExpression("Default text"),),
  });

  static const type = "entity_with_complex_property_with_default_value";
  // default value: const EntityWithComplexPropertyWithDefaultValueProperty(value: ValueExpression("Default text"),)
  final EntityWithComplexPropertyWithDefaultValueProperty property;

  @override
  List<Object?> get props => [
        property,
      ];

  static EntityWithComplexPropertyWithDefaultValue? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    return EntityWithComplexPropertyWithDefaultValue(
      property: safeParseObj(EntityWithComplexPropertyWithDefaultValueProperty.fromJson(json['property']), fallback: const EntityWithComplexPropertyWithDefaultValueProperty(value: ValueExpression("Default text"),),)!,
    );
  }
}

class EntityWithComplexPropertyWithDefaultValueProperty with EquatableMixin {
  const EntityWithComplexPropertyWithDefaultValueProperty({
    required this.value,
  });


  final Expression<String> value;

  @override
  List<Object?> get props => [
        value,
      ];

  static EntityWithComplexPropertyWithDefaultValueProperty? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    return EntityWithComplexPropertyWithDefaultValueProperty(
      value: safeParseStrExpr(json['value']?.toString(),)!,
    );
  }
}
