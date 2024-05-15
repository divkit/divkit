// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import '../utils/parsing_utils.dart';
import 'div_stroke.dart';

class DivTextRangeBorder with EquatableMixin {
  const DivTextRangeBorder({
    this.cornerRadius,
    this.stroke,
  });

  // constraint: number >= 0
  final Expression<int>? cornerRadius;

  final DivStroke? stroke;

  @override
  List<Object?> get props => [
        cornerRadius,
        stroke,
      ];

  static DivTextRangeBorder? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    return DivTextRangeBorder(
      cornerRadius: safeParseIntExpr(
        json['corner_radius'],
      ),
      stroke: safeParseObj(
        DivStroke.fromJson(json['stroke']),
      ),
    );
  }
}
