// Generated code. Do not modify.

import 'package:divkit/src/schema/div_stroke.dart';
import 'package:divkit/src/utils/parsing.dart';
import 'package:equatable/equatable.dart';

/// Character range border.
class DivTextRangeBorder with EquatableMixin {
  const DivTextRangeBorder({
    this.cornerRadius,
    this.stroke,
  });

  /// One radius of element and stroke corner rounding. Has a lower priority than `corners_radius`.
  // constraint: number >= 0
  final Expression<int>? cornerRadius;

  /// Stroke style.
  final DivStroke? stroke;

  @override
  List<Object?> get props => [
        cornerRadius,
        stroke,
      ];

  DivTextRangeBorder copyWith({
    Expression<int>? Function()? cornerRadius,
    DivStroke? Function()? stroke,
  }) =>
      DivTextRangeBorder(
        cornerRadius:
            cornerRadius != null ? cornerRadius.call() : this.cornerRadius,
        stroke: stroke != null ? stroke.call() : this.stroke,
      );

  static DivTextRangeBorder? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return DivTextRangeBorder(
        cornerRadius: safeParseIntExpr(
          json['corner_radius'],
        ),
        stroke: safeParseObject(
          json['stroke'],
          parse: DivStroke.fromJson,
        ),
      );
    } catch (e, st) {
      logger.warning("Parsing error", error: e, stackTrace: st);
      return null;
    }
  }
}
