import 'package:collection/collection.dart';
import 'package:divkit/divkit.dart';
import 'package:divkit/src/core/widgets/gallery/div_gallery_model.dart';
import 'package:divkit/src/utils/mapping_widget.dart';
import 'package:divkit/src/utils/provider.dart';
import 'package:flutter/widgets.dart';

class DivGalleryWidget extends DivMappingWidget<DivGallery, DivGalleryModel> {
  const DivGalleryWidget(
    super.data, {
    super.key,
  });

  @override
  DivGalleryModel value(BuildContext context) => data.resolve(context);

  @override
  Stream<DivGalleryModel> stream(BuildContext context) =>
      watch<DivContext>(context)!.variableManager.watch(
            (values) => data.resolve(context),
          );

  @override
  Widget build(BuildContext context, DivGalleryModel model) {
    final isHorizontal = model.orientation == Axis.horizontal;

    final childrenWithSpacing = model.children
        .mapIndexed((i, element) {
          final isLastElement = i == model.children.length - 1;
          final spacing = isLastElement ? 0.0 : model.itemSpacing;
          return [
            element,
            SizedBox(
              width: isHorizontal ? spacing : null,
              height: isHorizontal ? null : spacing,
            ),
          ];
        })
        .expand((element) => element)
        .toList();

    return DivBaseWidget(
      data: data,
      child: SingleChildScrollView(
        scrollDirection: model.orientation,
        child: provide(
          isHorizontal ? DivParentData.row : DivParentData.column,
          child: Flex(
            crossAxisAlignment: CrossAxisAlignment.start,
            direction: isHorizontal ? Axis.horizontal : Axis.vertical,
            mainAxisSize: MainAxisSize.min,
            children: childrenWithSpacing,
          ),
        ),
      ),
    );
  }
}
