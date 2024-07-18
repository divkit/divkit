import 'package:divkit/src/core/protocol/div_context.dart';
import 'package:divkit/src/generated_sources/div_image.dart';
import 'package:divkit/src/utils/converters.dart';
import 'package:divkit/src/utils/provider.dart';
import 'package:equatable/equatable.dart';
import 'package:flutter/material.dart';
import 'package:divkit/src/utils/div_scaling_model.dart';

class DivImageModel with EquatableMixin {
  final String src;
  final BoxFit fit;
  final Color? color;
  final BlendMode? colorBlendMode;
  final AlignmentGeometry contentAlignment;
  final double? aspectRatio;
  final double? blurRadius;
  final bool rtlMirror;

  const DivImageModel({
    required this.src,
    required this.fit,
    required this.contentAlignment,
    required this.rtlMirror,
    this.color,
    this.colorBlendMode,
    this.aspectRatio,
    this.blurRadius,
  });

  static Stream<DivImageModel> from(
    BuildContext context,
    DivImage data,
  ) {
    final variables =
        DivKitProvider.watch<DivContext>(context)!.variableManager;

    final divScalingModel = DivKitProvider.watch<DivScalingModel>(context);
    final viewScale = divScalingModel?.viewScale ?? 1;

    return variables.watch<DivImageModel>((context) async {
      final alignment = PassDivAlignment(
        data.contentAlignmentVertical,
        data.contentAlignmentHorizontal,
      );

      final filters = await DivFilters.resolve(
        filters: data.filters ?? [],
        context: context,
        viewScale: viewScale,
      );

      return DivImageModel(
        src: (await data.imageUrl.resolveValue(
          context: context,
        ))
            .toString(),
        fit: (await data.scale.resolveValue(
          context: context,
        ))
            .asBoxFit,
        color: await data.tintColor?.resolveValue(
          context: context,
        ),
        colorBlendMode: (await data.tintMode.resolveValue(
          context: context,
        ))
            .asBlendMode,
        contentAlignment: await alignment.resolve(
              context: context,
            ) ??
            Alignment.center,
        aspectRatio: await data.aspect?.resolve(
          context: context,
        ),
        blurRadius: filters.blurRadius == null
            ? null
            : (filters.blurRadius ?? 0) * viewScale,
        rtlMirror: filters.isRtl,
      );
    }).distinct();
  }

  @override
  List<Object?> get props => [
        src,
        fit,
        color,
        colorBlendMode,
        contentAlignment,
        aspectRatio,
        blurRadius,
        rtlMirror,
      ];
}
