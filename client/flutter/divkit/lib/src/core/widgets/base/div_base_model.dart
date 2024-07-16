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
  final BoxDecoration? boxDecoration;
  final BoxDecoration focusDecoration;
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
    this.boxDecoration,
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
          boxDecoration: await data.resolveBoxDecoration(
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
        boxDecoration,
        focusDecoration,
        divId,
        divVisibility,
        visibilityActions,
      ];
}

extension PassDivBase on DivBase {
  Future<BoxDecoration> resolveBoxDecoration({
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
    final boxShadow = await border.resolveShadow(
      context: context,
      viewScale: viewScale,
    );
    final backgrounds = background;
    if (backgrounds != null) {
      final resolvedBackground = await PassDivBackground.resolve(
        backgrounds,
        context: context,
        viewScale: viewScale,
      );
      return BoxDecoration(
        color: resolvedBackground.bgColor,
        gradient: resolvedBackground.bgGradient,
        image: resolvedBackground.bgImage,
        border: boxBorder,
        borderRadius: borderRadius,
        boxShadow: boxShadow,
      );
    }
    return BoxDecoration(
      border: boxBorder,
      borderRadius: borderRadius,
      boxShadow: boxShadow,
    );
  }

  Future<BoxDecoration> resolveFocusBoxDecoration({
    required DivVariableContext context,
    required double viewScale,
  }) async {
    final BoxDecoration focusDecoration;
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
      final resolvedBackground = await PassDivBackground.resolve(
        focusBg,
        context: context,
        viewScale: viewScale,
      );
      focusDecoration = BoxDecoration(
        color: resolvedBackground.bgColor,
        gradient: resolvedBackground.bgGradient,
        image: resolvedBackground.bgImage,
        border: resolvedFocusBorder,
        borderRadius: focusRadius,
      );
    } else {
      focusDecoration = BoxDecoration(
        border: resolvedFocusBorder,
        borderRadius: focusRadius,
      );
    }

    return focusDecoration;
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
