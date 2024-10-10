// Generated code. Do not modify.

import 'package:divkit/src/schema/div_edge_insets.dart';
import 'package:divkit/src/schema/div_fixed_size.dart';
import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:equatable/equatable.dart';

/// Cloud text background. Lines draws a rectangular background with the specified color and rounded corners.
class DivCloudBackground extends Preloadable with EquatableMixin {
  const DivCloudBackground({
    required this.color,
    required this.cornerRadius,
    this.padding = const DivEdgeInsets(),
  });

  static const type = "cloud";

  /// Fill color.
  final Expression<Color> color;

  /// Corner rounding radius.
  final DivFixedSize cornerRadius;

  /// Margins between line bounds and background.
  final DivEdgeInsets padding;

  @override
  List<Object?> get props => [
        color,
        cornerRadius,
        padding,
      ];

  DivCloudBackground copyWith({
    Expression<Color>? color,
    DivFixedSize? cornerRadius,
    DivEdgeInsets? padding,
  }) =>
      DivCloudBackground(
        color: color ?? this.color,
        cornerRadius: cornerRadius ?? this.cornerRadius,
        padding: padding ?? this.padding,
      );

  static DivCloudBackground? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return DivCloudBackground(
        color: safeParseColorExpr(
          json['color'],
        )!,
        cornerRadius: safeParseObj(
          DivFixedSize.fromJson(json['corner_radius']),
        )!,
        padding: safeParseObj(
          DivEdgeInsets.fromJson(json['padding']),
          fallback: const DivEdgeInsets(),
        )!,
      );
    } catch (e) {
      return null;
    }
  }

  static Future<DivCloudBackground?> parse(
    Map<String, dynamic>? json,
  ) async {
    if (json == null) {
      return null;
    }
    try {
      return DivCloudBackground(
        color: (await safeParseColorExprAsync(
          json['color'],
        ))!,
        cornerRadius: (await safeParseObjAsync(
          DivFixedSize.fromJson(json['corner_radius']),
        ))!,
        padding: (await safeParseObjAsync(
          DivEdgeInsets.fromJson(json['padding']),
          fallback: const DivEdgeInsets(),
        ))!,
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
      await cornerRadius.preload(context);
      await padding.preload(context);
    } catch (e) {
      return;
    }
  }
}
