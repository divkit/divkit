// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import '../utils/parsing_extensions.dart';

class EntityWithStringEnumPropertyWithDefaultValue with EquatableMixin {
  const EntityWithStringEnumPropertyWithDefaultValue({
    this.value = EntityWithStringEnumPropertyWithDefaultValueValue.second,
  });

  static const type = "entity_with_string_enum_property_with_default_value";
  // default value: EntityWithStringEnumPropertyWithDefaultValueValue.second
  final EntityWithStringEnumPropertyWithDefaultValueValue value;

  @override
  List<Object?> get props => [
        value,
      ];

  static EntityWithStringEnumPropertyWithDefaultValue? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    return EntityWithStringEnumPropertyWithDefaultValue(
      value: EntityWithStringEnumPropertyWithDefaultValueValue.fromJson(json['value']) ?? EntityWithStringEnumPropertyWithDefaultValueValue.second,
    );
  }
}

enum EntityWithStringEnumPropertyWithDefaultValueValue {
  first('first'),
  second('second'),
  third('third');

  final String value;

  const EntityWithStringEnumPropertyWithDefaultValueValue(this.value);

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
