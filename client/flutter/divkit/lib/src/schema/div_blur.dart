// Generated code. Do not modify.

import 'package:divkit/src/utils/parsing.dart';
import 'package:equatable/equatable.dart';

/// Gaussian image blur.
class DivBlur extends Resolvable with EquatableMixin {
  const DivBlur({
    required this.radius,
  });

  static const type = "blur";

  /// Blur radius. Defines how many pixels blend into each other. Specified in: `dp`.
  // constraint: number >= 0
  final Expression<int> radius;

  @override
  List<Object?> get props => [
        radius,
      ];

  DivBlur copyWith({
    Expression<int>? radius,
  }) =>
      DivBlur(
        radius: radius ?? this.radius,
      );

  static DivBlur? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return DivBlur(
        radius: reqVProp<int>(
          safeParseIntExpr(
            json['radius'],
          ),
          name: 'radius',
        ),
      );
    } catch (e, st) {
      logger.warning("Parsing error", error: e, stackTrace: st);
      return null;
    }
  }

  @override
  DivBlur resolve(DivVariableContext context) {
    radius.resolve(context);
    return this;
  }
}
