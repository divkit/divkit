// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import 'package:divkit/src/utils/parsing.dart';

/// Entity with simple properties.
class EntityWithSimpleProperties with EquatableMixin  {
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
  /// Boolean property.
  final Expression<bool>? boolean;
  /// Boolean value in numeric format.
  final Expression<bool>? booleanInt;
  /// Color.
  final Expression<Color>? color;
  /// Floating point number.
  final Expression<double>? dNum;
  /// ID. Can't contain expressions.
   // default value: 0
  final int id;
  /// Integer.
   // default value: 0
  final Expression<int> integer;
  /// Positive integer.
   // constraint: number > 0
  final Expression<int>? positiveInteger;
  /// String.
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

  static EntityWithSimpleProperties? fromJson(Map<String, dynamic>? json,) {
    if (json == null) {
      return null;
    }
    try {
      return EntityWithSimpleProperties(
        boolean: safeParseBoolExpr(json['boolean'],),
        booleanInt: safeParseBoolExpr(json['boolean_int'],),
        color: safeParseColorExpr(json['color'],),
        dNum: safeParseDoubleExpr(json['dNum'],),
        id: reqProp<int>(safeParseInt(json['id'], fallback: 0,), name: 'id',),
        integer: reqVProp<int>(safeParseIntExpr(json['integer'], fallback: 0,), name: 'integer',),
        positiveInteger: safeParseIntExpr(json['positive_integer'],),
        string: safeParseStrExpr(json['string'],),
        url: safeParseUriExpr(json['url'],),
      );
    } catch (e, st) {
      logger.warning("Parsing error", error: e, stackTrace: st);
      return null;
    }
  }
}
