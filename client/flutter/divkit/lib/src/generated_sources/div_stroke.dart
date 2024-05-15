// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import '../utils/parsing_utils.dart';
import 'div_size_unit.dart';

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
  final Expression<int> width;

  @override
  List<Object?> get props => [
        color,
        unit,
        width,
      ];

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
      width: safeParseIntExpr(
        json['width'],
        fallback: 1,
      )!,
    );
  }
}
