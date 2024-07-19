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
    this.integer = const ValueExpression(0),
    this.positiveInteger,
    this.string,
    this.url,
  });

  static const type = "entity_with_simple_properties";

  final Expression<bool>? boolean;

  final Expression<bool>? booleanInt;

  final Expression<Color>? color;

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

  EntityWithSimpleProperties copyWith({
      Expression<bool>? Function()?  boolean,
      Expression<bool>? Function()?  booleanInt,
      Expression<Color>? Function()?  color,
      Expression<double>? Function()?  dNum,
      int?  id,
      Expression<int>?  integer,
      Expression<int>? Function()?  positiveInteger,
      Expression<String>? Function()?  string,
      Expression<Uri>? Function()?  url,
  }) => EntityWithSimpleProperties(
      boolean: boolean != null ? boolean.call() : this.boolean,
      booleanInt: booleanInt != null ? booleanInt.call() : this.booleanInt,
      color: color != null ? color.call() : this.color,
      dNum: dNum != null ? dNum.call() : this.dNum,
      id: id ?? this.id,
      integer: integer ?? this.integer,
      positiveInteger: positiveInteger != null ? positiveInteger.call() : this.positiveInteger,
      string: string != null ? string.call() : this.string,
      url: url != null ? url.call() : this.url,
    );

  static EntityWithSimpleProperties? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    try {
      return EntityWithSimpleProperties(
        boolean: safeParseBoolExpr(json['boolean'],),
        booleanInt: safeParseBoolExpr(json['boolean_int'],),
        color: safeParseColorExpr(json['color'],),
        dNum: safeParseDoubleExpr(json['dNum'],),
        id: safeParseInt(json['id'], fallback: 0,)!,
        integer: safeParseIntExpr(json['integer'], fallback: 0,)!,
        positiveInteger: safeParseIntExpr(json['positive_integer'],),
        string: safeParseStrExpr(json['string']?.toString(),),
        url: safeParseUriExpr(json['url']),
      );
    } catch (e, st) {
      return null;
    }
  }
}
