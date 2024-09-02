// Generated code. Do not modify.

import 'package:divkit/src/schema/div_size_unit.dart';
import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:equatable/equatable.dart';

class DivDimension extends Preloadable with EquatableMixin {
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

  static DivDimension? fromJson(
    Map<String, dynamic>? json,
  ) {
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

  static Future<DivDimension?> parse(
    Map<String, dynamic>? json,
  ) async {
    if (json == null) {
      return null;
    }
    try {
      return DivDimension(
        unit: (await safeParseStrEnumExprAsync(
          json['unit'],
          parse: DivSizeUnit.fromJson,
          fallback: DivSizeUnit.dp,
        ))!,
        value: (await safeParseDoubleExprAsync(
          json['value'],
        ))!,
      );
    } catch (e) {
      return null;
    }
  }

  @override
  Future<void> preload(
    Map<String, dynamic> context,
  ) async {
    try {
      await unit.preload(context);
      await value.preload(context);
    } catch (e) {
      return;
    }
  }
}
