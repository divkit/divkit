// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import 'package:divkit/src/utils/parsing_utils.dart';


class EntityWithStringArrayProperty extends Preloadable with EquatableMixin  {
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

  EntityWithStringArrayProperty copyWith({
      Expression<List<String>>?  array,
  }) => EntityWithStringArrayProperty(
      array: array ?? this.array,
    );

  static EntityWithStringArrayProperty? fromJson(Map<String, dynamic>? json,) {
    if (json == null) {
      return null;
    }
    try {
      return EntityWithStringArrayProperty(
        array: safeParseObjExpr(safeListMap(json['array'], (v) => safeParseStr(v?.toString(),)!,),)!,
      );
    } catch (e, st) {
      return null;
    }
  }

  static Future<EntityWithStringArrayProperty?> parse(Map<String, dynamic>? json,) async {
    if (json == null) {
      return null;
    }
    try {
      return EntityWithStringArrayProperty(
        array: (await safeParseObjExprAsync(await safeListMapAsync(json['array'], (v) => safeParseStr(v?.toString(),)!,),))!,
      );
    } catch (e, st) {
      return null;
    }
  }

  Future<void> preload(Map<String, dynamic> context,) async {
    try {
    await array.preload(context);
    } catch (e, st) {
      return;
    }
  }
}
