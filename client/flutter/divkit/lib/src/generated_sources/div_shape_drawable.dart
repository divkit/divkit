// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:divkit/src/generated_sources/div_shape.dart';
import 'package:divkit/src/generated_sources/div_stroke.dart';

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

  DivShapeDrawable copyWith({
    Expression<Color>? color,
    DivShape? shape,
    DivStroke? Function()? stroke,
  }) =>
      DivShapeDrawable(
        color: color ?? this.color,
        shape: shape ?? this.shape,
        stroke: stroke != null ? stroke.call() : this.stroke,
      );

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
