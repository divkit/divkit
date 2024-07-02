// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import '../utils/parsing_utils.dart';

class EntityWithJsonProperty with EquatableMixin {
  const EntityWithJsonProperty({
    this.jsonProperty = None,
  });

  static const type = "entity_with_json_property";
  // default value: None
  final Map<String, dynamic> jsonProperty;

  @override
  List<Object?> get props => [
        jsonProperty,
      ];

  EntityWithJsonProperty copyWith({
      Map<String, dynamic>?  jsonProperty,
  }) => EntityWithJsonProperty(
      jsonProperty: jsonProperty ?? this.jsonProperty,
    );

  static EntityWithJsonProperty? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    return EntityWithJsonProperty(
      jsonProperty: safeParseMap(json, fallback: None,)!,
    );
  }
}
