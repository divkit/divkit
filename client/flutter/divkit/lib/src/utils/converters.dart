import 'dart:math' as math;
import 'dart:ui';

import 'package:divkit/src/core/action/action.dart' as resolved_action;
import 'package:divkit/src/core/action/action_converter.dart';
import 'package:divkit/src/core/expression/expression.dart';
import 'package:divkit/src/core/expression/resolver.dart';
import 'package:divkit/src/core/protocol/div_variable.dart';
import 'package:divkit/src/generated_sources/generated_sources.dart';
import 'package:flutter/widgets.dart';

enum DivAxisAlignment {
  start,
  center,
  end;

  MainAxisAlignment get asMainAxis {
    switch (this) {
      case DivAxisAlignment.start:
        return MainAxisAlignment.start;
      case DivAxisAlignment.center:
        return MainAxisAlignment.center;
      case DivAxisAlignment.end:
        return MainAxisAlignment.end;
    }
  }

  CrossAxisAlignment get asCrossAxis {
    switch (this) {
      case DivAxisAlignment.start:
        return CrossAxisAlignment.start;
      case DivAxisAlignment.center:
        return CrossAxisAlignment.center;
      case DivAxisAlignment.end:
        return CrossAxisAlignment.end;
    }
  }

  T map<T>({
    required T Function() start,
    required T Function() center,
    required T Function() end,
  }) {
    switch (this) {
      case DivAxisAlignment.start:
        return start();
      case DivAxisAlignment.center:
        return center();
      case DivAxisAlignment.end:
        return end();
    }
  }
}

class DivAlignment {
  final DivAxisAlignment? vertical;
  final DivAxisAlignment? horizontal;

  const DivAlignment({
    this.vertical,
    this.horizontal,
  });

  AlignmentGeometry? get asAlignment {
    final safeVertical = vertical;
    final safeHorizontal = horizontal;
    if (safeVertical != null && safeHorizontal != null) {
      return safeVertical.map(
        start: () => safeHorizontal.map(
          start: () => Alignment.topLeft,
          center: () => Alignment.topCenter,
          end: () => Alignment.topRight,
        ),
        center: () => safeHorizontal.map(
          start: () => Alignment.centerLeft,
          center: () => Alignment.center,
          end: () => Alignment.centerRight,
        ),
        end: () => safeHorizontal.map(
          start: () => Alignment.bottomLeft,
          center: () => Alignment.bottomCenter,
          end: () => Alignment.bottomRight,
        ),
      );
    } else if (safeVertical != null) {
      return safeVertical.map(
        start: () => Alignment.topCenter,
        center: () => Alignment.center,
        end: () => Alignment.bottomCenter,
      );
    } else if (safeHorizontal != null) {
      return safeHorizontal.map(
        start: () => Alignment.centerLeft,
        center: () => Alignment.center,
        end: () => Alignment.centerRight,
      );
    }
    return null;
  }
}

class PassDivAlignment {
  final Expression<DivAlignmentVertical?>? vertical;
  final Expression<DivAlignmentHorizontal?>? horizontal;

  const PassDivAlignment(this.vertical, this.horizontal);

  Future<DivAlignment> resolveAlignment({
    required DivVariableContext context,
  }) async {
    final safeVertical = await vertical?.resolveValue(context: context);
    final safeHorizontal = await horizontal?.resolveValue(context: context);
    final mappedVertical = safeVertical?.map(
      top: () => DivAxisAlignment.start,
      center: () => DivAxisAlignment.center,
      bottom: () => DivAxisAlignment.end,
      // TODO: support baseline alignment in flutter
      baseline: () => null,
    );
    final mappedHorizontal = safeHorizontal?.map(
      center: () => DivAxisAlignment.center,
      start: () => DivAxisAlignment.start,
      end: () => DivAxisAlignment.end,
      // TODO: support RTL
      right: () => DivAxisAlignment.end,
      left: () => DivAxisAlignment.start,
    );
    return DivAlignment(
      horizontal: mappedHorizontal,
      vertical: mappedVertical,
    );
  }

