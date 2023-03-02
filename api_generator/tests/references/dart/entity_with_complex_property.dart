// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import '../utils/parsing_extensions.dart';

class EntityWithComplexProperty with EquatableMixin {
  const EntityWithComplexProperty({
    required this.property,
  });

  static const type = "entity_with_complex_property";

  final EntityWithComplexPropertyProperty property;

  @override
  List<Object?> get props => [
        property,
      ];

  static EntityWithComplexProperty? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    return EntityWithComplexProperty(
      property: EntityWithComplexPropertyProperty.fromJson(json['property'])!,
    );
  }
}

class EntityWithComplexPropertyProperty with EquatableMixin {
  const EntityWithComplexPropertyProperty({
    required this.value,
  });


  final Uri value;

  @override
  List<Object?> get props => [
        value,
      ];

  static EntityWithComplexPropertyProperty? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    return EntityWithComplexPropertyProperty(
      value: safeParseUri(json['value'])!,
    );
  }
}
