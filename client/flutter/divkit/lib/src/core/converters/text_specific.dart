import 'package:divkit/divkit.dart';
import 'package:divkit/src/core/converters/converters.dart';
import 'package:flutter/material.dart';

extension DivFontWeightConverter on DivFontWeight {
  FontWeight convert() {
    switch (this) {
      case DivFontWeight.light:
        return FontWeight.w200;
      case DivFontWeight.medium:
        return FontWeight.w500;
      case DivFontWeight.regular:
        return FontWeight.w400;
      case DivFontWeight.bold:
        return FontWeight.w700;
    }
  }
}

extension TextDecorationConverter on DivLineStyle {
  TextDecoration get asLineThrough {
    switch (this) {
      case DivLineStyle.none:
        return TextDecoration.none;
      case DivLineStyle.single:
        return TextDecoration.lineThrough;
    }
  }

  TextDecoration get asUnderline {
    switch (this) {
      case DivLineStyle.none:
        return TextDecoration.none;
      case DivLineStyle.single:
        return TextDecoration.underline;
    }
  }
}

extension DivShadowConverter on DivShadow {
  BoxShadow? resolveBoxShadow(
    DivVariableContext context, {
    required double viewScale,
  }) =>
      BoxShadow(
        color: color.resolve(context).withOpacity(alpha.resolve(context)),
        blurRadius: blur.resolve(context).toDouble() * viewScale,
        offset: offset.resolve(context, viewScale: viewScale),
      );

  Shadow? resolveShadow(
    DivVariableContext context, {
    required double viewScale,
  }) =>
      Shadow(
        color: color.resolve(context).withOpacity(alpha.resolve(context)),
        blurRadius: blur.resolve(context).toDouble() * viewScale,
        offset: offset.resolve(context, viewScale: viewScale),
      );
}
