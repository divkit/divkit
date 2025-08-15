import 'package:divkit/divkit.dart';
import 'package:divkit/src/core/converters/alignment.dart';
import 'package:divkit/src/core/converters/base_specific.dart';
import 'package:divkit/src/core/converters/decoration.dart';
import 'package:divkit/src/core/converters/edge_insets.dart';
import 'package:divkit/src/utils/provider.dart';
import 'package:equatable/equatable.dart';
import 'package:flutter/widgets.dart';

class DivBaseModel with EquatableMixin {
  final double opacity;
  final bool isGone;
  final AlignmentGeometry? alignment;
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
    required this.focusDecoration,
    required this.divVisibility,
    required this.isGone,
    this.alignment,
    this.opacity = 1.0,
    this.visibilityActions = const [],
    this.padding,
    this.margin,
    this.aspect,
    this.decoration,
    this.divId,
  });

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

extension DivBaseConverter on DivBase {
  DivBaseModel resolve(
    BuildContext context, {
    Expression<double>? aspect,
  }) {
    final divContext = read<DivContext>(context)!;
    final variables = divContext.variables;
    final viewScale = divContext.scale.view;

    final alignment = DivAlignmentConverter(
      alignmentVertical,
      alignmentHorizontal,
    ).resolve(variables);

    return DivBaseModel(
      isGone: visibility.resolve(variables).isGone,
      opacity: resolveOpacity(variables).clamp(0.0, 1.0),
      alignment: alignment,
      width: resolveWidth(variables, viewScale: viewScale),
      height: resolveHeight(variables, viewScale: viewScale),
      visibilityActions:
          visibilityActions?.map((e) => e.resolve(variables)).toList() ??
              (visibilityAction != null
                  ? [visibilityAction!.resolve(variables)]
                  : []),
      padding: paddings.resolve(variables, viewScale: viewScale),
      margin: margins.resolve(variables, viewScale: viewScale),
      aspect: aspect?.resolve(variables),
      decoration: resolveDecoration(variables, viewScale: viewScale),
      divId: id,
      focusDecoration: resolveFocusDecoration(variables, viewScale: viewScale),
      divVisibility: visibility.resolve(variables),
    );
  }
}
