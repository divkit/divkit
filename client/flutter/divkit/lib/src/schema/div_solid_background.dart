// Generated code. Do not modify.

import 'package:divkit/src/utils/parsing_utils.dart';
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
        color: safeParseColorExpr(
          json['color'],
        )!,
      );
    } catch (e) {
      return null;
    }
  }

  @override
  DivSolidBackground resolve(DivVariableContext context) {
    color.resolve(context);
    return this;
  }
}
