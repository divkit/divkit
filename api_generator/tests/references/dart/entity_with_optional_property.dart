// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import '../utils/parsing_extensions.dart';

class EntityWithOptionalProperty with EquatableMixin {
  const EntityWithOptionalProperty({
    this.property,
  });

  static const type = "entity_with_optional_property";
  // at least 1 char
  final String? property;

  @override
  List<Object?> get props => [
        property,
      ];

  static EntityWithOptionalProperty? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    return EntityWithOptionalProperty(
      property: json['property']?.toString(),
    );
  }
}
