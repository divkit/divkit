// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import 'package:divkit/src/utils/parsing_utils.dart';

class EntityWithComplexPropertyWithDefaultValue extends Preloadable with EquatableMixin  {
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
        property: safeParseObj(EntityWithComplexPropertyWithDefaultValueProperty.fromJson(json['property']), fallback: const EntityWithComplexPropertyWithDefaultValueProperty(value: ValueExpression("Default text",),),)!,
      );
    } catch (e, st) {
      return null;
    }
  }

  static Future<EntityWithComplexPropertyWithDefaultValue?> parse(Map<String, dynamic>? json,) async {
    if (json == null) {
      return null;
    }
    try {
      return EntityWithComplexPropertyWithDefaultValue(
        property: (await safeParseObjAsync(EntityWithComplexPropertyWithDefaultValueProperty.fromJson(json['property']), fallback: const EntityWithComplexPropertyWithDefaultValueProperty(value: ValueExpression("Default text",),),))!,
      );
    } catch (e, st) {
      return null;
    }
  }

  Future<void> preload(Map<String, dynamic> context,) async {
    try {
    await property?.preload(context);
    } catch (e, st) {
      return;
    }
  }
}

class EntityWithComplexPropertyWithDefaultValueProperty extends Preloadable with EquatableMixin  {
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
        value: safeParseStrExpr(json['value']?.toString(),)!,
      );
    } catch (e, st) {
      return null;
    }
  }

  static Future<EntityWithComplexPropertyWithDefaultValueProperty?> parse(Map<String, dynamic>? json,) async {
    if (json == null) {
      return null;
    }
    try {
      return EntityWithComplexPropertyWithDefaultValueProperty(
        value: (await safeParseStrExprAsync(json['value']?.toString(),))!,
      );
    } catch (e, st) {
      return null;
    }
  }

  Future<void> preload(Map<String, dynamic> context,) async {
    try {
    await value.preload(context);
    } catch (e, st) {
      return;
    }
  }
}
