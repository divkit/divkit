import 'dart:math';
import 'dart:ui';

import 'package:divkit/divkit.dart';
import 'package:divkit/src/core/converters/alignment.dart';
import 'package:divkit/src/core/converters/converters.dart';
import 'package:divkit/src/core/converters/decoration.dart';
import 'package:divkit/src/core/converters/filters_converter.dart';
import 'package:divkit/src/core/converters/image_specific.dart';
import 'package:divkit/src/core/converters/size.dart';
import 'package:flutter/widgets.dart';

extension DivBaseConverter on DivBase {
  DivDecoration convertDecoration({
    required double viewScale,
  }) {
    final boxBorder = border.convertBorder(
      viewScale: viewScale,
    );
    final borderRadius = border.convertBorderRadius(
      viewScale: viewScale,
    );
    final outerShadow = border.convertShadow(
      viewScale: viewScale,
    );
    final backgrounds = background;
    if (backgrounds != null) {
      final backgroundWidgets = DivBackgroundConverter.convert(
        backgrounds,
        viewScale: viewScale,
      );
      return DivDecoration(
        boxDecoration: BoxDecoration(
          border: boxBorder,
        ),
        outerShadow: outerShadow,
        customBorderRadius: borderRadius,
        backgroundWidgets: backgroundWidgets,
      );
    }
    return DivDecoration(
      outerShadow: outerShadow,
      customBorderRadius: borderRadius,
      boxDecoration: BoxDecoration(
        border: boxBorder,
      ),
    );
  }

  DivDecoration convertFocusDecoration({
    required double viewScale,
  }) {
    final focusBg = focus?.background;
    final focusBorder = focus?.border;
    final resolvedFocusBorder = focusBorder?.convertBorder(
      viewScale: viewScale,
    );
    final focusRadius = focusBorder?.convertBorderRadius(
      viewScale: viewScale,
    );
    if (focusBg != null) {
      final backgroundWidgets = DivBackgroundConverter.convert(
        focusBg,
        viewScale: viewScale,
      );
      return DivDecoration(
        boxDecoration: BoxDecoration(
          border: resolvedFocusBorder,
        ),
        customBorderRadius: focusRadius ?? CustomBorderRadius(),
        backgroundWidgets: backgroundWidgets,
      );
    } else {
      return DivDecoration(
        customBorderRadius: focusRadius ?? CustomBorderRadius(),
        boxDecoration: BoxDecoration(
          border: resolvedFocusBorder,
        ),
      );
    }
  }

  double convertOpacity() {
    final visibility = this.visibility.value;
    if (!visibility.isVisible) {
      return 0;
    }
    return alpha.value;
  }

  DivSizeValue valueWidth({
    required double viewScale,
  }) =>
      width.convert(viewScale: viewScale);

  DivSizeValue valueHeight({
    required double viewScale,
  }) =>
      height.convert(viewScale: viewScale);
}

extension DivBorderConverter on DivBorder {
  CustomBorderRadius convertBorderRadius({
    required double viewScale,
  }) {
    final singleCornerRadius = cornerRadius?.value;

    final multipleCornerRadius = cornersRadius;

    if (multipleCornerRadius != null) {
      // If corners_radius of any corner is null — should use corner_radius
      // https://divkit.tech/en/doc/overview/concepts/divs/2/div-border.html
      final resolvedTopLeft = multipleCornerRadius.topLeft?.value ??
          singleCornerRadius ??
          0 * viewScale;
      final resolvedTopRight = multipleCornerRadius.topRight?.value ??
          singleCornerRadius ??
          0 * viewScale;
      final resolvedBottomLeft = multipleCornerRadius.bottomLeft?.value ??
          singleCornerRadius ??
          0 * viewScale;
      final resolvedBottomRight = multipleCornerRadius.bottomRight?.value ??
          singleCornerRadius ??
          0 * viewScale;
      return CustomBorderRadius(
        topLeft: Radius.circular(resolvedTopLeft.toDouble()),
        topRight: Radius.circular(resolvedTopRight.toDouble()),
        bottomLeft: Radius.circular(resolvedBottomLeft.toDouble()),
        bottomRight: Radius.circular(resolvedBottomRight.toDouble()),
      );
    } else if (singleCornerRadius != null) {
      // corner_radius has lower priority than corners_radius
      return CustomBorderRadius(
        topLeft: Radius.circular(singleCornerRadius.toDouble() * viewScale),
        topRight: Radius.circular(singleCornerRadius.toDouble() * viewScale),
        bottomLeft: Radius.circular(singleCornerRadius.toDouble() * viewScale),
        bottomRight: Radius.circular(singleCornerRadius.toDouble() * viewScale),
      );
    }
    return CustomBorderRadius(
      topLeft: Radius.zero,
      topRight: Radius.zero,
      bottomLeft: Radius.zero,
      bottomRight: Radius.zero,
    );
  }

