// Generated code. Do not modify.

import 'package:divkit/src/schema/div_fixed_size.dart';
import 'package:divkit/src/schema/div_stroke.dart';
import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:equatable/equatable.dart';

class DivCircleShape extends Preloadable with EquatableMixin {
  const DivCircleShape({
    this.backgroundColor,
    this.radius = const DivFixedSize(
      value: ValueExpression(
        10,
      ),
    ),
    this.stroke,
  });

  static const type = "circle";

  final Expression<Color>? backgroundColor;
  // default value: const DivFixedSize(value: ValueExpression(10,),)
  final DivFixedSize radius;

  final DivStroke? stroke;

  @override
  List<Object?> get props => [
        backgroundColor,
        radius,
        stroke,
      ];

  DivCircleShape copyWith({
    Expression<Color>? Function()? backgroundColor,
    DivFixedSize? radius,
    DivStroke? Function()? stroke,
  }) =>
      DivCircleShape(
        backgroundColor: backgroundColor != null
            ? backgroundColor.call()
            : this.backgroundColor,
        radius: radius ?? this.radius,
        stroke: stroke != null ? stroke.call() : this.stroke,
      );

  static DivCircleShape? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return DivCircleShape(
        backgroundColor: safeParseColorExpr(
          json['background_color'],
        ),
        radius: safeParseObj(
          DivFixedSize.fromJson(json['radius']),
          fallback: const DivFixedSize(
            value: ValueExpression(
              10,
            ),
          ),
        )!,
        stroke: safeParseObj(
          DivStroke.fromJson(json['stroke']),
        ),
      );
    } catch (e) {
      return null;
    }
  }

  static Future<DivCircleShape?> parse(
    Map<String, dynamic>? json,
  ) async {
    if (json == null) {
      return null;
    }
    try {
      return DivCircleShape(
        backgroundColor: await safeParseColorExprAsync(
          json['background_color'],
        ),
        radius: (await safeParseObjAsync(
          DivFixedSize.fromJson(json['radius']),
          fallback: const DivFixedSize(
            value: ValueExpression(
              10,
            ),
          ),
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
      await backgroundColor?.preload(context);
      await radius.preload(context);
      await stroke?.preload(context);
    } catch (e) {
      return;
    }
  }
}
