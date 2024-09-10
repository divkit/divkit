// Generated code. Do not modify.

import 'package:divkit/src/schema/div_shape.dart';
import 'package:divkit/src/schema/div_stroke.dart';
import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:equatable/equatable.dart';

/// Drawable of a simple geometric shape.
class DivShapeDrawable extends Preloadable with EquatableMixin {
  const DivShapeDrawable({
    required this.color,
    required this.shape,
    this.stroke,
  });

  static const type = "shape_drawable";

  /// Fill color.
  final Expression<Color> color;

  /// Shape.
  final DivShape shape;

  /// Stroke style.
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

  static DivShapeDrawable? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
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
    } catch (e) {
      return null;
    }
  }

  static Future<DivShapeDrawable?> parse(
    Map<String, dynamic>? json,
  ) async {
    if (json == null) {
      return null;
    }
    try {
      return DivShapeDrawable(
        color: (await safeParseColorExprAsync(
          json['color'],
        ))!,
        shape: (await safeParseObjAsync(
          DivShape.fromJson(json['shape']),
        ))!,
        stroke: await safeParseObjAsync(
          DivStroke.fromJson(json['stroke']),
        ),
      );
    } catch (e) {
      return null;
    }
  }

  @override
  Future<void> preload(
    Map<String, dynamic> context,
  ) async {
    try {
      await color.preload(context);
      await shape.preload(context);
      await stroke?.preload(context);
    } catch (e) {
      return;
    }
  }
}
