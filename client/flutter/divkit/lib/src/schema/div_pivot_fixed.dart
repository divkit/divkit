// Generated code. Do not modify.

import 'package:divkit/src/schema/div_size_unit.dart';
import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:equatable/equatable.dart';

/// Fixed coordinates of the rotation axis.
class DivPivotFixed extends Preloadable with EquatableMixin {
  const DivPivotFixed({
    this.unit = const ValueExpression(DivSizeUnit.dp),
    this.value,
  });

  static const type = "pivot-fixed";

  /// Measurement unit. To learn more about units of size measurement, see [Layout inside the card](https://divkit.tech/docs/en/concepts/layout).
  // default value: DivSizeUnit.dp
  final Expression<DivSizeUnit> unit;

  /// Coordinate value.
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

  static DivPivotFixed? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
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
    } catch (e) {
      return null;
    }
  }

  static Future<DivPivotFixed?> parse(
    Map<String, dynamic>? json,
  ) async {
    if (json == null) {
      return null;
    }
    try {
      return DivPivotFixed(
        unit: (await safeParseStrEnumExprAsync(
          json['unit'],
          parse: DivSizeUnit.fromJson,
          fallback: DivSizeUnit.dp,
        ))!,
        value: await safeParseIntExprAsync(
          json['value'],
        ),
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
      await value?.preload(context);
    } catch (e) {
      return;
    }
  }
}
