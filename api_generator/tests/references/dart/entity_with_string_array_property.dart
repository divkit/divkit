// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import '../utils/parsing_utils.dart';

class EntityWithStringArrayProperty with EquatableMixin {
  const EntityWithStringArrayProperty({
    required this.array,
  });

  static const type = "entity_with_string_array_property";
  // at least 1 elements
  final Expression<List<String>> array;

  @override
  List<Object?> get props => [
        array,
      ];

  static EntityWithStringArrayProperty? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    return EntityWithStringArrayProperty(
      array: safeParseClassExpr((json['array'] as List<dynamic>).map((v) => (v as String)).toList())!,
    );
  }
}
