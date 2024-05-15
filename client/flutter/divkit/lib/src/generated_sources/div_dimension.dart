// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import '../utils/parsing_utils.dart';
import 'div_size_unit.dart';

class DivDimension with EquatableMixin {
  const DivDimension({
    this.unit = const ValueExpression(DivSizeUnit.dp),
    required this.value,
  });

  // default value: DivSizeUnit.dp
  final Expression<DivSizeUnit> unit;

  final Expression<double> value;

  @override
  List<Object?> get props => [
        unit,
        value,
      ];

  static DivDimension? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    return DivDimension(
      unit: safeParseStrEnumExpr(
        json['unit'],
        parse: DivSizeUnit.fromJson,
        fallback: DivSizeUnit.dp,
      )!,
      value: safeParseDoubleExpr(
        json['value'],
      )!,
    );
  }
}
