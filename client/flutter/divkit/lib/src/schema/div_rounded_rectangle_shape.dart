// Generated code. Do not modify.

import 'package:divkit/src/schema/div_fixed_size.dart';
import 'package:divkit/src/schema/div_stroke.dart';
import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:equatable/equatable.dart';

/// A rectangle with rounded corners.
class DivRoundedRectangleShape extends Resolvable with EquatableMixin {
  const DivRoundedRectangleShape({
    this.backgroundColor,
    this.cornerRadius = const DivFixedSize(
      value: ValueExpression(
        5,
      ),
    ),
    this.itemHeight = const DivFixedSize(
      value: ValueExpression(
        10,
      ),
    ),
    this.itemWidth = const DivFixedSize(
      value: ValueExpression(
        10,
      ),
    ),
    this.stroke,
  });

  static const type = "rounded_rectangle";

  /// Fill color.
  final Expression<Color>? backgroundColor;

  /// Corner rounding radius.
  // default value: const DivFixedSize(value: ValueExpression(5,),)
  final DivFixedSize cornerRadius;

  /// Height.
  // default value: const DivFixedSize(value: ValueExpression(10,),)
  final DivFixedSize itemHeight;

  /// Width.
  // default value: const DivFixedSize(value: ValueExpression(10,),)
  final DivFixedSize itemWidth;

  /// Stroke style.
  final DivStroke? stroke;

  @override
  List<Object?> get props => [
        backgroundColor,
        cornerRadius,
        itemHeight,
        itemWidth,
        stroke,
      ];

  DivRoundedRectangleShape copyWith({
    Expression<Color>? Function()? backgroundColor,
    DivFixedSize? cornerRadius,
    DivFixedSize? itemHeight,
    DivFixedSize? itemWidth,
    DivStroke? Function()? stroke,
  }) =>
      DivRoundedRectangleShape(
        backgroundColor: backgroundColor != null
            ? backgroundColor.call()
            : this.backgroundColor,
        cornerRadius: cornerRadius ?? this.cornerRadius,
        itemHeight: itemHeight ?? this.itemHeight,
        itemWidth: itemWidth ?? this.itemWidth,
        stroke: stroke != null ? stroke.call() : this.stroke,
      );

  static DivRoundedRectangleShape? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return DivRoundedRectangleShape(
        backgroundColor: safeParseColorExpr(
          json['background_color'],
        ),
        cornerRadius: safeParseObj(
          DivFixedSize.fromJson(json['corner_radius']),
          fallback: const DivFixedSize(
            value: ValueExpression(
              5,
            ),
          ),
        )!,
        itemHeight: safeParseObj(
          DivFixedSize.fromJson(json['item_height']),
          fallback: const DivFixedSize(
            value: ValueExpression(
              10,
            ),
          ),
        )!,
        itemWidth: safeParseObj(
          DivFixedSize.fromJson(json['item_width']),
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

  @override
  DivRoundedRectangleShape resolve(DivVariableContext context) {
    backgroundColor?.resolve(context);
    cornerRadius.resolve(context);
    itemHeight.resolve(context);
    itemWidth.resolve(context);
    stroke?.resolve(context);
    return this;
  }
}
