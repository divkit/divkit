// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import '../utils/parsing_extensions.dart';

class EntityWithRawArray with EquatableMixin {
  const EntityWithRawArray({
    required this.array,
  });

  static const type = "entity_with_raw_array";

  final List<dynamic> array;

  @override
  List<Object?> get props => [
        array,
      ];

  static EntityWithRawArray? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    return EntityWithRawArray(
      array: json['array']!,
    );
  }
}
