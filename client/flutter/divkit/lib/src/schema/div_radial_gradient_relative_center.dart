// Generated code. Do not modify.

import 'package:divkit/src/utils/parsing.dart';
import 'package:equatable/equatable.dart';

/// Location of the central point of the gradient relative to the element borders.
class DivRadialGradientRelativeCenter extends Resolvable with EquatableMixin {
  const DivRadialGradientRelativeCenter({
    required this.value,
  });

  static const type = "relative";

  /// Coordinate value in the range "0...1".
  final Expression<double> value;

  @override
  List<Object?> get props => [
        value,
      ];

  DivRadialGradientRelativeCenter copyWith({
    Expression<double>? value,
  }) =>
      DivRadialGradientRelativeCenter(
        value: value ?? this.value,
      );

  static DivRadialGradientRelativeCenter? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return DivRadialGradientRelativeCenter(
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
  DivRadialGradientRelativeCenter resolve(DivVariableContext context) {
    value.resolve(context);
    return this;
  }
}
