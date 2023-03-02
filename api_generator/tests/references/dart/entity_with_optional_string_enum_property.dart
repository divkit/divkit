// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import '../utils/parsing_extensions.dart';

class EntityWithOptionalStringEnumProperty with EquatableMixin {
  const EntityWithOptionalStringEnumProperty({
    this.property,
  });

  static const type = "entity_with_optional_string_enum_property";

  final EntityWithOptionalStringEnumPropertyProperty? property;

  @override
  List<Object?> get props => [
        property,
      ];

  static EntityWithOptionalStringEnumProperty? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    return EntityWithOptionalStringEnumProperty(
      property: EntityWithOptionalStringEnumPropertyProperty.fromJson(json['property']),
    );
  }
}

enum EntityWithOptionalStringEnumPropertyProperty {
  first('first'),
  second('second');

  final String value;

  const EntityWithOptionalStringEnumPropertyProperty(this.value);

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
