// Generated code. Do not modify.

import 'package:divkit/src/schema/div_point.dart';
import 'package:divkit/src/utils/parsing.dart';
import 'package:equatable/equatable.dart';

/// Element shadow.
class DivShadow extends Resolvable with EquatableMixin {
  const DivShadow({
    this.alpha = const ValueExpression(0.19),
    this.blur = const ValueExpression(2),
    this.color = const ValueExpression(Color(0xFF000000)),
    required this.offset,
  });

  /// Shadow transparency.
  // constraint: number >= 0.0 && number <= 1.0; default value: 0.19
  final Expression<double> alpha;

  /// Blur intensity.
  // constraint: number >= 0; default value: 2
  final Expression<int> blur;

  /// Shadow color.
  // default value: const Color(0xFF000000)
  final Expression<Color> color;

  /// Shadow offset.
  final DivPoint offset;

  @override
  List<Object?> get props => [
        alpha,
        blur,
        color,
        offset,
      ];

  DivShadow copyWith({
    Expression<double>? alpha,
    Expression<int>? blur,
    Expression<Color>? color,
    DivPoint? offset,
  }) =>
      DivShadow(
        alpha: alpha ?? this.alpha,
        blur: blur ?? this.blur,
        color: color ?? this.color,
        offset: offset ?? this.offset,
      );

  static DivShadow? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return DivShadow(
        alpha: reqVProp<double>(
          safeParseDoubleExpr(
            json['alpha'],
            fallback: 0.19,
          ),
          name: 'alpha',
        ),
        blur: reqVProp<int>(
          safeParseIntExpr(
            json['blur'],
            fallback: 2,
          ),
          name: 'blur',
        ),
        color: reqVProp<Color>(
          safeParseColorExpr(
            json['color'],
            fallback: const Color(0xFF000000),
          ),
          name: 'color',
        ),
        offset: reqProp<DivPoint>(
          safeParseObject(
            json['offset'],
            parse: DivPoint.fromJson,
          ),
          name: 'offset',
        ),
      );
    } catch (e, st) {
      logger.warning("Parsing error", error: e, stackTrace: st);
      return null;
    }
  }

  @override
  DivShadow resolve(DivVariableContext context) {
    alpha.resolve(context);
    blur.resolve(context);
    color.resolve(context);
    offset.resolve(context);
    return this;
  }
}
