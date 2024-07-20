import 'package:divkit/src/core/protocol/div_context.dart';
import 'package:divkit/src/core/protocol/div_variable.dart';
import 'package:divkit/src/core/visibility/visibility_action_converter.dart';
import 'package:divkit/src/generated_sources/div_base.dart';
import 'package:divkit/src/generated_sources/div_visibility.dart';
import 'package:divkit/src/utils/converters.dart';
import 'package:divkit/src/utils/provider.dart';
import 'package:divkit/src/utils/size_converters.dart';
import 'package:equatable/equatable.dart';
import 'package:flutter/widgets.dart';
import 'package:divkit/src/utils/div_scaling_model.dart';
import 'package:divkit/src/core/visibility/models/visibility_action.dart';

class DivBaseModel with EquatableMixin {
  final double opacity;
  final DivAlignment alignment;
  final PassDivSize width;
  final PassDivSize height;
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
    this.opacity = 1.0,
    this.visibilityActions = const [],
    this.padding,
    this.margin,
    this.decoration,
    this.divId,
  });

  static Stream<DivBaseModel> from(
    BuildContext context,
    DivBase data,
  ) {
    final variables =
        DivKitProvider.watch<DivContext>(context)!.variableManager;

    final divScalingModel = DivKitProvider.watch<DivScalingModel>(context);
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
            extension: margin.horizontal,
            viewScale: viewScale,
          ),
          height: await data.resolveHeight(
            context: context,
            extension: margin.vertical,
            viewScale: viewScale,
          ),
          visibilityActions: visibilityActionsList,
          padding: await data.paddings.resolve(
            context: context,
            viewScale: viewScale,
          ),
          margin: margin,
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

  Future<double> resolveOpacity({
    required DivVariableContext context,
  }) async {
    final opacity = (await visibility.resolveValue(context: context)).asOpacity;
    if (opacity != 1) {
      return 0;
    }
    return await alpha.resolveValue(context: context);
  }

  Future<PassDivSize> resolveWidth({
    required DivVariableContext context,
    required double viewScale,
    double extension = 0,
  }) async {
    if ((await visibility.resolveValue(context: context)).isGone) {
      return const FixedDivSize(0);
    } else {
      return await width.resolve(
        context: context,
        extension: extension,
        viewScale: viewScale,
      );
    }
  }

  Future<PassDivSize> resolveHeight({
    required DivVariableContext context,
    required double viewScale,
    double extension = 0,
  }) async {
    if ((await visibility.resolveValue(context: context)).isGone) {
      return const FixedDivSize(0);
    } else {
      return await height.resolve(
        context: context,
        extension: extension,
        viewScale: viewScale,
      );
    }
  }
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
