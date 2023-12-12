// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import '../utils/parsing_utils.dart';

class EntityWithSimpleProperties with EquatableMixin {
  const EntityWithSimpleProperties({
    this.boolean,
    this.booleanInt,
    this.color,
    this.dNum,
    this.id = 0,
    this.integer = const Expression.value(0),
    this.positiveInteger,
    this.string,
    this.url,
  });

  static const type = "entity_with_simple_properties";

  final Expression<bool>? boolean;

  final Expression<bool>? booleanInt;

  final Expression<int>? color;

  final Expression<double>? dNum;
  // default value: 0
  final int id;
  // default value: 0
  final Expression<int> integer;
  // constraint: number > 0
  final Expression<int>? positiveInteger;

  final Expression<String>? string;

  final Expression<Uri>? url;

  @override
  List<Object?> get props => [
        boolean,
        booleanInt,
        color,
        dNum,
        id,
        integer,
        positiveInteger,
        string,
        url,
      ];

  static EntityWithSimpleProperties? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    return EntityWithSimpleProperties(
      boolean: safeParseBoolExpr(json['boolean']),
      booleanInt: safeParseBoolExpr(json['boolean_int']),
      color: safeParseColorExpr(json['color']),
      dNum: safeParseDoubleExpr(json['dNum']),
      id: safeParseInt(json['id']) ?? 0,
      integer: safeParseIntExpr(json['integer']) ?? const Expression.value(0),
      positiveInteger: safeParseIntExpr(json['positive_integer']),
      string: safeParseStrExpr(json['string']?.toString()),
      url: safeParseUriExpr(json['url']),
    );
  }
}
