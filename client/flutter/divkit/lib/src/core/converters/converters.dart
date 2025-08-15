import 'dart:ui';

import 'package:divkit/divkit.dart';

extension DivSizeUnitConverter on DivSizeUnit {
  double get asPx {
    switch (this) {
      case DivSizeUnit.dp:
        return 1.0;
      case DivSizeUnit.sp:
        // Since we still support a wide range of Flutter versions, we cannot migrate to the new View API yet.
        // ignore: deprecated_member_use
        return window.textScaleFactor;
      case DivSizeUnit.px:
        // Since we still support a wide range of Flutter versions, we cannot migrate to the new View API yet.
        // ignore: deprecated_member_use
        return 1 / window.devicePixelRatio;
    }
  }
}

extension DivDimensionConverter on DivDimension {
  double resolve(
    DivVariableContext context, {
    required double viewScale,
  }) =>
      (value.resolve(context)) * (unit.resolve(context)).asPx * viewScale;
}

extension DivFixedSizeConverter on DivFixedSize {
  double resolve(
    DivVariableContext context, {
    required double viewScale,
  }) =>
      (value.resolve(context)) * (unit.resolve(context)).asPx * viewScale;
}

extension DivWrapContentSizeConstraintSizeConverter
    on DivWrapContentSizeConstraintSize {
  double resolve(
    DivVariableContext context, {
    required double viewScale,
  }) =>
      (value.resolve(context)) * (unit.resolve(context)).asPx * viewScale;
}

extension DivPointConverter on DivPoint {
  Offset resolve(
    DivVariableContext context, {
    required double viewScale,
  }) =>
      Offset(
        x.resolve(context, viewScale: viewScale),
        y.resolve(context, viewScale: viewScale),
      );
}
