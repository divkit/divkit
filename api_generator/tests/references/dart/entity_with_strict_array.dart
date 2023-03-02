// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import '../utils/parsing_extensions.dart';
import 'entity.dart';

class EntityWithStrictArray with EquatableMixin {
  const EntityWithStrictArray({
    required this.array,
  });

  static const type = "entity_with_strict_array";
  // at least 1 elements; all received elements must be valid
  final List<Entity> array;

  @override
  List<Object?> get props => [
        array,
      ];

  static EntityWithStrictArray? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    return EntityWithStrictArray(
      array: (json['array'] as List<dynamic>).map((j) => Entity.fromJson(j as Map <String, dynamic>)!).toList(),
    );
  }
}
