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

  static EntityWithArray? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    return EntityWithArray(
      array: safeParseObj((json['array'] as List<dynamic>).map((v) => safeParseObj(Entity.fromJson(v),)!,).toList(),)!,
    );
  }
}
