// Generated code. Do not modify.

import 'package:divkit/src/schema/div_corners_radius.dart';
import 'package:divkit/src/schema/div_shadow.dart';
import 'package:divkit/src/schema/div_stroke.dart';
import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:equatable/equatable.dart';

class DivBorder extends Preloadable with EquatableMixin {
  const DivBorder({
    this.cornerRadius,
    this.cornersRadius,
    this.hasShadow = const ValueExpression(false),
    this.shadow,
    this.stroke,
  });

  // constraint: number >= 0
  final Expression<int>? cornerRadius;

  final DivCornersRadius? cornersRadius;
  // default value: false
  final Expression<bool> hasShadow;

  final DivShadow? shadow;

  final DivStroke? stroke;

  @override
  List<Object?> get props => [
        cornerRadius,
        cornersRadius,
        hasShadow,
        shadow,
        stroke,
      ];

  DivBorder copyWith({
    Expression<int>? Function()? cornerRadius,
    DivCornersRadius? Function()? cornersRadius,
    Expression<bool>? hasShadow,
    DivShadow? Function()? shadow,
    DivStroke? Function()? stroke,
  }) =>
      DivBorder(
        cornerRadius:
            cornerRadius != null ? cornerRadius.call() : this.cornerRadius,
        cornersRadius:
            cornersRadius != null ? cornersRadius.call() : this.cornersRadius,
        hasShadow: hasShadow ?? this.hasShadow,
        shadow: shadow != null ? shadow.call() : this.shadow,
        stroke: stroke != null ? stroke.call() : this.stroke,
      );

  static DivBorder? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return DivBorder(
        cornerRadius: safeParseIntExpr(
          json['corner_radius'],
        ),
        cornersRadius: safeParseObj(
          DivCornersRadius.fromJson(json['corners_radius']),
        ),
        hasShadow: safeParseBoolExpr(
          json['has_shadow'],
          fallback: false,
        )!,
        shadow: safeParseObj(
          DivShadow.fromJson(json['shadow']),
        ),
        stroke: safeParseObj(
          DivStroke.fromJson(json['stroke']),
        ),
      );
    } catch (e) {
      return null;
    }
  }

  static Future<DivBorder?> parse(
    Map<String, dynamic>? json,
  ) async {
    if (json == null) {
      return null;
    }
    try {
      return DivBorder(
        cornerRadius: await safeParseIntExprAsync(
          json['corner_radius'],
        ),
        cornersRadius: await safeParseObjAsync(
          DivCornersRadius.fromJson(json['corners_radius']),
        ),
        hasShadow: (await safeParseBoolExprAsync(
          json['has_shadow'],
          fallback: false,
        ))!,
        shadow: await safeParseObjAsync(
          DivShadow.fromJson(json['shadow']),
        ),
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
      await cornerRadius?.preload(context);
      await cornersRadius?.preload(context);
      await hasShadow.preload(context);
      await shadow?.preload(context);
      await stroke?.preload(context);
    } catch (e) {
      return;
    }
  }
}
