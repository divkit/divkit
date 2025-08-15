// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import 'package:divkit/src/utils/parsing.dart';


class EntityWithStringArrayProperty with EquatableMixin  {
  const EntityWithStringArrayProperty({
    required this.array,
  });

  static const type = "entity_with_string_array_property";
   // at least 1 elements
  final Expression<Arr<String>> array;

  @override
  List<Object?> get props => [
        array,
      ];

  EntityWithStringArrayProperty copyWith({
      Expression<Arr<String>>?  array,
  }) => EntityWithStringArrayProperty(
      array: array ?? this.array,
    );

  static EntityWithStringArrayProperty? fromJson(Map<String, dynamic>? json,) {
    if (json == null) {
      return null;
    }
    try {
      return EntityWithStringArrayProperty(
        array: reqVProp<Arr<String>>(safeParseObjectsExpr(json['array'],(v) => reqProp<String>(safeParseStr(v),), ), name: 'array',),
      );
    } catch (e, st) {
      logger.warning("Parsing error", error: e, stackTrace: st);
      return null;
    }
  }
}
