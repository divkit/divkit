// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import 'entity.dart';
import 'package:divkit/src/utils/parsing.dart';


class EntityWithArray extends Resolvable with EquatableMixin  {
  const EntityWithArray({
    required this.array,
  });

  static const type = "entity_with_array";
   // at least 1 elements
  final Arr<Entity> array;

  @override
  List<Object?> get props => [
        array,
      ];

  EntityWithArray copyWith({
      Arr<Entity>?  array,
  }) => EntityWithArray(
      array: array ?? this.array,
    );

  static EntityWithArray? fromJson(Map<String, dynamic>? json,) {
    if (json == null) {
      return null;
    }
    try {
      return EntityWithArray(
        array: reqProp<Arr<Entity>>(safeParseObjects(json['array'],(v) => reqProp<Entity>(safeParseObject(v, parse: Entity.fromJson,),), ), name: 'array',),
      );
    } catch (e, st) {
      logger.warning("Parsing error", error: e, stackTrace: st);
      return null;
    }
  }

  EntityWithArray resolve(DivVariableContext context) {
    tryResolveList(array, (v) => v.resolve(context));
    return this;
  }
}
