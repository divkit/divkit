import 'dart:math' as math;
import 'dart:ui';

import 'package:divkit/divkit.dart';
import 'package:equatable/equatable.dart';
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

class DivAlignment with EquatableMixin {
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
        start: () => Alignment.topLeft,
        center: () => Alignment.centerLeft,
        end: () => Alignment.bottomLeft,
      );
    } else if (safeHorizontal != null) {
      return safeHorizontal.map(
        start: () => Alignment.topLeft,
        center: () => Alignment.topCenter,
        end: () => Alignment.topRight,
      );
    }
    return null;
  }

  @override
  List<Object?> get props => [vertical, horizontal];
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

  DivAlignment valueAlignment() {
    final safeVertical = vertical?.value;
    final safeHorizontal = horizontal?.value;
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

  AlignmentGeometry? valueAlignmentGeometry() {
    final vertical = this.vertical?.value;
    final horizontal = this.horizontal?.value;

    if (vertical != null && horizontal != null) {
      return vertical.map(
        top: () => horizontal.map(
          left: () => Alignment.topLeft,
          center: () => Alignment.topCenter,
          right: () => Alignment.topRight,
          start: () => AlignmentDirectional.topStart,
          end: () => AlignmentDirectional.topEnd,
        ),
        center: () => horizontal.map(
          left: () => Alignment.centerLeft,
          center: () => Alignment.center,
          right: () => Alignment.centerRight,
          start: () => AlignmentDirectional.centerStart,
          end: () => AlignmentDirectional.centerEnd,
        ),
        bottom: () => horizontal.map(
          left: () => Alignment.bottomLeft,
          center: () => Alignment.bottomCenter,
          right: () => Alignment.bottomRight,
          start: () => AlignmentDirectional.bottomStart,
          end: () => AlignmentDirectional.bottomEnd,
        ),
        // TODO: support baseline alignment in flutter
        baseline: () => horizontal.map(
          left: () => Alignment.centerLeft,
          center: () => Alignment.center,
          right: () => Alignment.centerRight,
          start: () => AlignmentDirectional.centerStart,
          end: () => AlignmentDirectional.centerEnd,
        ),
      );
    } else if (vertical != null) {
      return vertical.map(
        top: () => Alignment.topCenter,
        center: () => Alignment.center,
        bottom: () => Alignment.bottomCenter,
        // TODO: support baseline alignment in flutter
        baseline: () => Alignment.center,
      );
    } else if (horizontal != null) {
      return horizontal.map(
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

  TextAlign? valueTextAlign() {
    switch (horizontal?.value) {
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

  TextAlign valueTextAlign() {
    switch (horizontal.requireValue) {
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

  TextAlignVertical valueTextAlignVertical() {
    switch (vertical.requireValue) {
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
  final List<Widget> backgroundWidgets;

  const PassDivBackground({
    required this.backgroundWidgets,
  });

  static Future<List<Widget>> resolve(
    List<DivBackground> backgrounds, {
    required DivVariableContext context,
    required double viewScale,
  }) async {
    final backgroundWidgets = <Widget>[];
    // TODO: complicated DivBackground support
    for (var bg in backgrounds) {
      await bg.map(
        divImageBackground: (divImageBackground) async {
          final filters = divImageBackground.filters;
          final divBlur = filters?[0] as DivBlur?;
          final isRtl = (filters?[1] as DivFilterRtlMirror?) != null;
          final blurRadius =
              await resolveBlurRadius(divBlur: divBlur, context: context);
          backgroundWidgets.add(
            Transform(
              alignment: Alignment.center,
              transform: Matrix4.rotationY(isRtl ? math.pi : 0),
              child: Container(
                width: double.maxFinite,
                height: double.maxFinite,
                decoration: BoxDecoration(
                  image: DecorationImage(
                    image: NetworkImage(
                      (await divImageBackground.imageUrl
                              .resolveValue(context: context))
                          .toString(),
                    ),
                    alignment: await PassDivAlignment(
                          divImageBackground.contentAlignmentVertical,
                          divImageBackground.contentAlignmentHorizontal,
                        ).resolve(context: context) ??
                        Alignment.center,
                    fit: (await divImageBackground.scale
                            .resolveValue(context: context))
                        .asBoxFit,
                    opacity: await divImageBackground.alpha.resolveValue(
                      context: context,
                    ),
                  ),
                ),
                child: BackdropFilter(
                  filter:
                      ImageFilter.blur(sigmaX: blurRadius, sigmaY: blurRadius),
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
        divNinePatchBackground: (divNinePatchBackground) async {
          final insetsLeft = await divNinePatchBackground.insets.left
              .resolveValue(context: context);
          final insetsTop = await divNinePatchBackground.insets.top
              .resolveValue(context: context);
          final insetsRight = await divNinePatchBackground.insets.right
              .resolveValue(context: context);
          final insetsBottom = await divNinePatchBackground.insets.bottom
              .resolveValue(context: context);
          backgroundWidgets.add(
            Container(
              width: double.maxFinite,
              height: double.maxFinite,
              decoration: BoxDecoration(
                image: DecorationImage(
                  image: NetworkImage(
                    (await divNinePatchBackground.imageUrl
                            .resolveValue(context: context))
                        .toString(),
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
        divRadialGradient: (divRadialGradient) async {
          backgroundWidgets.add(
            Container(
              width: double.maxFinite,
              height: double.maxFinite,
              decoration: BoxDecoration(
                gradient: RadialGradient(
                  colors: (await divRadialGradient.colors
                          .resolveValue(context: context))
                      .map((code) => Color(code.value))
                      .toList(),
                  radius: await divRadialGradient.radius.map(
                    divFixedSize: (divFixedSize) async =>
                        (await divFixedSize.value
                                .resolveValue(context: context))
                            .toDouble() *
                        viewScale,
                    divRadialGradientRelativeRadius:
                        (divRadialGradientRelativeRadius) async {
                      throw UnimplementedError();
                    },
                  ),
                ),
              ),
            ),
          );
        },
        divLinearGradient: (divLinearGradient) async {
          final resolvedRadians =
              await divLinearGradient.angle.resolveValue(context: context) *
                  viewScale /
                  (-180 / math.pi);
          final angleAlignment = Alignment(
            math.cos(resolvedRadians),
            math.sin(resolvedRadians),
          );
          backgroundWidgets.add(
            Container(
              width: double.maxFinite,
              height: double.maxFinite,
              decoration: BoxDecoration(
                gradient: LinearGradient(
                  begin: -angleAlignment,
                  end: angleAlignment,
                  colors: (await divLinearGradient.colors.resolveValue(
                    context: context,
                  ))
                      .map((code) => Color(code.value))
                      .toList(),
                ),
              ),
            ),
          );
        },
        divSolidBackground: (divSolidBackground) async {
          backgroundWidgets.add(
            Container(
              width: double.maxFinite,
              height: double.maxFinite,
              decoration: BoxDecoration(
                color: Color(
                  (await divSolidBackground.color
                          .resolveValue(context: context))
                      .value,
                ),
              ),
            ),
          );
        },
      );
    }
    return backgroundWidgets;
  }

  static List<Widget> value(
    List<DivBackground> backgrounds, {
    required double viewScale,
  }) {
    final backgroundWidgets = <Widget>[];
    // TODO: complicated DivBackground support
    for (var bg in backgrounds) {
      bg.map(
        divImageBackground: (divImageBackground) {
          final filters = divImageBackground.filters;
          final divBlur = filters?[0] as DivBlur?;
          final isRtl = (filters?[1] as DivFilterRtlMirror?) != null;
          final blurRadius = valueBlurRadius(divBlur: divBlur);
          backgroundWidgets.add(
            Transform(
              alignment: Alignment.center,
              transform: Matrix4.rotationY(isRtl ? math.pi : 0),
              child: Container(
                width: double.maxFinite,
                height: double.maxFinite,
                decoration: BoxDecoration(
                  image: DecorationImage(
                    image: NetworkImage(
                      divImageBackground.imageUrl.requireValue.toString(),
                    ),
                    alignment: PassDivAlignment(
                          divImageBackground.contentAlignmentVertical,
                          divImageBackground.contentAlignmentHorizontal,
                        ).valueAlignmentGeometry() ??
                        Alignment.center,
                    fit: divImageBackground.scale.requireValue.asBoxFit,
                    opacity: divImageBackground.alpha.requireValue,
                  ),
                ),
                child: BackdropFilter(
                  filter:
                      ImageFilter.blur(sigmaX: blurRadius, sigmaY: blurRadius),
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
          final insetsLeft = divNinePatchBackground.insets.left.requireValue;
          final insetsTop = divNinePatchBackground.insets.top.requireValue;
          final insetsRight = divNinePatchBackground.insets.right.requireValue;
          final insetsBottom =
              divNinePatchBackground.insets.bottom.requireValue;
          backgroundWidgets.add(
            Container(
              width: double.maxFinite,
              height: double.maxFinite,
              decoration: BoxDecoration(
                image: DecorationImage(
                  image: NetworkImage(
                    divNinePatchBackground.imageUrl.requireValue.toString(),
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
                  colors: divRadialGradient.colors.requireValue
                      .map((code) => Color(code.value))
                      .toList(),
                  radius: divRadialGradient.radius.map(
                    divFixedSize: (divFixedSize) =>
                        divFixedSize.value.requireValue.toDouble() * viewScale,
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
          final resolvedRadians = divLinearGradient.angle.requireValue *
              viewScale /
              (-180 / math.pi);
          final angleAlignment = Alignment(
            math.cos(resolvedRadians),
            math.sin(resolvedRadians),
          );
          backgroundWidgets.add(
            Container(
              width: double.maxFinite,
              height: double.maxFinite,
              decoration: BoxDecoration(
                gradient: LinearGradient(
                  begin: -angleAlignment,
                  end: angleAlignment,
                  colors: divLinearGradient.colors.requireValue
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
                  divSolidBackground.color.requireValue.value,
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

double valueBlurRadius({
  DivBlur? divBlur,
}) {
  if (divBlur == null) {
    return 0.0;
  }
  final radius = divBlur.radius.requireValue;
  return radius.toDouble();
}

Future<double> resolveBlurRadius({
  required DivVariableContext context,
  DivBlur? divBlur,
}) async {
  if (divBlur == null) {
    return 0.0;
  }
  final radius = await divBlur.radius.resolveValue(context: context);
  return radius.toDouble();
}

class DivFilters {
  final double? blurRadius;
  final bool isRtl;

  const DivFilters({this.blurRadius, this.isRtl = false});

  static Future<DivFilters> resolve({
    required List<DivFilter> filters,
    required DivVariableContext context,
    required double viewScale,
  }) async {
    double? blurRadius;
    bool isRtl = false;
    for (var el in filters) {
      await el.map(
        divBlur: (divBlur) async {
          blurRadius =
              (await divBlur.radius.resolveValue(context: context)).toDouble() *
                  viewScale;
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

  static DivFilters value({
    required List<DivFilter> filters,
    required double viewScale,
  }) {
    double? blurRadius;
    bool isRtl = false;
    for (var el in filters) {
      el.map(
        divBlur: (divBlur) async {
          blurRadius = divBlur.radius.requireValue.toDouble() * viewScale;
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
    required double viewScale,
  }) async {
    final safeStart = await start?.resolveValue(context: context);
    final safeEnd = await end?.resolveValue(context: context);
    final safeLeft = await left.resolveValue(context: context);
    final safeRight = await right.resolveValue(context: context);
    final safeTop = await top.resolveValue(context: context);
    final safeBottom = await bottom.resolveValue(context: context);

    if (safeStart != null || safeEnd != null) {
      return EdgeInsetsDirectional.fromSTEB(
        (safeStart?.toDouble() ?? 0) * viewScale,
        safeTop.toDouble() * viewScale,
        (safeEnd?.toDouble() ?? 0) * viewScale,
        safeBottom.toDouble() * viewScale,
      );
    }
    return EdgeInsets.fromLTRB(
      safeLeft.toDouble() * viewScale,
      safeTop.toDouble() * viewScale,
      safeRight.toDouble() * viewScale,
      safeBottom.toDouble() * viewScale,
    );
  }

  EdgeInsetsGeometry value({
    required double viewScale,
  }) {
    final safeStart = start?.requireValue;
    final safeEnd = end?.requireValue;
    final safeLeft = left.requireValue;
    final safeRight = right.requireValue;
    final safeTop = top.requireValue;
    final safeBottom = bottom.requireValue;

    if (safeStart != null || safeEnd != null) {
      return EdgeInsetsDirectional.fromSTEB(
        (safeStart?.toDouble() ?? 0) * viewScale,
        safeTop.toDouble() * viewScale,
        (safeEnd?.toDouble() ?? 0) * viewScale,
        safeBottom.toDouble() * viewScale,
      );
    }
    return EdgeInsets.fromLTRB(
      safeLeft.toDouble() * viewScale,
      safeTop.toDouble() * viewScale,
      safeRight.toDouble() * viewScale,
      safeBottom.toDouble() * viewScale,
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
    required double viewScale,
  }) async =>
      (await value.resolveValue(context: context)) *
      (await unit.resolveValue(context: context)).asPx *
      viewScale;

  double valueDimension({
    required double viewScale,
  }) =>
      (value.requireValue) * (unit.requireValue).asPx * viewScale;
}

extension PassDivFixedSize on DivFixedSize {
  Future<double> resolveDimension({
    required DivVariableContext context,
    required double viewScale,
  }) async =>
      (await value.resolveValue(context: context)) *
      (await unit.resolveValue(context: context)).asPx *
      viewScale;

  double valueDimension({
    required double viewScale,
  }) =>
      (value.requireValue) * (unit.requireValue).asPx * viewScale;
}

extension PassDivWrapContentSizeConstraintSize
    on DivWrapContentSizeConstraintSize {
  Future<double> resolveDimension({
    required DivVariableContext context,
    required double viewScale,
  }) async =>
      (await value.resolveValue(context: context)) *
      (await unit.resolveValue(context: context)).asPx *
      viewScale;

  double valueDimension({
    required double viewScale,
  }) =>
      (value.requireValue) * (unit.requireValue).asPx * viewScale;
}

extension DivPointAsOffset on DivPoint {
  Future<Offset> resolveOffset({
    required DivVariableContext context,
    required double viewScale,
  }) async =>
      Offset(
        await x.resolveDimension(
          context: context,
          viewScale: viewScale,
        ),
        await y.resolveDimension(
          context: context,
          viewScale: viewScale,
        ),
      );

  Offset valueOffset({
    required double viewScale,
  }) =>
      Offset(
        x.valueDimension(
          viewScale: viewScale,
        ),
        y.valueDimension(
          viewScale: viewScale,
        ),
      );
}

class DivDecoration {
  final BoxDecoration boxDecoration;
  final List<Widget> backgroundWidgets;
  final CustomBorderRadius customBorderRadius;
  final BoxShadow? outerShadow;

  DivDecoration({
    required this.boxDecoration,
    required this.customBorderRadius,
    this.outerShadow,
    this.backgroundWidgets = const <Widget>[],
  });
}

class CustomBorderRadius {
  final Radius? topLeft;
  final Radius? topRight;
  final Radius? bottomLeft;
  final Radius? bottomRight;

  CustomBorderRadius({
    this.topLeft = Radius.zero,
    this.topRight = Radius.zero,
    this.bottomLeft = Radius.zero,
    this.bottomRight = Radius.zero,
  });

  BorderRadius toBorderRadius() => BorderRadius.only(
        topLeft: topLeft ?? Radius.zero,
        topRight: topRight ?? Radius.zero,
        bottomLeft: bottomLeft ?? Radius.zero,
        bottomRight: bottomRight ?? Radius.zero,
      );
}

extension PassDivBorder on DivBorder {
  Future<CustomBorderRadius> resolveBorderRadius({
    required DivVariableContext context,
    required double viewScale,
  }) async {
    final singleCornerRadius =
        await cornerRadius?.resolveValue(context: context);

    final multipleCornerRadius = cornersRadius;

    if (multipleCornerRadius != null) {
      // If corners_radius of any corner is null — should use corner_radius
      // https://divkit.tech/en/doc/overview/concepts/divs/2/div-border.html
      final resolvedTopLeft =
          (await multipleCornerRadius.topLeft?.resolveValue(context: context) ??
                  singleCornerRadius ??
                  0) *
              viewScale;
      final resolvedTopRight = (await multipleCornerRadius.topRight
                  ?.resolveValue(context: context) ??
              singleCornerRadius ??
              0) *
          viewScale;
      final resolvedBottomLeft = (await multipleCornerRadius.bottomLeft
                  ?.resolveValue(context: context) ??
              singleCornerRadius ??
              0) *
          viewScale;
      final resolvedBottomRight = (await multipleCornerRadius.bottomRight
                  ?.resolveValue(context: context) ??
              singleCornerRadius ??
              0) *
          viewScale;
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

  CustomBorderRadius valueBorderRadius({
    required double viewScale,
  }) {
    final singleCornerRadius = cornerRadius?.requireValue;

    final multipleCornerRadius = cornersRadius;

    if (multipleCornerRadius != null) {
      // If corners_radius of any corner is null — should use corner_radius
      // https://divkit.tech/en/doc/overview/concepts/divs/2/div-border.html
      final resolvedTopLeft = multipleCornerRadius.topLeft?.requireValue ??
          singleCornerRadius ??
          0 * viewScale;
      final resolvedTopRight = multipleCornerRadius.topRight?.requireValue ??
          singleCornerRadius ??
          0 * viewScale;
      final resolvedBottomLeft =
          multipleCornerRadius.bottomLeft?.requireValue ??
              singleCornerRadius ??
              0 * viewScale;
      final resolvedBottomRight =
          multipleCornerRadius.bottomRight?.requireValue ??
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

  Future<Border?> resolveBorder({
    required DivVariableContext context,
    required double viewScale,
  }) async {
    final borderStroke = stroke;
    if (borderStroke != null) {
      return Border.all(
        width: ((await borderStroke.unit.resolveValue(context: context)).asPx) *
            (await borderStroke.width.resolveValue(context: context))
                .toDouble() *
            viewScale,
        color: Color(
          (await borderStroke.color.resolveValue(context: context)).value,
        ),
      );
    }
    return null;
  }

  Border? valueBorder({
    required double viewScale,
  }) {
    final borderStroke = stroke;
    if (borderStroke != null) {
      return Border.all(
        width: ((borderStroke.unit.requireValue).asPx) *
            (borderStroke.width.requireValue).toDouble() *
            viewScale,
        color: Color(
          (borderStroke.color.requireValue).value,
        ),
      );
    }
    return null;
  }

  Future<BoxShadow?> resolveShadow({
    required DivVariableContext context,
    required double viewScale,
  }) async {
    final borderShadow = shadow;
    if (borderShadow != null &&
        (await hasShadow.resolveValue(context: context))) {
      return BoxShadow(
        color: Color(
          (await borderShadow.color.resolveValue(context: context)).value,
        ).withAlpha(
          (await borderShadow.alpha.resolveValue(context: context) * 255)
              .toInt(),
        ),
        blurRadius: (await borderShadow.blur.resolveValue(context: context))
                .toDouble() *
            viewScale,
        offset: await borderShadow.offset.resolveOffset(
          context: context,
          viewScale: viewScale,
        ),
      );
    }
    return null;
  }

  BoxShadow? valueShadow({
    required double viewScale,
  }) {
    final borderShadow = shadow;
    if (borderShadow != null && hasShadow.requireValue) {
      return BoxShadow(
        color: Color(
          borderShadow.color.requireValue.value,
        ).withAlpha(
          (borderShadow.alpha.requireValue * 255).toInt(),
        ),
        blurRadius: borderShadow.blur.requireValue.toDouble() * viewScale,
        offset: borderShadow.offset.valueOffset(
          viewScale: viewScale,
        ),
      );
    }
    return null;
  }
}

extension PassDivShadow on DivShadow {
  Future<BoxShadow?> resolveBoxShadow({
    required DivVariableContext context,
    required double viewScale,
  }) async =>
      BoxShadow(
        color: (await color.resolveValue(context: context)).withOpacity(
          await alpha.resolveValue(context: context),
        ),
        blurRadius:
            (await blur.resolveValue(context: context)).toDouble() * viewScale,
        offset:
            await offset.resolveOffset(context: context, viewScale: viewScale),
      );

  BoxShadow? valueBoxShadow({
    required double viewScale,
  }) =>
      BoxShadow(
        color: color.requireValue.withOpacity(alpha.requireValue),
        blurRadius: blur.requireValue.toDouble() * viewScale,
        offset: offset.valueOffset(
          viewScale: viewScale,
        ),
      );

  Future<Shadow?> resolveShadow({
    required DivVariableContext context,
    required double viewScale,
  }) async =>
      Shadow(
        color: (await color.resolveValue(context: context)).withOpacity(
          await alpha.resolveValue(context: context),
        ),
        blurRadius:
            (await blur.resolveValue(context: context)).toDouble() * viewScale,
        offset:
            await offset.resolveOffset(context: context, viewScale: viewScale),
      );

  Shadow? valueShadow({
    required double viewScale,
  }) =>
      Shadow(
        color: color.requireValue.withOpacity(alpha.requireValue),
        blurRadius: blur.requireValue.toDouble() * viewScale,
        offset: offset.valueOffset(
          viewScale: viewScale,
        ),
      );
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

  FontWeight passValue() {
    switch (requireValue) {
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
  Future<List<DivActionModel>> resolveOnBlurActions({
    required DivVariableContext context,
  }) async {
    List<DivActionModel> result = [];
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

  List<DivActionModel> valueOnBlurActions() {
    List<DivActionModel> result = [];
    final blurAction = onBlur;
    if (blurAction != null) {
      for (final action in blurAction) {
        final rAction = action.value();
        if (rAction.enabled) {
          result.add(rAction);
        }
      }
    }
    return result;
  }

  Future<List<DivActionModel>> resolveOnFocusActions({
    required DivVariableContext context,
  }) async {
    List<DivActionModel> result = [];
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

  List<DivActionModel> valueOnFocusActions() {
    List<DivActionModel> result = [];
    final blurAction = onFocus;
    if (blurAction != null) {
      for (final action in blurAction) {
        final rAction = action.value();
        if (rAction.enabled) {
          result.add(rAction);
        }
      }
    }
    return result;
  }
}