  Future<AlignmentGeometry?> resolve({
    required DivVariableContext context,
  }) async {
    if (vertical != null && horizontal != null) {
      final vertical = await exprResolver.resolve(
        this.vertical!,
        context: context,
      );
      final horizontal = await exprResolver.resolve(
        this.horizontal!,
        context: context,
      );

      return vertical!.map(
        top: () => horizontal!.map(
          left: () => Alignment.topLeft,
          center: () => Alignment.topCenter,
          right: () => Alignment.topRight,
          start: () => AlignmentDirectional.topStart,
          end: () => AlignmentDirectional.topEnd,
        ),
        center: () => horizontal!.map(
          left: () => Alignment.centerLeft,
          center: () => Alignment.center,
          right: () => Alignment.centerRight,
          start: () => AlignmentDirectional.centerStart,
          end: () => AlignmentDirectional.centerEnd,
        ),
        bottom: () => horizontal!.map(
          left: () => Alignment.bottomLeft,
          center: () => Alignment.bottomCenter,
          right: () => Alignment.bottomRight,
          start: () => AlignmentDirectional.bottomStart,
          end: () => AlignmentDirectional.bottomEnd,
        ),
        // TODO: support baseline alignment in flutter
        baseline: () => horizontal!.map(
          left: () => Alignment.centerLeft,
          center: () => Alignment.center,
          right: () => Alignment.centerRight,
          start: () => AlignmentDirectional.centerStart,
          end: () => AlignmentDirectional.centerEnd,
        ),
      );
    } else if (vertical != null) {
      final vertical = await exprResolver.resolve(
        this.vertical!,
        context: context,
      );

      return vertical!.map(
        top: () => Alignment.topCenter,
        center: () => Alignment.center,
        bottom: () => Alignment.bottomCenter,
        // TODO: support baseline alignment in flutter
        baseline: () => Alignment.center,
      );
    } else if (horizontal != null) {
      final horizontal = await exprResolver.resolve(
        this.horizontal!,
        context: context,
      );

      return horizontal!.map(
        left: () => Alignment.centerLeft,
        center: () => Alignment.center,
        right: () => Alignment.centerRight,
        start: () => AlignmentDirectional.centerStart,
        end: () => AlignmentDirectional.centerEnd,
      );
    }

    return null;
  }

  Future<TextAlign?> resolveTextAlign({
    required DivVariableContext context,
  }) async {
    switch (await horizontal?.resolveValue(context: context)) {
      case DivAlignmentHorizontal.left:
        return TextAlign.left;
      case DivAlignmentHorizontal.center:
        return TextAlign.center;
      case DivAlignmentHorizontal.right:
        return TextAlign.right;
      case DivAlignmentHorizontal.start:
        return TextAlign.start;
      case DivAlignmentHorizontal.end:
        return TextAlign.end;
      case null:
        return null;
    }
  }
}

class PassDivTextAlignment {
  final Expression<DivAlignmentVertical> vertical;
  final Expression<DivAlignmentHorizontal> horizontal;

  const PassDivTextAlignment(this.vertical, this.horizontal);

  Future<TextAlign> resolveTextAlign({
    required DivVariableContext context,
  }) async {
    switch (await horizontal.resolveValue(context: context)) {
      case DivAlignmentHorizontal.left:
        return TextAlign.left;
      case DivAlignmentHorizontal.center:
        return TextAlign.center;
      case DivAlignmentHorizontal.right:
        return TextAlign.right;
      case DivAlignmentHorizontal.start:
        return TextAlign.start;
      case DivAlignmentHorizontal.end:
        return TextAlign.end;
    }
  }

  Future<TextAlignVertical> resolveTextAlignVertical({
    required DivVariableContext context,
  }) async {
    switch (await vertical.resolveValue(context: context)) {
      case DivAlignmentVertical.top:
        return TextAlignVertical.top;
      case DivAlignmentVertical.center:
        return TextAlignVertical.center;
      case DivAlignmentVertical.bottom:
        return TextAlignVertical.bottom;
      // TODO: support baseline alignment in flutter
      case DivAlignmentVertical.baseline:
        return TextAlignVertical.center;
    }
  }
}

class PassDivBackground {
  final Color? bgColor;
  final Gradient? bgGradient;
  final DecorationImage? bgImage;

  const PassDivBackground({this.bgColor, this.bgImage, this.bgGradient});

