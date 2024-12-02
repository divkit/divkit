// Generated code. Do not modify.

import 'package:divkit/src/schema/div_size_unit.dart';
import 'package:divkit/src/utils/parsing.dart';
import 'package:equatable/equatable.dart';

/// Fixed coordinates of the central point of the gradient.
class DivRadialGradientFixedCenter extends Resolvable with EquatableMixin {
  const DivRadialGradientFixedCenter({
    this.unit = const ValueExpression(DivSizeUnit.dp),
    required this.value,
  });

  static const type = "fixed";

  /// Unit of measurement. To learn more about units of size measurement, see [Layout inside the card](https://divkit.tech/docs/en/concepts/layout).
  // default value: DivSizeUnit.dp
  final Expression<DivSizeUnit> unit;

  /// Coordinate value.
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

  static DivRadialGradientFixedCenter? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return DivRadialGradientFixedCenter(
        unit: reqVProp<DivSizeUnit>(
          safeParseStrEnumExpr(
            json['unit'],
            parse: DivSizeUnit.fromJson,
            fallback: DivSizeUnit.dp,
          ),
          name: 'unit',
        ),
        value: reqVProp<int>(
          safeParseIntExpr(
            json['value'],
          ),
          name: 'value',
        ),
      );
    } catch (e, st) {
      logger.warning("Parsing error", error: e, stackTrace: st);
      return null;
    }
  }

  @override
  DivRadialGradientFixedCenter resolve(DivVariableContext context) {
    unit.resolve(context);
    value.resolve(context);
    return this;
  }
}
