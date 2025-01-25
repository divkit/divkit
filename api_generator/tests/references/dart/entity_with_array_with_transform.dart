// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import 'package:divkit/src/utils/parsing.dart';


class EntityWithArrayWithTransform with EquatableMixin  {
  const EntityWithArrayWithTransform({
    required this.array,
  });

  static const type = "entity_with_array_with_transform";
   // at least 1 elements
  final Expression<Arr<Color>> array;

  @override
  List<Object?> get props => [
        array,
      ];

  EntityWithArrayWithTransform copyWith({
      Expression<Arr<Color>>?  array,
  }) => EntityWithArrayWithTransform(
      array: array ?? this.array,
    );

  static EntityWithArrayWithTransform? fromJson(Map<String, dynamic>? json,) {
    if (json == null) {
      return null;
    }
    try {
      return EntityWithArrayWithTransform(
        array: reqVProp<Arr<Color>>(safeParseObjectsExpr(json['array'],(v) => reqProp<Color>(safeParseColor(v),), ), name: 'array',),
      );
    } catch (e, st) {
      logger.warning("Parsing error", error: e, stackTrace: st);
      return null;
    }
  }
}
