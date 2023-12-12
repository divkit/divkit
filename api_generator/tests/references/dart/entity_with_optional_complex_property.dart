// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import '../utils/parsing_utils.dart';

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
      property: safeParseClass(EntityWithOptionalComplexPropertyProperty.fromJson(json['property'])),
    );
  }
}

class EntityWithOptionalComplexPropertyProperty with EquatableMixin {
  const EntityWithOptionalComplexPropertyProperty({
    required this.value,
  });


  final Expression<Uri> value;

  @override
  List<Object?> get props => [
        value,
      ];

  static EntityWithOptionalComplexPropertyProperty? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    return EntityWithOptionalComplexPropertyProperty(
      value: safeParseUriExpr(json['value'])!,
    );
  }
}
