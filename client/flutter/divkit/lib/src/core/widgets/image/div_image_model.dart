import 'package:divkit/divkit.dart';
import 'package:divkit/src/core/converters/alignment.dart';
import 'package:divkit/src/core/converters/filters_converter.dart';
import 'package:divkit/src/core/converters/image_specific.dart';
import 'package:divkit/src/utils/provider.dart';
import 'package:equatable/equatable.dart';
import 'package:flutter/material.dart';

class DivImageModel with EquatableMixin {
  final String src;
  final BoxFit fit;
  final Color? color;
  final BlendMode? colorBlendMode;
  final AlignmentGeometry contentAlignment;
  final double? blurRadius;
  final bool rtlMirror;

  const DivImageModel({
    required this.src,
    required this.fit,
    required this.contentAlignment,
    required this.rtlMirror,
    this.color,
    this.colorBlendMode,
    this.blurRadius,
  });

  @override
  List<Object?> get props => [
        src,
        fit,
        color,
        colorBlendMode,
        contentAlignment,
        blurRadius,
        rtlMirror,
      ];
}

extension DivImageConverer on DivImage {
  DivImageModel resolve(BuildContext context) {
    final divContext = read<DivContext>(context)!;
    final variables = divContext.variables;
    final viewScale = divContext.scale.view;

    final alignment = DivAlignmentConverter(
      contentAlignmentVertical,
      contentAlignmentHorizontal,
    ).resolve(variables);

    final filter = DivFilters.combine(
      variables,
      filters: filters ?? [],
      viewScale: viewScale,
    );

    return DivImageModel(
      src: imageUrl.resolve(variables).toString(),
      fit: scale.resolve(variables).convert(),
      color: tintColor?.resolve(variables),
      colorBlendMode: tintMode.resolve(variables).convert(),
      contentAlignment: alignment ?? Alignment.center,
      blurRadius: filter.blurRadius == null
          ? null
          : (filter.blurRadius ?? 0) * viewScale,
      rtlMirror: filter.isRtl,
    );
  }
}
