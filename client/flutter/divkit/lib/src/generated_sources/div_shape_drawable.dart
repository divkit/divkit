// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import '../utils/parsing_utils.dart';
import 'div_shape.dart';
import 'div_stroke.dart';

class DivShapeDrawable with EquatableMixin {
  const DivShapeDrawable({
    required this.color,
    required this.shape,
    this.stroke,
  });

  static const type = "shape_drawable";

  final Expression<Color> color;

  final DivShape shape;

  final DivStroke? stroke;

  @override
  List<Object?> get props => [
        color,
        shape,
        stroke,
      ];

  static DivShapeDrawable? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    return DivShapeDrawable(
      color: safeParseColorExpr(
        json['color'],
      )!,
      shape: safeParseObj(
        DivShape.fromJson(json['shape']),
      )!,
      stroke: safeParseObj(
        DivStroke.fromJson(json['stroke']),
      ),
    );
  }
}
