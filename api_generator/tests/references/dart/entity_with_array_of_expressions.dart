// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import 'package:divkit/src/utils/parsing.dart';


class EntityWithArrayOfExpressions with EquatableMixin  {
  const EntityWithArrayOfExpressions({
    required this.items,
  });

  static const type = "entity_with_array_of_expressions";
   // at least 1 elements
  final Expression<Arr<String>> items;

  @override
  List<Object?> get props => [
        items,
      ];

  EntityWithArrayOfExpressions copyWith({
      Expression<Arr<String>>?  items,
  }) => EntityWithArrayOfExpressions(
      items: items ?? this.items,
    );

  static EntityWithArrayOfExpressions? fromJson(Map<String, dynamic>? json,) {
    if (json == null) {
      return null;
    }
    try {
      return EntityWithArrayOfExpressions(
        items: reqVProp<Arr<String>>(safeParseObjectsExpr(json['items'],(v) => reqProp<String>(safeParseStr(v),), ), name: 'items',),
      );
    } catch (e, st) {
      logger.warning("Parsing error", error: e, stackTrace: st);
      return null;
    }
  }
}
