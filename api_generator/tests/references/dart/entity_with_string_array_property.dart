// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import 'package:divkit/src/utils/parsing_utils.dart';


class EntityWithStringArrayProperty extends Resolvable with EquatableMixin  {
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

  EntityWithStringArrayProperty resolve(DivVariableContext context) {
    array.resolve(context);
    return this;
  }
}
