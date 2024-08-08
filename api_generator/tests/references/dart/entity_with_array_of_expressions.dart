// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import 'package:divkit/src/utils/parsing_utils.dart';

class EntityWithArrayOfExpressions extends Preloadable with EquatableMixin  {
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

  EntityWithArrayOfExpressions copyWith({
      Expression<List<String>>?  items,
  }) => EntityWithArrayOfExpressions(
      items: items ?? this.items,
    );

  static EntityWithArrayOfExpressions? fromJson(Map<String, dynamic>? json,) {
    if (json == null) {
      return null;
    }
    try {
      return EntityWithArrayOfExpressions(
        items: safeParseObjExpr(safeListMap(json['items'], (v) => safeParseStr(v?.toString(),)!,),)!,
      );
    } catch (e, st) {
      return null;
    }
  }

  static Future<EntityWithArrayOfExpressions?> parse(Map<String, dynamic>? json,) async {
    if (json == null) {
      return null;
    }
    try {
      return EntityWithArrayOfExpressions(
        items: (await safeParseObjExprAsync(await safeListMapAsync(json['items'], (v) => safeParseStr(v?.toString(),)!,),))!,
      );
    } catch (e, st) {
      return null;
    }
  }

  Future<void> preload(Map<String, dynamic> context,) async {
    try {
    await items.preload(context);
    } catch (e, st) {
      return;
    }
  }
}
