// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import '../utils/parsing_extensions.dart';

class EntityWithOptionalComplexProperty with EquatableMixin {
  const EntityWithOptionalComplexProperty({
    this.property,
  });

  static const type = "entity_with_optional_complex_property";

  final EntityWithOptionalComplexPropertyProperty? property;

  @override
  List<Object?> get props => [
        property,
      ];

  static EntityWithOptionalComplexProperty? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    return EntityWithOptionalComplexProperty(
      property: EntityWithOptionalComplexPropertyProperty.fromJson(json['property']),
    );
  }
}

class EntityWithOptionalComplexPropertyProperty with EquatableMixin {
  const EntityWithOptionalComplexPropertyProperty({
    required this.value,
  });


  final Uri value;

  @override
  List<Object?> get props => [
        value,
      ];

  static EntityWithOptionalComplexPropertyProperty? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    return EntityWithOptionalComplexPropertyProperty(
      value: safeParseUri(json['value'])!,
    );
  }
}
