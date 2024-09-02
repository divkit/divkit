// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import 'package:divkit/src/utils/parsing_utils.dart';

class EntityWithStringEnumProperty extends Preloadable with EquatableMixin  {
  const EntityWithStringEnumProperty({
    required this.property,
  });

  static const type = "entity_with_string_enum_property";

  final Expression<EntityWithStringEnumPropertyProperty> property;

  @override
  List<Object?> get props => [
        property,
      ];

  EntityWithStringEnumProperty copyWith({
      Expression<EntityWithStringEnumPropertyProperty>?  property,
  }) => EntityWithStringEnumProperty(
      property: property ?? this.property,
    );

  static EntityWithStringEnumProperty? fromJson(Map<String, dynamic>? json,) {
    if (json == null) {
      return null;
    }
    try {
      return EntityWithStringEnumProperty(
        property: safeParseStrEnumExpr(json['property'], parse: EntityWithStringEnumPropertyProperty.fromJson,)!,
      );
    } catch (e, st) {
      return null;
    }
  }

  static Future<EntityWithStringEnumProperty?> parse(Map<String, dynamic>? json,) async {
    if (json == null) {
      return null;
    }
    try {
      return EntityWithStringEnumProperty(
        property: (await safeParseStrEnumExprAsync(json['property'], parse: EntityWithStringEnumPropertyProperty.fromJson,))!,
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

enum EntityWithStringEnumPropertyProperty implements Preloadable {
  first('first'),
  second('second');

  final String value;

  const EntityWithStringEnumPropertyProperty(this.value);
  bool get isFirst => this == first;

  bool get isSecond => this == second;


  T map<T>({
    required T Function() first,
    required T Function() second,
  }) {
    switch (this) {
      case EntityWithStringEnumPropertyProperty.first:
        return first();
      case EntityWithStringEnumPropertyProperty.second:
        return second();
     }
  }

  T maybeMap<T>({
    T Function()? first,
    T Function()? second,
    required T Function() orElse,
  }) {
    switch (this) {
      case EntityWithStringEnumPropertyProperty.first:
        return first?.call() ?? orElse();
      case EntityWithStringEnumPropertyProperty.second:
        return second?.call() ?? orElse();
     }
  }

  Future<void> preload(Map<String, dynamic> context) async {}

  static EntityWithStringEnumPropertyProperty? fromJson(String? json,) {
    if (json == null) {
      return null;
    }
    try {
      switch (json) {
        case 'first':
        return EntityWithStringEnumPropertyProperty.first;
        case 'second':
        return EntityWithStringEnumPropertyProperty.second;
      }
      return null;
    } catch (e, st) {
      return null;
    }
  }

  static Future<EntityWithStringEnumPropertyProperty?> parse(String? json,) async {
    if (json == null) {
      return null;
    }
    try {
      switch (json) {
        case 'first':
        return EntityWithStringEnumPropertyProperty.first;
        case 'second':
        return EntityWithStringEnumPropertyProperty.second;
      }
      return null;
    } catch (e, st) {
      return null;
    }
  }
}
