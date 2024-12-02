// Generated code. Do not modify.

import 'package:divkit/src/utils/parsing.dart';
import 'package:equatable/equatable.dart';

/// Location of the coordinate of the rotation axis as a percentage relative to the element size.
class DivPivotPercentage extends Resolvable with EquatableMixin {
  const DivPivotPercentage({
    required this.value,
  });

  static const type = "pivot-percentage";

  /// Coordinate value as a percentage.
  final Expression<double> value;

  @override
  List<Object?> get props => [
        value,
      ];

  DivPivotPercentage copyWith({
    Expression<double>? value,
  }) =>
      DivPivotPercentage(
        value: value ?? this.value,
      );

  static DivPivotPercentage? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return DivPivotPercentage(
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

  @override
  DivPivotPercentage resolve(DivVariableContext context) {
    value.resolve(context);
    return this;
  }
}
