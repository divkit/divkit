// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import '../utils/parsing_utils.dart';

class EntityWithArrayOfExpressions with EquatableMixin {
  const EntityWithArrayOfExpressions({
    required this.items,
  });

  static const type = "entity_with_array_of_expressions";
  // at least 1 elements
  final Expression<List<String>> items;

  @override
  List<Object?> get props => [
        items,
      ];

  static EntityWithArrayOfExpressions? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    return EntityWithArrayOfExpressions(
      items: safeParseObjExpr((json['items'] as List<dynamic>).map((v) => safeParseStr(v?.toString(),)!,).toList(),)!,
    );
  }
}
