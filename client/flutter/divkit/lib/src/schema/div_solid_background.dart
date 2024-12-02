// Generated code. Do not modify.

import 'package:divkit/src/utils/parsing.dart';
import 'package:equatable/equatable.dart';

/// Solid background color.
class DivSolidBackground extends Resolvable with EquatableMixin {
  const DivSolidBackground({
    required this.color,
  });

  static const type = "solid";

  /// Color.
  final Expression<Color> color;

  @override
  List<Object?> get props => [
        color,
      ];

  DivSolidBackground copyWith({
    Expression<Color>? color,
  }) =>
      DivSolidBackground(
        color: color ?? this.color,
      );

  static DivSolidBackground? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return DivSolidBackground(
        color: reqVProp<Color>(
          safeParseColorExpr(
            json['color'],
          ),
          name: 'color',
        ),
      );
    } catch (e, st) {
      logger.warning("Parsing error", error: e, stackTrace: st);
      return null;
    }
  }

  @override
  DivSolidBackground resolve(DivVariableContext context) {
    color.resolve(context);
    return this;
  }
}
