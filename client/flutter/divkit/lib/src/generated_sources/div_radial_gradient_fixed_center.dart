// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:divkit/src/generated_sources/div_size_unit.dart';

class DivRadialGradientFixedCenter with EquatableMixin {
  const DivRadialGradientFixedCenter({
    this.unit = const ValueExpression(DivSizeUnit.dp),
    required this.value,
  });

  static const type = "fixed";
  // default value: DivSizeUnit.dp
  final Expression<DivSizeUnit> unit;

  final Expression<int> value;

  @override
  List<Object?> get props => [
        unit,
        value,
      ];

  DivRadialGradientFixedCenter copyWith({
    Expression<DivSizeUnit>? unit,
    Expression<int>? value,
  }) =>
      DivRadialGradientFixedCenter(
        unit: unit ?? this.unit,
        value: value ?? this.value,
      );

  static DivRadialGradientFixedCenter? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    return DivRadialGradientFixedCenter(
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
