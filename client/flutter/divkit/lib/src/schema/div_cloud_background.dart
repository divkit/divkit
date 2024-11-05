// Generated code. Do not modify.

import 'package:divkit/src/schema/div_edge_insets.dart';
import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:equatable/equatable.dart';

/// Cloud text background. Lines draws a rectangular background with the specified color and rounded corners.
class DivCloudBackground extends Resolvable with EquatableMixin {
  const DivCloudBackground({
    required this.color,
    required this.cornerRadius,
    this.paddings = const DivEdgeInsets(),
  });

  static const type = "cloud";

  /// Fill color.
  final Expression<Color> color;

  /// Corner rounding radius.
  // constraint: number >= 0
  final Expression<int> cornerRadius;

  /// Margins between line bounds and background.
  final DivEdgeInsets paddings;

  @override
  List<Object?> get props => [
        color,
        cornerRadius,
        paddings,
      ];

  DivCloudBackground copyWith({
    Expression<Color>? color,
    Expression<int>? cornerRadius,
    DivEdgeInsets? paddings,
  }) =>
      DivCloudBackground(
        color: color ?? this.color,
        cornerRadius: cornerRadius ?? this.cornerRadius,
        paddings: paddings ?? this.paddings,
      );

  static DivCloudBackground? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return DivCloudBackground(
        color: safeParseColorExpr(
          json['color'],
        )!,
        cornerRadius: safeParseIntExpr(
          json['corner_radius'],
        )!,
        paddings: safeParseObj(
          DivEdgeInsets.fromJson(json['paddings']),
          fallback: const DivEdgeInsets(),
        )!,
      );
    } catch (e) {
      return null;
    }
  }

  @override
  DivCloudBackground resolve(DivVariableContext context) {
    color.resolve(context);
    cornerRadius.resolve(context);
    paddings.resolve(context);
    return this;
  }
}
