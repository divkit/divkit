import 'package:collection/collection.dart';
import 'package:divkit/divkit.dart';
import 'package:divkit/src/core/widgets/gallery/div_gallery_model.dart';
import 'package:divkit/src/utils/provider.dart';
import 'package:flutter/widgets.dart';

class DivGalleryWidget extends StatefulWidget {
  final DivGallery data;

  const DivGalleryWidget(
    this.data, {
    super.key,
  });

  @override
  State<DivGalleryWidget> createState() => _DivGalleryWidgetState();
}

class _DivGalleryWidgetState extends State<DivGalleryWidget> {
  DivGalleryModel? value;
  Stream<DivGalleryModel>? stream;

  @override
  void initState() {
    super.initState();
    value = DivGalleryModel.value(context, widget.data);
  }

  @override
  void didChangeDependencies() {
    super.didChangeDependencies();

    stream ??= DivGalleryModel.from(context, widget.data);
  }

  @override
  void didUpdateWidget(covariant DivGalleryWidget oldWidget) {
    super.didUpdateWidget(oldWidget);

    if (widget.data != oldWidget.data) {
      value = DivGalleryModel.value(context, widget.data);
      stream = DivGalleryModel.from(context, widget.data);
    }
  }

  @override
  Widget build(BuildContext context) => DivBaseWidget(
        data: widget.data,
        child: StreamBuilder<DivGalleryModel>(
          initialData: value,
          stream: stream,
          builder: (context, snapshot) {
            if (snapshot.hasData) {
              final model = snapshot.requireData;

              final isHorizontal = model.orientation == Axis.horizontal;

              final childrenWithSpacing = model.children
                  .mapIndexed((i, e) {
                    final isLastElement = i == model.children.length - 1;
                    final spacing = isLastElement ? 0.0 : model.itemSpacing;
                    return [
                      e,
                      SizedBox(
                        width: isHorizontal ? spacing : null,
                        height: isHorizontal ? null : spacing,
                      ),
                    ];
                  })
                  .expand((el) => el)
                  .toList();

              return SingleChildScrollView(
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
              );
            }

            return const SizedBox.shrink();
          },
        ),
      );

  @override
  void dispose() {
    value = null;
    stream = null;
    super.dispose();
  }
}
