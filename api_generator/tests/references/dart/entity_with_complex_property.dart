// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import 'package:divkit/src/utils/parsing_utils.dart';


class EntityWithComplexProperty extends Preloadable with EquatableMixin  {
  const EntityWithComplexProperty({
    required this.property,
  });

  static const type = "entity_with_complex_property";
  final EntityWithComplexPropertyProperty property;

  @override
  List<Object?> get props => [
        property,
      ];

  EntityWithComplexProperty copyWith({
      EntityWithComplexPropertyProperty?  property,
  }) => EntityWithComplexProperty(
      property: property ?? this.property,
    );

  static EntityWithComplexProperty? fromJson(Map<String, dynamic>? json,) {
    if (json == null) {
      return null;
    }
    try {
      return EntityWithComplexProperty(
        property: safeParseObj(EntityWithComplexPropertyProperty.fromJson(json['property']),)!,
      );
    } catch (e, st) {
      return null;
    }
  }

  static Future<EntityWithComplexProperty?> parse(Map<String, dynamic>? json,) async {
    if (json == null) {
      return null;
    }
    try {
      return EntityWithComplexProperty(
        property: (await safeParseObjAsync(EntityWithComplexPropertyProperty.fromJson(json['property']),))!,
      );
    } catch (e, st) {
      return null;
    }
  }

  Future<void> preload(Map<String, dynamic> context,) async {
    try {
    await property.preload(context);
    } catch (e, st) {
      return;
    }
  }
}


class EntityWithComplexPropertyProperty extends Preloadable with EquatableMixin  {
  const EntityWithComplexPropertyProperty({
    required this.value,
  });

  final Expression<Uri> value;

  @override
  List<Object?> get props => [
        value,
      ];

  EntityWithComplexPropertyProperty copyWith({
      Expression<Uri>?  value,
  }) => EntityWithComplexPropertyProperty(
      value: value ?? this.value,
    );

  static EntityWithComplexPropertyProperty? fromJson(Map<String, dynamic>? json,) {
    if (json == null) {
      return null;
    }
    try {
      return EntityWithComplexPropertyProperty(
        value: safeParseUriExpr(json['value'])!,
      );
    } catch (e, st) {
      return null;
    }
  }

  static Future<EntityWithComplexPropertyProperty?> parse(Map<String, dynamic>? json,) async {
    if (json == null) {
      return null;
    }
    try {
      return EntityWithComplexPropertyProperty(
        value: (await safeParseUriExprAsync(json['value']))!,
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
