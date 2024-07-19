// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:divkit/src/generated_sources/div_size_unit.dart';

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

  DivDimension copyWith({
    Expression<DivSizeUnit>? unit,
    Expression<double>? value,
  }) =>
      DivDimension(
        unit: unit ?? this.unit,
        value: value ?? this.value,
      );

  static DivDimension? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    try {
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
    } catch (e) {
      return null;
    }
  }
}
