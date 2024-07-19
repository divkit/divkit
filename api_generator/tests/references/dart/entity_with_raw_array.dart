// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import '../utils/parsing_utils.dart';

class EntityWithRawArray with EquatableMixin {
  const EntityWithRawArray({
    required this.array,
  });

  static const type = "entity_with_raw_array";

  final Expression<List<dynamic>> array;

  @override
  List<Object?> get props => [
        array,
      ];

  EntityWithRawArray copyWith({
      Expression<List<dynamic>>?  array,
  }) => EntityWithRawArray(
      array: array ?? this.array,
    );

  static EntityWithRawArray? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    try {
      return EntityWithRawArray(
        array: safeParseListExpr(json['array'],)!,
      );
    } catch (e, st) {
      return null;
    }
  }
}
