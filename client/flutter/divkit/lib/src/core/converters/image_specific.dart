import 'package:divkit/divkit.dart';
import 'package:flutter/widgets.dart';

extension DivImageScaleConverter on DivImageScale {
  BoxFit convert() {
    switch (this) {
      case DivImageScale.fill:
        return BoxFit.cover;
      case DivImageScale.noScale:
        return BoxFit.none;
      case DivImageScale.fit:
        return BoxFit.contain;
      case DivImageScale.stretch:
        return BoxFit.fill;
    }
  }
}

extension PassDivBlendConverter on DivBlendMode {
  BlendMode convert() {
    switch (this) {
      case DivBlendMode.sourceIn:
        return BlendMode.srcIn;
      case DivBlendMode.sourceAtop:
        return BlendMode.srcATop;
      case DivBlendMode.darken:
        return BlendMode.darken;
      case DivBlendMode.lighten:
        return BlendMode.lighten;
      case DivBlendMode.multiply:
        return BlendMode.multiply;
      case DivBlendMode.screen:
        return BlendMode.screen;
    }
  }
}
