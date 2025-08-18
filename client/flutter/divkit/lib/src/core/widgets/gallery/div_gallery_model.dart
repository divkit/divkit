import 'package:divkit/divkit.dart';
import 'package:divkit/src/core/converters/gallery_specific.dart';
import 'package:divkit/src/utils/provider.dart';
import 'package:equatable/equatable.dart';
import 'package:flutter/widgets.dart';

class DivGalleryModel with EquatableMixin {
  final Axis orientation;
  final CrossAxisAlignment crossContentAlignment;
  final double itemSpacing;
  final List<Widget> children;

  const DivGalleryModel({
    required this.orientation,
    required this.crossContentAlignment,
    required this.itemSpacing,
    required this.children,
  });

  @override
  List<Object?> get props => [
        orientation,
        crossContentAlignment,
        itemSpacing,
      ];
}

extension DivGalleryConverter on DivGallery {
  DivGalleryModel resolve(BuildContext context) {
    final divContext = read<DivContext>(context)!;
    final variables = divContext.variables;
    final viewScale = divContext.scale.view;

    final alignment = crossContentAlignment.resolve(variables).convert();
    final orientation = this.orientation.resolve(variables).convert();
    final children = items?.map((e) => DivWidget(e)).toList();
    final itemSpacing =
        this.itemSpacing.resolve(variables).toDouble() * viewScale;

    return DivGalleryModel(
      crossContentAlignment: alignment,
      orientation: orientation,
      itemSpacing: itemSpacing,
      children: children ?? [],
    );
  }
}
