// Generated code. Do not modify.

import 'package:divkit/src/schema/div_edge_insets.dart';
import 'package:divkit/src/utils/parsing.dart';
import 'package:equatable/equatable.dart';

/// Cloud-style text background. Rows have a rectangular background in the specified color with rounded corners.
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

  /// Margins between the row border and background border.
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
        color: reqVProp<Color>(
          safeParseColorExpr(
            json['color'],
          ),
          name: 'color',
        ),
        cornerRadius: reqVProp<int>(
          safeParseIntExpr(
            json['corner_radius'],
          ),
          name: 'corner_radius',
        ),
        paddings: reqProp<DivEdgeInsets>(
          safeParseObject(
            json['paddings'],
            parse: DivEdgeInsets.fromJson,
            fallback: const DivEdgeInsets(),
          ),
          name: 'paddings',
        ),
      );
    } catch (e, st) {
      logger.warning("Parsing error", error: e, stackTrace: st);
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
