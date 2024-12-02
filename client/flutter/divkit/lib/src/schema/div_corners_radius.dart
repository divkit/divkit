// Generated code. Do not modify.

import 'package:divkit/src/utils/parsing.dart';
import 'package:equatable/equatable.dart';

/// Sets corner rounding.
class DivCornersRadius extends Resolvable with EquatableMixin {
  const DivCornersRadius({
    this.bottomLeft,
    this.bottomRight,
    this.topLeft,
    this.topRight,
  });

  /// Rounding radius of a lower left corner. If not specified, then `corner_radius` is used.
  // constraint: number >= 0
  final Expression<int>? bottomLeft;

  /// Rounding radius of a lower right corner. If not specified, then `corner_radius` is used.
  // constraint: number >= 0
  final Expression<int>? bottomRight;

  /// Rounding radius of an upper left corner. If not specified, then `corner_radius` is used.
  // constraint: number >= 0
  final Expression<int>? topLeft;

  /// Rounding radius of an upper right corner. If not specified, then `corner_radius` is used.
  // constraint: number >= 0
  final Expression<int>? topRight;

  @override
  List<Object?> get props => [
        bottomLeft,
        bottomRight,
        topLeft,
        topRight,
      ];

  DivCornersRadius copyWith({
    Expression<int>? Function()? bottomLeft,
    Expression<int>? Function()? bottomRight,
    Expression<int>? Function()? topLeft,
    Expression<int>? Function()? topRight,
  }) =>
      DivCornersRadius(
        bottomLeft: bottomLeft != null ? bottomLeft.call() : this.bottomLeft,
        bottomRight:
            bottomRight != null ? bottomRight.call() : this.bottomRight,
        topLeft: topLeft != null ? topLeft.call() : this.topLeft,
        topRight: topRight != null ? topRight.call() : this.topRight,
      );

  static DivCornersRadius? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return DivCornersRadius(
        bottomLeft: safeParseIntExpr(
          json['bottom-left'],
        ),
        bottomRight: safeParseIntExpr(
          json['bottom-right'],
        ),
        topLeft: safeParseIntExpr(
          json['top-left'],
        ),
        topRight: safeParseIntExpr(
          json['top-right'],
        ),
      );
    } catch (e, st) {
      logger.warning("Parsing error", error: e, stackTrace: st);
      return null;
    }
  }

  @override
  DivCornersRadius resolve(DivVariableContext context) {
    bottomLeft?.resolve(context);
    bottomRight?.resolve(context);
    topLeft?.resolve(context);
    topRight?.resolve(context);
    return this;
  }
}
