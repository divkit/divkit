// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import 'package:divkit/src/utils/parsing.dart';


class EntityWithJsonProperty with EquatableMixin  {
  const EntityWithJsonProperty({
    this.jsonProperty = None,
  });

  static const type = "entity_with_json_property";
   // default value: None
  final Obj jsonProperty;

  @override
  List<Object?> get props => [
        jsonProperty,
      ];

  EntityWithJsonProperty copyWith({
      Obj?  jsonProperty,
  }) => EntityWithJsonProperty(
      jsonProperty: jsonProperty ?? this.jsonProperty,
    );

  static EntityWithJsonProperty? fromJson(Map<String, dynamic>? json,) {
    if (json == null) {
      return null;
    }
    try {
      return EntityWithJsonProperty(
        jsonProperty: reqProp<Obj>(safeParseMap(json['json_property'], fallback: None,), name: 'json_property',),
      );
    } catch (e, st) {
      logger.warning("Parsing error", error: e, stackTrace: st);
      return null;
    }
  }
}
