// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:divkit/src/generated_sources/div_size_unit.dart';

class DivPivotFixed with EquatableMixin {
  const DivPivotFixed({
    this.unit = const ValueExpression(DivSizeUnit.dp),
    this.value,
  });

  static const type = "pivot-fixed";
  // default value: DivSizeUnit.dp
  final Expression<DivSizeUnit> unit;

  final Expression<int>? value;

  @override
  List<Object?> get props => [
        unit,
        value,
      ];

  DivPivotFixed copyWith({
    Expression<DivSizeUnit>? unit,
    Expression<int>? Function()? value,
  }) =>
      DivPivotFixed(
        unit: unit ?? this.unit,
        value: value != null ? value.call() : this.value,
      );

  static DivPivotFixed? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    return DivPivotFixed(
      unit: safeParseStrEnumExpr(
        json['unit'],
        parse: DivSizeUnit.fromJson,
        fallback: DivSizeUnit.dp,
      )!,
      value: safeParseIntExpr(
        json['value'],
      ),
    );
  }
}
