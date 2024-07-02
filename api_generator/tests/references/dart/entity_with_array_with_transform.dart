// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import '../utils/parsing_utils.dart';

class EntityWithArrayWithTransform with EquatableMixin {
  const EntityWithArrayWithTransform({
    required this.array,
  });

  static const type = "entity_with_array_with_transform";
  // at least 1 elements
  final Expression<List<Color>> array;

  @override
  List<Object?> get props => [
        array,
      ];

  EntityWithArrayWithTransform copyWith({
      Expression<List<Color>>?  array,
  }) => EntityWithArrayWithTransform(
      array: array ?? this.array,
    );

  static EntityWithArrayWithTransform? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    return EntityWithArrayWithTransform(
      array: safeParseObjExpr(safeListMap(json['array'], (v) => safeParseColor(v,)!),)!,
    );
  }
}
