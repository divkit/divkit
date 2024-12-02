// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import 'package:divkit/src/utils/parsing.dart';


class EntityWithRawArray extends Resolvable with EquatableMixin  {
  const EntityWithRawArray({
    required this.array,
  });

  static const type = "entity_with_raw_array";
  final Expression<Arr> array;

  @override
  List<Object?> get props => [
        array,
      ];

  EntityWithRawArray copyWith({
      Expression<Arr>?  array,
  }) => EntityWithRawArray(
      array: array ?? this.array,
    );

  static EntityWithRawArray? fromJson(Map<String, dynamic>? json,) {
    if (json == null) {
      return null;
    }
    try {
      return EntityWithRawArray(
        array: reqVProp<Arr>(safeParseListExpr(json['array'],), name: 'array',),
      );
    } catch (e, st) {
      logger.warning("Parsing error", error: e, stackTrace: st);
      return null;
    }
  }

  EntityWithRawArray resolve(DivVariableContext context) {
    array.resolve(context);
    return this;
  }
}
