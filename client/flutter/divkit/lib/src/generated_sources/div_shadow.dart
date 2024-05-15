// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import '../utils/parsing_utils.dart';
import 'div_point.dart';

class DivShadow with EquatableMixin {
  const DivShadow({
    this.alpha = const ValueExpression(0.19),
    this.blur = const ValueExpression(2),
    this.color = const ValueExpression(const Color(0xFF000000)),
    required this.offset,
  });

  // constraint: number >= 0.0 && number <= 1.0; default value: 0.19
  final Expression<double> alpha;
  // constraint: number >= 0; default value: 2
  final Expression<int> blur;
  // default value: const Color(0xFF000000)
  final Expression<Color> color;

  final DivPoint offset;

  @override
  List<Object?> get props => [
        alpha,
        blur,
        color,
        offset,
      ];

  static DivShadow? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    return DivShadow(
      alpha: safeParseDoubleExpr(
        json['alpha'],
        fallback: 0.19,
      )!,
      blur: safeParseIntExpr(
        json['blur'],
        fallback: 2,
      )!,
      color: safeParseColorExpr(
        json['color'],
        fallback: const Color(0xFF000000),
      )!,
      offset: safeParseObj(
        DivPoint.fromJson(json['offset']),
      )!,
    );
  }
}
