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
  double convert({required double viewScale}) =>
      (value.value) * (unit.value).asPx * viewScale;
}

extension DivFixedSizeConverter on DivFixedSize {
  double convert({required double viewScale}) =>
      (value.value) * (unit.value).asPx * viewScale;
}

extension DivWrapContentSizeConstraintSizeConverter
    on DivWrapContentSizeConstraintSize {
  double convert({required double viewScale}) =>
      (value.value) * (unit.value).asPx * viewScale;
}

extension DivPointConverter on DivPoint {
  Offset convert({
    required double viewScale,
  }) =>
      Offset(
        x.convert(viewScale: viewScale),
        y.convert(viewScale: viewScale),
      );
}
