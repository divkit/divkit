// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:divkit/src/generated_sources/div_size_unit.dart';

class DivStroke with EquatableMixin {
  const DivStroke({
    required this.color,
    this.unit = const ValueExpression(DivSizeUnit.dp),
    this.width = const ValueExpression(1),
  });

  final Expression<Color> color;
  // default value: DivSizeUnit.dp
  final Expression<DivSizeUnit> unit;
  // constraint: number >= 0; default value: 1
  final Expression<double> width;

  @override
  List<Object?> get props => [
        color,
        unit,
        width,
      ];

  DivStroke copyWith({
    Expression<Color>? color,
    Expression<DivSizeUnit>? unit,
    Expression<double>? width,
  }) =>
      DivStroke(
        color: color ?? this.color,
        unit: unit ?? this.unit,
        width: width ?? this.width,
      );

  static DivStroke? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    return DivStroke(
      color: safeParseColorExpr(
        json['color'],
      )!,
      unit: safeParseStrEnumExpr(
        json['unit'],
        parse: DivSizeUnit.fromJson,
        fallback: DivSizeUnit.dp,
      )!,
      width: safeParseDoubleExpr(
        json['width'],
        fallback: 1,
      )!,
    );
  }
}