  static Future<PassDivBackground> resolve(
    List<DivBackground> backgrounds, {
    required DivVariableContext context,
  }) async {
    Color? bgColor;
    Gradient? bgGradient;
    DecorationImage? bgImage;

    // TODO: complicated DivBackground support
    for (var bg in backgrounds) {
      await bg.map(
        divImageBackground: (divImageBackground) async {
          bgImage = DecorationImage(
            image: NetworkImage(
              (await divImageBackground.imageUrl.resolveValue(context: context))
                  .toString(),
            ),
            alignment: await PassDivAlignment(
                  divImageBackground.contentAlignmentVertical,
                  divImageBackground.contentAlignmentHorizontal,
                ).resolve(context: context) ??
                Alignment.center,
            fit: (await divImageBackground.scale.resolveValue(context: context))
                .asBoxFit,
            opacity: await divImageBackground.alpha.resolveValue(
              context: context,
            ),
          );
        },
        divNinePatchBackground: (divNinePatchBackground) {
          throw UnimplementedError();
        },
        divRadialGradient: (divRadialGradient) async {
          bgGradient = RadialGradient(
            colors:
                (await divRadialGradient.colors.resolveValue(context: context))
                    .map((code) => Color(code.value))
                    .toList(),
            radius: await divRadialGradient.radius.map(
              divFixedSize: (divFixedSize) async =>
                  (await divFixedSize.value.resolveValue(context: context))
                      .toDouble(),
              divRadialGradientRelativeRadius:
                  (divRadialGradientRelativeRadius) async {
                throw UnimplementedError();
              },
            ),
          );
        },
        divLinearGradient: (divLinearGradient) async {
          final resolvedRadians =
              await divLinearGradient.angle.resolveValue(context: context) /
                  (-180 / math.pi);
          final angleAlignment = Alignment(
            math.cos(resolvedRadians),
            math.sin(resolvedRadians),
          );

          bgGradient = LinearGradient(
            begin: -angleAlignment,
            end: angleAlignment,
            colors: (await divLinearGradient.colors.resolveValue(
              context: context,
            ))
                .map((code) => Color(code.value))
                .toList(),
          );
        },
        divSolidBackground: (divSolidBackground) async {
          bgColor = Color(
            (await divSolidBackground.color.resolveValue(context: context))
                .value,
          );
        },
      );
    }
    return PassDivBackground(
      bgColor: bgColor,
      bgGradient: bgGradient,
      bgImage: bgImage,
    );
  }
}

class DivFilters {
  final double? blurRadius;
  final bool isRtl;

  const DivFilters({this.blurRadius, this.isRtl = false});

  static Future<DivFilters> resolve({
    required List<DivFilter> filters,
    required DivVariableContext context,
  }) async {
    double? blurRadius;
    bool isRtl = false;
    for (var el in filters) {
      await el.map(
        divBlur: (divBlur) async {
          blurRadius =
              (await divBlur.radius.resolveValue(context: context)).toDouble();
        },
        divFilterRtlMirror: (divFilterRtlMirror) {
          isRtl = true;
        },
      );
    }
    return DivFilters(
      blurRadius: blurRadius,
      isRtl: isRtl,
    );
  }
}

extension PassDivEdgeInsets on DivEdgeInsets {
  Future<EdgeInsetsGeometry> resolve({
    required DivVariableContext context,
  }) async {
    final safeStart = await start?.resolveValue(context: context);
    final safeEnd = await end?.resolveValue(context: context);
    final safeLeft = await left.resolveValue(context: context);
    final safeRight = await right.resolveValue(context: context);
    final safeTop = await top.resolveValue(context: context);
    final safeBottom = await bottom.resolveValue(context: context);

    if (safeStart != null || safeEnd != null) {
      return EdgeInsetsDirectional.fromSTEB(
        safeStart?.toDouble() ?? 0,
        safeTop.toDouble(),
        safeEnd?.toDouble() ?? 0,
        safeBottom.toDouble(),
      );
    }
    return EdgeInsets.fromLTRB(
      safeLeft.toDouble(),
      safeTop.toDouble(),
      safeRight.toDouble(),
      safeBottom.toDouble(),
    );
  }
}

extension DivSizeUnitMultiplier on DivSizeUnit {
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

extension PassDivVisibility on DivVisibility {
  bool get isGone => this == DivVisibility.gone;

