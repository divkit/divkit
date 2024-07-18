// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:divkit/src/generated_sources/div_stroke.dart';

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

  DivTextRangeBorder copyWith({
    Expression<int>? Function()? cornerRadius,
    DivStroke? Function()? stroke,
  }) =>
      DivTextRangeBorder(
        cornerRadius:
            cornerRadius != null ? cornerRadius.call() : this.cornerRadius,
        stroke: stroke != null ? stroke.call() : this.stroke,
      );

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
