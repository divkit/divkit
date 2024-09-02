// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import 'package:divkit/src/utils/parsing_utils.dart';

class EntityWithJsonProperty extends Preloadable with EquatableMixin  {
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

  static EntityWithJsonProperty? fromJson(Map<String, dynamic>? json,) {
    if (json == null) {
      return null;
    }
    try {
      return EntityWithJsonProperty(
        jsonProperty: safeParseMap(json['json_property'], fallback: None,)!,
      );
    } catch (e, st) {
      return null;
    }
  }

  static Future<EntityWithJsonProperty?> parse(Map<String, dynamic>? json,) async {
    if (json == null) {
      return null;
    }
    try {
      return EntityWithJsonProperty(
        jsonProperty: (await safeParseMapAsync(json['json_property'], fallback: None,))!,
      );
    } catch (e, st) {
      return null;
    }
  }

  Future<void> preload(Map<String, dynamic> context,) async {
    try {
    } catch (e, st) {
      return;
    }
  }
}
