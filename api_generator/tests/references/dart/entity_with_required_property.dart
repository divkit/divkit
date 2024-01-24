// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import '../utils/parsing_utils.dart';

class EntityWithRequiredProperty with EquatableMixin {
  const EntityWithRequiredProperty({
    required this.property,
  });

  static const type = "entity_with_required_property";
  // at least 1 char
  final Expression<String> property;

  @override
  List<Object?> get props => [
        property,
      ];

  static EntityWithRequiredProperty? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    return EntityWithRequiredProperty(
      property: safeParseStrExpr(json['property']?.toString(),)!,
    );
  }
}