  Border? convertBorder({
    required double viewScale,
  }) {
    final borderStroke = stroke;
    if (borderStroke != null) {
      return Border.all(
        width: ((borderStroke.unit.value).asPx) *
            (borderStroke.width.value).toDouble() *
            viewScale,
        color: Color(
          (borderStroke.color.value).value,
        ),
      );
    }
    return null;
  }

  BoxShadow? convertShadow({
    required double viewScale,
  }) {
    final borderShadow = shadow;
    if (borderShadow != null && hasShadow.value) {
      return BoxShadow(
        color: Color(
          borderShadow.color.value.value,
        ).withAlpha(
          (borderShadow.alpha.value * 255).toInt(),
        ),
        blurRadius: borderShadow.blur.value.toDouble() * viewScale,
        offset: borderShadow.offset.convert(viewScale: viewScale),
      );
    }
    return null;
  }
}

class DivBackgroundConverter {
  final List<Widget> backgroundWidgets;

  const DivBackgroundConverter({
    required this.backgroundWidgets,
  });

  static List<Widget> convert(
    List<DivBackground> backgrounds, {
    required double viewScale,
  }) {
    final backgroundWidgets = <Widget>[];
    // TODO: complicated DivBackground support
    for (var bg in backgrounds) {
      bg.map(
        divImageBackground: (divImageBackground) {
          final filter = DivFilters.combine(
            filters: divImageBackground.filters ?? [],
            viewScale: viewScale,
          );
          backgroundWidgets.add(
            Transform(
              alignment: Alignment.center,
              transform: Matrix4.rotationY(filter.isRtl ? pi : 0),
              child: Container(
                width: double.maxFinite,
                height: double.maxFinite,
                decoration: BoxDecoration(
                  image: DecorationImage(
                    image: NetworkImage(
                      divImageBackground.imageUrl.value.toString(),
                    ),
                    alignment: DivAlignmentConverter(
                          divImageBackground.contentAlignmentVertical,
                          divImageBackground.contentAlignmentHorizontal,
                        ).convert() ??
                        Alignment.center,
                    fit: divImageBackground.scale.value.convert(),
                    opacity: divImageBackground.alpha.value,
                  ),
                ),
                child: BackdropFilter(
                  filter: ImageFilter.blur(
                    sigmaX: filter.blurRadius ?? 0,
                    sigmaY: filter.blurRadius ?? 0,
                  ),
                  child: Container(
                    decoration: const BoxDecoration(
                      color: Color(0x00ffffff),
                    ),
                  ),
                ),
              ),
            ),
          );
        },
        divNinePatchBackground: (divNinePatchBackground) {
          final insetsLeft = divNinePatchBackground.insets.left.value;
          final insetsTop = divNinePatchBackground.insets.top.value;
          final insetsRight = divNinePatchBackground.insets.right.value;
          final insetsBottom = divNinePatchBackground.insets.bottom.value;
          backgroundWidgets.add(
            Container(
              width: double.maxFinite,
              height: double.maxFinite,
              decoration: BoxDecoration(
                image: DecorationImage(
                  image: NetworkImage(
                    divNinePatchBackground.imageUrl.value.toString(),
                  ),
                  centerSlice: Rect.fromLTRB(
                    insetsLeft.toDouble(),
                    insetsTop.toDouble(),
                    insetsRight.toDouble(),
                    insetsBottom.toDouble(),
                  ),
                ),
              ),
            ),
          );
        },
        divRadialGradient: (divRadialGradient) {
          backgroundWidgets.add(
            Container(
              width: double.maxFinite,
              height: double.maxFinite,
              decoration: BoxDecoration(
                gradient: RadialGradient(
                  colors: divRadialGradient.colors.value
                      .map((code) => Color(code.value))
                      .toList(),
                  radius: divRadialGradient.radius.map(
                    divFixedSize: (divFixedSize) =>
                        divFixedSize.value.value.toDouble() * viewScale,
                    divRadialGradientRelativeRadius:
                        (divRadialGradientRelativeRadius) {
                      throw UnimplementedError();
                    },
                  ),
                ),
              ),
            ),
          );
        },
        divLinearGradient: (divLinearGradient) {
          final resolvedRadians =
              divLinearGradient.angle.value * viewScale / (-180 / pi);
          final angleAlignment = Alignment(
            cos(resolvedRadians),
            sin(resolvedRadians),
          );
          backgroundWidgets.add(
            Container(
              width: double.maxFinite,
              height: double.maxFinite,
              decoration: BoxDecoration(
                gradient: LinearGradient(
                  begin: -angleAlignment,
                  end: angleAlignment,
                  colors: divLinearGradient.colors.value
                      .map((code) => Color(code.value))
                      .toList(),
                ),
              ),
            ),
          );
        },
        divSolidBackground: (divSolidBackground) {
          backgroundWidgets.add(
            Container(
              width: double.maxFinite,
              height: double.maxFinite,
              decoration: BoxDecoration(
                color: Color(
                  divSolidBackground.color.value.value,
                ),
              ),
            ),
          );
        },
      );
    }
    return backgroundWidgets;
  }
}
