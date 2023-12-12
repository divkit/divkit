// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import '../utils/parsing_utils.dart';

class EntityWithStringEnumProperty with EquatableMixin {
  const EntityWithStringEnumProperty({
    required this.property,
  });

  static const type = "entity_with_string_enum_property";

  final Expression<EntityWithStringEnumPropertyProperty> property;

  @override
  List<Object?> get props => [
        property,
      ];

  static EntityWithStringEnumProperty? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    return EntityWithStringEnumProperty(
      property: safeParseClassExpr(EntityWithStringEnumPropertyProperty.fromJson(json['property']))!,
    );
  }
}

enum EntityWithStringEnumPropertyProperty {
  first('first'),
  second('second');

  final String value;

  const EntityWithStringEnumPropertyProperty(this.value);

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


  static EntityWithStringEnumPropertyProperty? fromJson(String? json) {
    if (json == null) {
      return null;
    }
    switch (json) {
      case 'first':
        return EntityWithStringEnumPropertyProperty.first;
      case 'second':
        return EntityWithStringEnumPropertyProperty.second;
    }
    return null;
  }
}