  double? get asOpacity {
    switch (this) {
      case DivVisibility.visible:
        return 1;
      case DivVisibility.invisible:
        return 0;
      case DivVisibility.gone:
        return null;
    }
  }
}

extension PassDivImageScale on DivImageScale {
  BoxFit get asBoxFit {
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

extension PassDivDimension on DivDimension {
  Future<double> resolveDimension({
    required DivVariableContext context,
  }) async =>
      (await value.resolveValue(context: context)) *
      (await unit.resolveValue(context: context)).asPx;
}

extension PassDivFixedSize on DivFixedSize {
  Future<double> resolveDimension({
    required DivVariableContext context,
  }) async =>
      (await value.resolveValue(context: context)) *
      (await unit.resolveValue(context: context)).asPx;
}

extension PassDivWrapContentSizeConstraintSize
    on DivWrapContentSizeConstraintSize {
  Future<double> resolveDimension({
    required DivVariableContext context,
  }) async =>
      (await value.resolveValue(context: context)) *
      (await unit.resolveValue(context: context)).asPx;
}

extension DivPointAsOffset on DivPoint {
  Future<Offset> resolveOffset({
    required DivVariableContext context,
  }) async =>
      Offset(
        await x.resolveDimension(context: context),
        await y.resolveDimension(context: context),
      );
}

extension PassDivBorder on DivBorder {
  Future<BorderRadius> resolveBorderRadius({
    required DivVariableContext context,
  }) async {
    final singleCornerRadius =
        await cornerRadius?.resolveValue(context: context);
    final multipleCornerRadius = cornersRadius;

    if (multipleCornerRadius != null) {
      // If corners_radius of any corner is null — should use corner_radius
      // https://divkit.tech/en/doc/overview/concepts/divs/2/div-border.html
      final resolvedTopLeft =
          await multipleCornerRadius.topLeft?.resolveValue(context: context) ??
              singleCornerRadius ??
              0;
      final resolvedTopRight =
          await multipleCornerRadius.topRight?.resolveValue(context: context) ??
              singleCornerRadius ??
              0;
      final resolvedBottomLeft = await multipleCornerRadius.bottomLeft
              ?.resolveValue(context: context) ??
          singleCornerRadius ??
          0;
      final resolvedBottomRight = await multipleCornerRadius.bottomRight
              ?.resolveValue(context: context) ??
          singleCornerRadius ??
          0;
      return BorderRadius.only(
        topLeft: Radius.circular(resolvedTopLeft.toDouble()),
        topRight: Radius.circular(resolvedTopRight.toDouble()),
        bottomLeft: Radius.circular(resolvedBottomLeft.toDouble()),
        bottomRight: Radius.circular(resolvedBottomRight.toDouble()),
      );
    } else if (singleCornerRadius != null) {
      // corner_radius has lower priority than corners_radius
      return BorderRadius.circular(
        singleCornerRadius.toDouble(),
      );
    }
    return BorderRadius.zero;
  }

  Future<Border?> resolveBorder({
    required DivVariableContext context,
  }) async {
    final borderStroke = stroke;
    if (borderStroke != null) {
      return Border.all(
        width: ((await borderStroke.unit.resolveValue(context: context)).asPx) *
            (await borderStroke.width.resolveValue(context: context))
                .toDouble(),
        color: Color(
          (await borderStroke.color.resolveValue(context: context)).value,
        ),
      );
    }
    return null;
  }

  Future<List<BoxShadow>> resolveShadow({
    required DivVariableContext context,
  }) async {
    final borderShadow = shadow;
    if (borderShadow != null &&
        (await hasShadow.resolveValue(context: context))) {
      return [
        BoxShadow(
          color: Color(
            (await borderShadow.color.resolveValue(context: context)).value,
          ).withAlpha(
            (await borderShadow.alpha.resolveValue(context: context) * 255)
                .toInt(),
          ),
          blurRadius: (await borderShadow.blur.resolveValue(context: context))
              .toDouble(),
          offset: await borderShadow.offset.resolveOffset(context: context),
        ),
      ];
    }
    return [];
  }
}

extension PassDivFontWeight on Expression<DivFontWeight> {
  Future<FontWeight> resolve({
    required DivVariableContext context,
  }) async {
    switch (await exprResolver.resolve(this, context: context)) {
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

extension PassTextDecoration on DivLineStyle {
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

extension PassDivContainerOrientation on Expression<DivContainerOrientation> {
  Future<DivContainerOrientation> resolve({
    required DivVariableContext context,
  }) =>
      exprResolver.resolve(this, context: context);
}

extension PassDivAspect on DivAspect {
  Future<double> resolve({
    required DivVariableContext context,
  }) =>
      ratio.resolveValue(context: context);
}

extension PassDivBlendMode on DivBlendMode {
  BlendMode get asBlendMode {
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

extension PassActions on DivFocus {
  Future<List<resolved_action.DivAction>> resolveOnBlurActions({
    required DivVariableContext context,
  }) async {
    List<resolved_action.DivAction> result = [];
    final blurAction = onBlur;
    if (blurAction != null) {
      for (final action in blurAction) {
        final rAction = await action.resolve(context: context);
        if (rAction.enabled) {
          result.add(rAction);
        }
      }
    }
    return result;
  }

  Future<List<resolved_action.DivAction>> resolveOnFocusActions({
    required DivVariableContext context,
  }) async {
    List<resolved_action.DivAction> result = [];
    final blurAction = onFocus;
    if (blurAction != null) {
      for (final action in blurAction) {
        final rAction = await action.resolve(context: context);
        if (rAction.enabled) {
          result.add(rAction);
        }
      }
    }
    return result;
  }
}
