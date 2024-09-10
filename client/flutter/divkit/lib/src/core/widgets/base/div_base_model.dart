import 'package:divkit/divkit.dart';
import 'package:divkit/src/utils/div_scaling_model.dart';
import 'package:divkit/src/utils/provider.dart';
import 'package:equatable/equatable.dart';
import 'package:flutter/widgets.dart';

class DivBaseModel with EquatableMixin {
  final double opacity;
  final bool isGone;
  final DivAlignment alignment;
  final DivSizeValue width;
  final DivSizeValue height;
  final double? aspect;
  final EdgeInsetsGeometry? padding;
  final EdgeInsetsGeometry? margin;
  final DivDecoration? decoration;
  final DivDecoration focusDecoration;
  final String? divId;
  final DivVisibility divVisibility;
  final List<DivVisibilityActionModel> visibilityActions;

  const DivBaseModel({
    required this.width,
    required this.height,
    required this.alignment,
    required this.focusDecoration,
    required this.divVisibility,
    required this.isGone,
    this.opacity = 1.0,
    this.visibilityActions = const [],
    this.padding,
    this.margin,
    this.aspect,
    this.decoration,
    this.divId,
  });

  static DivBaseModel? value(
    BuildContext context,
    DivBase data, [
    Expression<double>? aspect,
  ]) {
    final divScalingModel = read<DivScalingModel>(context);
    final viewScale = divScalingModel?.viewScale ?? 1;

    try {
      final margin = data.margins.value(
        viewScale: viewScale,
      );

      final List<DivVisibilityActionModel> visibilityActionsList = [];
      final dtoVisibilityActionsList = [
        ...?data.visibilityActions,
      ];
      final visibilityAction = data.visibilityAction;
      if (data.visibilityActions == null && visibilityAction != null) {
        dtoVisibilityActionsList.add(visibilityAction);
      }
      for (final dtoAction in dtoVisibilityActionsList) {
        visibilityActionsList.add(dtoAction.value());
      }

      return DivBaseModel(
        isGone: data.visibility.requireValue.isGone,
        opacity: data.valueOpacity().clamp(0.0, 1.0),
        alignment: PassDivAlignment(
          data.alignmentVertical,
          data.alignmentHorizontal,
        ).valueAlignment(),
        width: data.valueWidth(
          viewScale: viewScale,
        ),
        height: data.valueHeight(
          viewScale: viewScale,
        ),
        visibilityActions: visibilityActionsList,
        padding: data.paddings.value(
          viewScale: viewScale,
        ),
        margin: margin,
        aspect: aspect?.requireValue,
        decoration: data.valueBoxDecoration(
          viewScale: viewScale,
        ),
        divId: data.id,
        focusDecoration: data.valueFocusBoxDecoration(
          viewScale: viewScale,
        ),
        divVisibility: data.visibility.requireValue,
      );
    } catch (e, st) {
      logger.warning(
        'Expression cache is corrupted! Instant rendering is not available for div',
        error: e,
        stackTrace: st,
      );
      return null;
    }
  }

  static Stream<DivBaseModel> from(
    BuildContext context,
    DivBase data, [
    Expression<double>? aspect,
  ]) {
    final variables = watch<DivContext>(context)!.variableManager;
    final divScalingModel = watch<DivScalingModel>(context);
    final viewScale = divScalingModel?.viewScale ?? 1;

    return variables.watch<DivBaseModel>(
      (context) async {
        final margin = await data.margins.resolve(
          context: context,
          viewScale: viewScale,
        );

        final List<DivVisibilityActionModel> visibilityActionsList = [];
        final dtoVisibilityActionsList = [
          ...?data.visibilityActions,
        ];
        final visibilityAction = data.visibilityAction;
        if (data.visibilityActions == null && visibilityAction != null) {
          dtoVisibilityActionsList.add(visibilityAction);
        }
        for (final dtoAction in dtoVisibilityActionsList) {
          visibilityActionsList.add(
            await dtoAction.resolve(
              context: context,
            ),
          );
        }

        return DivBaseModel(
          isGone: (await data.visibility.resolveValue(context: context)).isGone,
          opacity: (await data.resolveOpacity(
            context: context,
          ))
              .clamp(0.0, 1.0),
          alignment: await PassDivAlignment(
            data.alignmentVertical,
            data.alignmentHorizontal,
          ).resolveAlignment(
            context: context,
          ),
          width: await data.resolveWidth(
            context: context,
            viewScale: viewScale,
          ),
          height: await data.resolveHeight(
            context: context,
            viewScale: viewScale,
          ),
          visibilityActions: visibilityActionsList,
          padding: await data.paddings.resolve(
            context: context,
            viewScale: viewScale,
          ),
          margin: margin,
          aspect: await aspect?.resolveValue(
            context: context,
          ),
          decoration: await data.resolveBoxDecoration(
            context: context,
            viewScale: viewScale,
          ),
          divId: data.id,
          focusDecoration: await data.resolveFocusBoxDecoration(
            context: context,
            viewScale: viewScale,
          ),
          divVisibility: (await data.visibility.resolveValue(
            context: context,
          )),
        );
      },
    ).distinct(); // The widget is redrawn when the model changes.
  }

