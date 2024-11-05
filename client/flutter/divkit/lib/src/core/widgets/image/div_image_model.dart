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

extension DivImageBinder on DivImage {
  DivImageModel bind(BuildContext context) {
    final divContext = read<DivContext>(context)!;

    final viewScale = divContext.scale.view;

    final alignment = DivAlignmentConverter(
      contentAlignmentVertical,
      contentAlignmentHorizontal,
    ).convert();

    final filter = DivFilters.combine(
      filters: filters ?? [],
      viewScale: viewScale,
    );

    return DivImageModel(
      src: imageUrl.value.toString(),
      fit: scale.value.convert(),
      color: tintColor?.value,
      colorBlendMode: tintMode.value.convert(),
      contentAlignment: alignment ?? Alignment.center,
      blurRadius: filter.blurRadius == null
          ? null
          : (filter.blurRadius ?? 0) * viewScale,
      rtlMirror: filter.isRtl,
    );
  }
}
