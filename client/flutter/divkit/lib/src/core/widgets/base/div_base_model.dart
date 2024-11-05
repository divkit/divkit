import 'package:divkit/divkit.dart';
import 'package:divkit/src/core/converters/alignment.dart';
import 'package:divkit/src/core/converters/base_specific.dart';
import 'package:divkit/src/core/converters/decoration.dart';
import 'package:divkit/src/core/converters/edge_insets.dart';
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
  DivBaseModel convert({
    required double viewScale,
    Expression<double>? aspect,
  }) {
    final alignment = DivAlignmentConverter(
      alignmentVertical,
      alignmentHorizontal,
    ).convert();

    return DivBaseModel(
      isGone: visibility.value.isGone,
      opacity: convertOpacity().clamp(0.0, 1.0),
      alignment: alignment,
      width: valueWidth(viewScale: viewScale),
      height: valueHeight(viewScale: viewScale),
      visibilityActions: visibilityActions?.map((e) => e.convert()).toList() ??
          (visibilityAction != null ? [visibilityAction!.convert()] : []),
      padding: paddings.convert(viewScale: viewScale),
      margin: margins.convert(viewScale: viewScale),
      aspect: aspect?.value,
      decoration: convertDecoration(viewScale: viewScale),
      divId: id,
      focusDecoration: convertFocusDecoration(viewScale: viewScale),
      divVisibility: visibility.value,
    );
  }
}
