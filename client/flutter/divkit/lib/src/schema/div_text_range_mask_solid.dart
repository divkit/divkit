// Generated code. Do not modify.

import 'package:divkit/src/utils/parsing.dart';
import 'package:equatable/equatable.dart';

/// A mask to hide text (spoiler) that looks like a rectangle filled with color specified by `color` parameter.
class DivTextRangeMaskSolid with EquatableMixin {
  const DivTextRangeMaskSolid({
    required this.color,
    this.isEnabled = const ValueExpression(true),
  });

  static const type = "solid";

  /// Color.
  final Expression<Color> color;

  /// Controls mask state: if set to `true` mask will hide specified part of the text, otherwise the text will be shown.
  // default value: true
  final Expression<bool> isEnabled;

  @override
  List<Object?> get props => [
        color,
        isEnabled,
      ];

  DivTextRangeMaskSolid copyWith({
    Expression<Color>? color,
    Expression<bool>? isEnabled,
  }) =>
      DivTextRangeMaskSolid(
        color: color ?? this.color,
        isEnabled: isEnabled ?? this.isEnabled,
      );

  static DivTextRangeMaskSolid? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return DivTextRangeMaskSolid(
        color: reqVProp<Color>(
          safeParseColorExpr(
            json['color'],
          ),
          name: 'color',
        ),
        isEnabled: reqVProp<bool>(
          safeParseBoolExpr(
            json['is_enabled'],
            fallback: true,
          ),
          name: 'is_enabled',
        ),
      );
    } catch (e, st) {
      logger.warning("Parsing error", error: e, stackTrace: st);
      return null;
    }
  }
}
