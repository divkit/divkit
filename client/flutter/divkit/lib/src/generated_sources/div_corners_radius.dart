// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import '../utils/parsing_utils.dart';

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
