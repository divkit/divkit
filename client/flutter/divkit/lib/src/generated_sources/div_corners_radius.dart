// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import 'package:divkit/src/utils/parsing_utils.dart';

class DivCornersRadius with EquatableMixin {
  const DivCornersRadius({
    this.bottomLeft,
    this.bottomRight,
    this.topLeft,
    this.topRight,
  });

  // constraint: number >= 0
  final Expression<int>? bottomLeft;
  // constraint: number >= 0
  final Expression<int>? bottomRight;
  // constraint: number >= 0
  final Expression<int>? topLeft;
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

  static DivCornersRadius? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
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
  }
}
