// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import '../utils/parsing_utils.dart';
import 'entity.dart';

class EntityWithArray with EquatableMixin {
  const EntityWithArray({
    required this.array,
  });

  static const type = "entity_with_array";
  // at least 1 elements
  final List<Entity> array;

  @override
  List<Object?> get props => [
        array,
      ];

  EntityWithArray copyWith({
      List<Entity>?  array,
  }) => EntityWithArray(
      array: array ?? this.array,
    );

  static EntityWithArray? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    try {
      return EntityWithArray(
        array: safeParseObj(safeListMap(json['array'], (v) => safeParseObj(Entity.fromJson(v),)!),)!,
      );
    } catch (e, st) {
      return null;
    }
  }
}
