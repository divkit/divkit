// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import '../utils/parsing_utils.dart';

class EntityWithOptionalStringEnumProperty with EquatableMixin {
  const EntityWithOptionalStringEnumProperty({
    this.property,
  });

  static const type = "entity_with_optional_string_enum_property";

  final Expression<EntityWithOptionalStringEnumPropertyProperty>? property;

  @override
  List<Object?> get props => [
        property,
      ];

  EntityWithOptionalStringEnumProperty copyWith({
      Expression<EntityWithOptionalStringEnumPropertyProperty>? Function()?  property,
  }) => EntityWithOptionalStringEnumProperty(
      property: property != null ? property.call() : this.property,
    );

  static EntityWithOptionalStringEnumProperty? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    return EntityWithOptionalStringEnumProperty(
      property: safeParseStrEnumExpr(json['property'], parse: EntityWithOptionalStringEnumPropertyProperty.fromJson,),
    );
  }
}

enum EntityWithOptionalStringEnumPropertyProperty {
  first('first'),
  second('second');

  final String value;

  const EntityWithOptionalStringEnumPropertyProperty(this.value);

  T map<T>({
    required T Function() first,
    required T Function() second,
  }) {
    switch (this) {
      case EntityWithOptionalStringEnumPropertyProperty.first:
        return first();
      case EntityWithOptionalStringEnumPropertyProperty.second:
        return second();
     }
  }

  T maybeMap<T>({
    T Function()? first,
    T Function()? second,
    required T Function() orElse,
  }) {
    switch (this) {
      case EntityWithOptionalStringEnumPropertyProperty.first:
        return first?.call() ?? orElse();
      case EntityWithOptionalStringEnumPropertyProperty.second:
        return second?.call() ?? orElse();
     }
  }


  static EntityWithOptionalStringEnumPropertyProperty? fromJson(String? json) {
    if (json == null) {
      return null;
    }
    switch (json) {
      case 'first':
        return EntityWithOptionalStringEnumPropertyProperty.first;
      case 'second':
        return EntityWithOptionalStringEnumPropertyProperty.second;
    }
    return null;
  }
}
