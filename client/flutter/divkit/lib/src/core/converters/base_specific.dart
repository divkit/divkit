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
  DivDecoration resolveDecoration(
    DivVariableContext context, {
    required double viewScale,
  }) {
    final boxBorder = border.resolveBorder(
      context,
      viewScale: viewScale,
    );
    final borderRadius = border.resolveBorderRadius(
      context,
      viewScale: viewScale,
    );
    final outerShadow = border.resolveShadow(
      context,
      viewScale: viewScale,
    );
    final backgrounds = background;
    if (backgrounds != null) {
      final backgroundWidgets = resolveBackgrounds(
        context,
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

  DivDecoration resolveFocusDecoration(
    DivVariableContext context, {
    required double viewScale,
  }) {
    final focusBg = focus?.background;
    final focusBorder = focus?.border;
    final resolvedFocusBorder = focusBorder?.resolveBorder(
      context,
      viewScale: viewScale,
    );
    final focusRadius = focusBorder?.resolveBorderRadius(
      context,
      viewScale: viewScale,
    );
    if (focusBg != null) {
      final backgroundWidgets = resolveBackgrounds(
        context,
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

  double resolveOpacity(DivVariableContext context) {
    final visibility = this.visibility.resolve(context);
    if (!visibility.isVisible) {
      return 0;
    }
    return alpha.resolve(context);
  }

  DivSizeValue resolveWidth(
    DivVariableContext context, {
    required double viewScale,
  }) =>
      width.resolve(context, viewScale: viewScale);

  DivSizeValue resolveHeight(
    DivVariableContext context, {
    required double viewScale,
  }) =>
      height.resolve(context, viewScale: viewScale);
}

extension DivBorderConverter on DivBorder {
  CustomBorderRadius resolveBorderRadius(
    DivVariableContext context, {
    required double viewScale,
  }) {
    final singleCornerRadius = cornerRadius?.resolve(context);

    final multipleCornerRadius = cornersRadius;

    if (multipleCornerRadius != null) {
      // If corners_radius of any corner is null — should use corner_radius
      // https://divkit.tech/en/doc/overview/concepts/divs/2/div-border.html
      final resolvedTopLeft = multipleCornerRadius.topLeft?.resolve(context) ??
          singleCornerRadius ??
          0 * viewScale;
      final resolvedTopRight =
          multipleCornerRadius.topRight?.resolve(context) ??
              singleCornerRadius ??
              0 * viewScale;
      final resolvedBottomLeft =
          multipleCornerRadius.bottomLeft?.resolve(context) ??
              singleCornerRadius ??
              0 * viewScale;
      final resolvedBottomRight =
          multipleCornerRadius.bottomRight?.resolve(context) ??
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

  Border? resolveBorder(
    DivVariableContext context, {
    required double viewScale,
  }) {
    final borderStroke = stroke;
    if (borderStroke != null) {
      return Border.all(
        width: ((borderStroke.unit.resolve(context)).asPx) *
            (borderStroke.width.resolve(context)).toDouble() *
            viewScale,
        color: borderStroke.color.resolve(context),
      );
    }
    return null;
  }

  BoxShadow? resolveShadow(
    DivVariableContext context, {
    required double viewScale,
  }) {
    final borderShadow = shadow;
    if (borderShadow != null && hasShadow.resolve(context)) {
      return BoxShadow(
        color: borderShadow.color.resolve(context).withAlpha(
              (borderShadow.alpha.resolve(context) * 255).toInt(),
            ),
        blurRadius: borderShadow.blur.resolve(context).toDouble() * viewScale,
        offset: borderShadow.offset.resolve(context, viewScale: viewScale),
      );
    }
    return null;
  }
}

List<Widget> resolveBackgrounds(
  DivVariableContext context,
  List<DivBackground> backgrounds, {
  required double viewScale,
}) {
  final backgroundWidgets = <Widget>[];
  // TODO: complicated DivBackground support
  for (var bg in backgrounds) {
    try {
      bg.map(
        divImageBackground: (divImageBackground) {
          final filter = DivFilters.combine(
            context,
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
                      divImageBackground.imageUrl.resolve(context).toString(),
                    ),
                    alignment: DivAlignmentConverter(
                          divImageBackground.contentAlignmentVertical,
                          divImageBackground.contentAlignmentHorizontal,
                        ).resolve(context) ??
                        Alignment.center,
                    fit: divImageBackground.scale.resolve(context).convert(),
                    opacity: divImageBackground.alpha.resolve(context),
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
          final insetsLeft =
              divNinePatchBackground.insets.left.resolve(context);
          final insetsTop = divNinePatchBackground.insets.top.resolve(context);
          final insetsRight =
              divNinePatchBackground.insets.right.resolve(context);
          final insetsBottom =
              divNinePatchBackground.insets.bottom.resolve(context);
          backgroundWidgets.add(
            Container(
              width: double.maxFinite,
              height: double.maxFinite,
              decoration: BoxDecoration(
                image: DecorationImage(
                  image: NetworkImage(
                    divNinePatchBackground.imageUrl.resolve(context).toString(),
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
                  colors: divRadialGradient.colors.resolve(context),
                  radius: divRadialGradient.radius.map(
                    divFixedSize: (divFixedSize) =>
                        divFixedSize.value.resolve(context).toDouble() *
                        viewScale,
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
          final resolvedRadians = divLinearGradient.angle.resolve(context) *
              viewScale /
              (-180 / pi);
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
                  colors: divLinearGradient.colors.resolve(context).toList(),
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
                color: divSolidBackground.color.resolve(context),
              ),
            ),
          );
        },
      );
    } catch (e, st) {
      logger.warning('Not correct background', error: e, stackTrace: st);
    }
  }
  return backgroundWidgets;
}