  @override
  List<Object?> get props => [
        opacity,
        alignment,
        width,
        height,
        aspect,
        padding,
        margin,
        decoration,
        focusDecoration,
        divId,
        divVisibility,
        visibilityActions,
      ];
}

extension PassDivBase on DivBase {
  Future<DivDecoration> resolveBoxDecoration({
    required DivVariableContext context,
    required double viewScale,
  }) async {
    final boxBorder = await border.resolveBorder(
      context: context,
      viewScale: viewScale,
    );
    final borderRadius = await border.resolveBorderRadius(
      context: context,
      viewScale: viewScale,
    );
    final outerShadow = await border.resolveShadow(
      context: context,
      viewScale: viewScale,
    );
    final backgrounds = background;
    if (backgrounds != null) {
      final backgroundWidgets = await PassDivBackground.resolve(
        backgrounds,
        context: context,
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

  DivDecoration valueBoxDecoration({
    required double viewScale,
  }) {
    final boxBorder = border.valueBorder(
      viewScale: viewScale,
    );
    final borderRadius = border.valueBorderRadius(
      viewScale: viewScale,
    );
    final outerShadow = border.valueShadow(
      viewScale: viewScale,
    );
    final backgrounds = background;
    if (backgrounds != null) {
      final backgroundWidgets = PassDivBackground.value(
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

  Future<DivDecoration> resolveFocusBoxDecoration({
    required DivVariableContext context,
    required double viewScale,
  }) async {
    final focusBg = focus?.background;
    final focusBorder = focus?.border;
    final resolvedFocusBorder = await focusBorder?.resolveBorder(
      context: context,
      viewScale: viewScale,
    );
    final focusRadius = await focusBorder?.resolveBorderRadius(
      context: context,
      viewScale: viewScale,
    );
    if (focusBg != null) {
      final backgroundWidgets = await PassDivBackground.resolve(
        focusBg,
        context: context,
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

  DivDecoration valueFocusBoxDecoration({
    required double viewScale,
  }) {
    final focusBg = focus?.background;
    final focusBorder = focus?.border;
    final resolvedFocusBorder = focusBorder?.valueBorder(
      viewScale: viewScale,
    );
    final focusRadius = focusBorder?.valueBorderRadius(
      viewScale: viewScale,
    );
    if (focusBg != null) {
      final backgroundWidgets = PassDivBackground.value(
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

  Future<double> resolveOpacity({
    required DivVariableContext context,
  }) async {
    final visibility = (await this.visibility.resolveValue(context: context));
    if (!visibility.isVisible) {
      return 0;
    }
    return await alpha.resolveValue(context: context);
  }

  double valueOpacity() {
    final visibility = this.visibility.requireValue;
    if (!visibility.isVisible) {
      return 0;
    }
    return alpha.requireValue;
  }

  Future<DivSizeValue> resolveWidth({
    required DivVariableContext context,
    required double viewScale,
  }) async =>
      await width.resolve(
        context: context,
        viewScale: viewScale,
      );

  DivSizeValue valueWidth({
    required double viewScale,
  }) =>
      width.passValue(
        viewScale: viewScale,
      );

  Future<DivSizeValue> resolveHeight({
    required DivVariableContext context,
    required double viewScale,
  }) async =>
      await height.resolve(
        context: context,
        viewScale: viewScale,
      );

  DivSizeValue valueHeight({
    required double viewScale,
  }) =>
      height.passValue(
        viewScale: viewScale,
      );
}
