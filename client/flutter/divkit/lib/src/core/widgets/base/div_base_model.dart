import 'package:divkit/src/core/protocol/div_context.dart';
import 'package:divkit/src/core/protocol/div_variable.dart';
import 'package:divkit/src/generated_sources/div_base.dart';
import 'package:divkit/src/generated_sources/div_visibility.dart';
import 'package:divkit/src/utils/converters.dart';
import 'package:divkit/src/utils/provider.dart';
import 'package:divkit/src/utils/size_converters.dart';
import 'package:equatable/equatable.dart';
import 'package:flutter/widgets.dart';

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
  final bool isVisible;

  const DivBaseModel({
    required this.width,
    required this.height,
    required this.alignment,
    required this.focusDecoration,
    required this.isVisible,
    this.opacity = 1.0,
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

    return variables.watch<DivBaseModel>(
      (context) async {
        final margin = await data.margins.resolve(
          context: context,
        );

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
          ),
          height: await data.resolveHeight(
            context: context,
            extension: margin.vertical,
          ),
          padding: await data.paddings.resolve(
            context: context,
          ),
          margin: margin,
          boxDecoration: await data.resolveBoxDecoration(
            context: context,
          ),
          divId: data.id,
          focusDecoration: await data.resolveFocusBoxDecoration(
            context: context,
          ),
          isVisible: (await data.visibility.resolveValue(
                context: context,
              )) ==
              DivVisibility.visible,
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
        isVisible,
      ];
}

extension PassDivBase on DivBase {
  Future<BoxDecoration> resolveBoxDecoration({
    required DivVariableContext context,
  }) async {
    final boxBorder = await border.resolveBorder(context: context);
    final borderRadius = await border.resolveBorderRadius(context: context);
    final boxShadow = await border.resolveShadow(context: context);
    final backgrounds = background;
    if (backgrounds != null) {
      final resolvedBackground = await PassDivBackground.resolve(
        backgrounds,
        context: context,
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
  }) async {
    final BoxDecoration focusDecoration;
    final focusBg = focus?.background;
    final focusBorder = focus?.border;
    final resolvedFocusBorder =
        await focusBorder?.resolveBorder(context: context);
    final focusRadius =
        await focusBorder?.resolveBorderRadius(context: context);
    if (focusBg != null) {
      final resolvedBackground = await PassDivBackground.resolve(
        focusBg,
        context: context,
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
    double extension = 0,
  }) async {
    if ((await visibility.resolveValue(context: context)).isGone) {
      return const FixedDivSize(0);
    } else {
      return await width.resolve(context: context, extension: extension);
    }
  }

  Future<PassDivSize> resolveHeight({
    required DivVariableContext context,
    double extension = 0,
  }) async {
    if ((await visibility.resolveValue(context: context)).isGone) {
      return const FixedDivSize(0);
    } else {
      return await height.resolve(context: context, extension: extension);
    }
  }
}
