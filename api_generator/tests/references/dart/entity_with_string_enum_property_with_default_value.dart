// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import '../utils/parsing_utils.dart';

class EntityWithStringEnumPropertyWithDefaultValue with EquatableMixin {
  const EntityWithStringEnumPropertyWithDefaultValue({
    this.value = const Expression.value(EntityWithStringEnumPropertyWithDefaultValueValue.second),
  });

  static const type = "entity_with_string_enum_property_with_default_value";
  // default value: EntityWithStringEnumPropertyWithDefaultValueValue.second
  final Expression<EntityWithStringEnumPropertyWithDefaultValueValue> value;

  @override
  List<Object?> get props => [
        value,
      ];

  static EntityWithStringEnumPropertyWithDefaultValue? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    return EntityWithStringEnumPropertyWithDefaultValue(
      value: safeParseClassExpr(EntityWithStringEnumPropertyWithDefaultValueValue.fromJson(json['value'])) ?? const Expression.value(EntityWithStringEnumPropertyWithDefaultValueValue.second),
    );
  }
}

enum EntityWithStringEnumPropertyWithDefaultValueValue {
  first('first'),
  second('second'),
  third('third');

  final String value;

  const EntityWithStringEnumPropertyWithDefaultValueValue(this.value);

  T map<T>({
    required T Function() first,
    required T Function() second,
    required T Function() third,
  }) {
    switch (this) {
      case EntityWithStringEnumPropertyWithDefaultValueValue.first:
        return first();
      case EntityWithStringEnumPropertyWithDefaultValueValue.second:
        return second();
      case EntityWithStringEnumPropertyWithDefaultValueValue.third:
        return third();
     }
  }

  T maybeMap<T>({
    T Function()? first,
    T Function()? second,
    T Function()? third,
    required T Function() orElse,
  }) {
    switch (this) {
      case EntityWithStringEnumPropertyWithDefaultValueValue.first:
        return first?.call() ?? orElse();
      case EntityWithStringEnumPropertyWithDefaultValueValue.second:
        return second?.call() ?? orElse();
      case EntityWithStringEnumPropertyWithDefaultValueValue.third:
        return third?.call() ?? orElse();
     }
  }


  static EntityWithStringEnumPropertyWithDefaultValueValue? fromJson(String? json) {
    if (json == null) {
      return null;
    }
    switch (json) {
      case 'first':
        return EntityWithStringEnumPropertyWithDefaultValueValue.first;
      case 'second':
        return EntityWithStringEnumPropertyWithDefaultValueValue.second;
      case 'third':
        return EntityWithStringEnumPropertyWithDefaultValueValue.third;
    }
    return null;
  }
}
