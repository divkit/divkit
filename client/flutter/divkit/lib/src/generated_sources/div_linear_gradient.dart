// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import 'package:divkit/src/utils/parsing_utils.dart';

class DivLinearGradient with EquatableMixin {
  const DivLinearGradient({
    this.angle = const ValueExpression(0),
    required this.colors,
  });

  static const type = "gradient";
  // constraint: number >= 0 && number <= 360; default value: 0
  final Expression<int> angle;
  // at least 2 elements
  final Expression<List<Color>> colors;

  @override
  List<Object?> get props => [
        angle,
        colors,
      ];

  DivLinearGradient copyWith({
    Expression<int>? angle,
    Expression<List<Color>>? colors,
  }) =>
      DivLinearGradient(
        angle: angle ?? this.angle,
        colors: colors ?? this.colors,
      );

  static DivLinearGradient? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    try {
      return DivLinearGradient(
        angle: safeParseIntExpr(
          json['angle'],
          fallback: 0,
        )!,
        colors: safeParseObjExpr(
          safeListMap(
            json['colors'],
            (v) => safeParseColor(
              v,
            )!,
          ),
        )!,
      );
    } catch (e) {
      return null;
    }
  }
}
