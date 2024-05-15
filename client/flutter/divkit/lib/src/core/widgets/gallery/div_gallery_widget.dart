import 'package:collection/collection.dart';
import 'package:divkit/src/core/widgets/base/div_base_widget.dart';
import 'package:divkit/src/core/widgets/gallery/div_gallery_model.dart';
import 'package:divkit/src/generated_sources/div_gallery.dart';
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
  // ToDo: Optimize repeated calculations on the same context
  Stream<DivGalleryModel>? stream;

  @override
  void didChangeDependencies() {
    super.didChangeDependencies();

    stream ??= DivGalleryModel.from(context, widget.data);
  }

  @override
  void didUpdateWidget(covariant DivGalleryWidget oldWidget) {
    super.didUpdateWidget(oldWidget);

    if (widget.data != oldWidget.data) {
      stream = DivGalleryModel.from(context, widget.data);
    }
  }

  @override
  Widget build(BuildContext context) => DivBaseWidget(
        data: widget.data,
        child: StreamBuilder<DivGalleryModel>(
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
                child: isHorizontal
                    ? Row(
                        children: childrenWithSpacing,
                      )
                    : Column(
                        children: childrenWithSpacing,
                      ),
              );
            }

            return const SizedBox.shrink();
          },
        ),
      );

  @override
  void dispose() {
    stream = null;
    super.dispose();
  }
}
