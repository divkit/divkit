// Generated code. Do not modify.

import 'package:divkit/src/schema/div_size_unit.dart';
import 'package:divkit/src/utils/parsing.dart';
import 'package:equatable/equatable.dart';

/// Fixed coordinates of the rotation axis.
class DivPivotFixed extends Resolvable with EquatableMixin {
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
        unit: reqVProp<DivSizeUnit>(
          safeParseStrEnumExpr(
            json['unit'],
            parse: DivSizeUnit.fromJson,
            fallback: DivSizeUnit.dp,
          ),
          name: 'unit',
        ),
        value: safeParseIntExpr(
          json['value'],
        ),
      );
    } catch (e, st) {
      logger.warning("Parsing error", error: e, stackTrace: st);
      return null;
    }
  }

  @override
  DivPivotFixed resolve(DivVariableContext context) {
    unit.resolve(context);
    value?.resolve(context);
    return this;
  }
}
