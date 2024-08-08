import 'package:divkit/divkit.dart';
import 'package:divkit/src/utils/div_scaling_model.dart';
import 'package:divkit/src/utils/provider.dart';
import 'package:equatable/equatable.dart';
import 'package:flutter/widgets.dart';

class DivGalleryModel with EquatableMixin {
  final List<Widget> children;
  final CrossAxisAlignment crossContentAlignment;
  final Axis orientation;
  final double itemSpacing;

  const DivGalleryModel({
    required this.children,
    required this.crossContentAlignment,
    required this.orientation,
    required this.itemSpacing,
  });

  static DivGalleryModel? value(
    BuildContext context,
    DivGallery data,
  ) {
    try {
      final divScalingModel = read<DivScalingModel>(context);
      final viewScale = divScalingModel?.viewScale ?? 1;

      final rawAlignment = data.crossContentAlignment.value!;
      final alignment = _convertAlignment(rawAlignment);

      final rawOrientation = data.orientation.value!;
      final orientation = _convertOrientation(rawOrientation);

      final children = data.items
              ?.map(
                (e) => DivWidget(e),
              )
              .toList(growable: false) ??
          [];

      return DivGalleryModel(
        children: children,
        crossContentAlignment: alignment,
        orientation: orientation,
        itemSpacing: data.itemSpacing.value!.toDouble() * viewScale,
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

  static Stream<DivGalleryModel> from(
    BuildContext context,
    DivGallery data,
  ) {
    final variables = watch<DivContext>(context)!.variableManager;

    final divScalingModel = watch<DivScalingModel>(context);
    final viewScale = divScalingModel?.viewScale ?? 1;

    return variables.watch<DivGalleryModel>((context) async {
      final rawAlignment = await data.crossContentAlignment.resolveValue(
        context: context,
      );
      final alignment = _convertAlignment(rawAlignment);

      final rawOrientation = await data.orientation.resolveValue(
        context: context,
      );
      final orientation = _convertOrientation(rawOrientation);

      final children = data.items
              ?.map(
                (e) => DivWidget(e),
              )
              .toList(growable: false) ??
          [];

      return DivGalleryModel(
        children: children,
        crossContentAlignment: alignment,
        orientation: orientation,
        itemSpacing: (await data.itemSpacing.resolveValue(
              context: context,
            ))
                .toDouble() *
            viewScale,
      );
    }).distinct();
  }

  @override
  List<Object?> get props => [
        crossContentAlignment,
        orientation,
        itemSpacing,
      ];

  static CrossAxisAlignment _convertAlignment(
    DivGalleryCrossContentAlignment alignment,
  ) {
    switch (alignment) {
      case DivGalleryCrossContentAlignment.start:
        return CrossAxisAlignment.start;
      case DivGalleryCrossContentAlignment.center:
        return CrossAxisAlignment.center;
      case DivGalleryCrossContentAlignment.end:
        return CrossAxisAlignment.end;
    }
  }

  static Axis _convertOrientation(DivGalleryOrientation orientation) {
    switch (orientation) {
      case DivGalleryOrientation.horizontal:
        return Axis.horizontal;
      case DivGalleryOrientation.vertical:
        return Axis.vertical;
    }
  }
}
