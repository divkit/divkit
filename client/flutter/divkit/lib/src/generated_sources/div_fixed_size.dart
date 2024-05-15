// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import '../utils/parsing_utils.dart';
import 'div_size_unit.dart';

class DivFixedSize with EquatableMixin {
  const DivFixedSize({
    this.unit = const ValueExpression(DivSizeUnit.dp),
    required this.value,
  });

  static const type = "fixed";
  // default value: DivSizeUnit.dp
  final Expression<DivSizeUnit> unit;
  // constraint: number >= 0
  final Expression<int> value;

  @override
  List<Object?> get props => [
        unit,
        value,
      ];

  static DivFixedSize? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    return DivFixedSize(
      unit: safeParseStrEnumExpr(
        json['unit'],
        parse: DivSizeUnit.fromJson,
        fallback: DivSizeUnit.dp,
      )!,
      value: safeParseIntExpr(
        json['value'],
      )!,
    );
  }
}
