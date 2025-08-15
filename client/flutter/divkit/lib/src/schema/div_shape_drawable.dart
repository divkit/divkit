// Generated code. Do not modify.

import 'package:divkit/src/schema/div_shape.dart';
import 'package:divkit/src/schema/div_stroke.dart';
import 'package:divkit/src/utils/parsing.dart';
import 'package:equatable/equatable.dart';

/// Drawable of a simple geometric shape.
class DivShapeDrawable with EquatableMixin {
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
        color: reqVProp<Color>(
          safeParseColorExpr(
            json['color'],
          ),
          name: 'color',
        ),
        shape: reqProp<DivShape>(
          safeParseObject(
            json['shape'],
            parse: DivShape.fromJson,
          ),
          name: 'shape',
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
