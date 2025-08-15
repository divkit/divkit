// Generated code. Do not modify.

import 'package:divkit/src/schema/div_size_unit.dart';
import 'package:divkit/src/utils/parsing.dart';
import 'package:equatable/equatable.dart';

/// Element dimension value.
class DivDimension with EquatableMixin {
  const DivDimension({
    this.unit = const ValueExpression(DivSizeUnit.dp),
    required this.value,
  });

  // default value: DivSizeUnit.dp
  final Expression<DivSizeUnit> unit;

  /// Value.
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
        unit: reqVProp<DivSizeUnit>(
          safeParseStrEnumExpr(
            json['unit'],
            parse: DivSizeUnit.fromJson,
            fallback: DivSizeUnit.dp,
          ),
          name: 'unit',
        ),
        value: reqVProp<double>(
          safeParseDoubleExpr(
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
}
