import 'package:divkit/src/core/widgets/base/div_base_widget.dart';
import 'package:divkit/src/core/widgets/container/div_container_model.dart';
import 'package:divkit/src/generated_sources/div_container.dart';
import 'package:divkit/src/utils/content_alignment_converters.dart';
import 'package:divkit/src/utils/provider.dart';
import 'package:flutter/widgets.dart';

class DivContainerWidget extends StatefulWidget {
  final DivContainer data;

  const DivContainerWidget(
    this.data, {
    super.key,
  });

  @override
  State<DivContainerWidget> createState() => _DivContainerWidgetState();
}

class _DivContainerWidgetState extends State<DivContainerWidget> {
  // ToDo:Optimize repeated calculations on the same context.
  // The model itself is not long-lived, so you need to keep the stream in the state?
  Stream<DivContainerModel>? stream;

  @override
  void didChangeDependencies() {
    super.didChangeDependencies();

    stream ??= DivContainerModel.from(context, widget.data);
  }

  @override
  void didUpdateWidget(covariant DivContainerWidget oldWidget) {
    super.didUpdateWidget(oldWidget);

    if (widget.data != oldWidget.data) {
      stream = DivContainerModel.from(context, widget.data);
    }
  }

  @override
  Widget build(BuildContext context) => DivBaseWidget(
        data: widget.data,
        action: widget.data.action,
        actions: widget.data.actions,
        child: StreamBuilder<DivContainerModel>(
          stream: stream,
          builder: (context, snapshot) {
            if (snapshot.hasData) {
              final model = snapshot.requireData;
              final mainWidget = DivKitProvider<ContentAlignment>(
                value: model.contentAlignment,
                child: model.contentAlignment.map(
                  flex: (flexData) => Flex(
                    direction: flexData.direction,
                    mainAxisAlignment: flexData.mainAxisAlignment,
                    crossAxisAlignment: flexData.crossAxisAlignment,
                    mainAxisSize: MainAxisSize.min,
                    children: model.children,
                  ),
                  wrap: (wrapData) => Wrap(
                    direction: wrapData.direction,
                    alignment: wrapData.wrapAlignment,
                    runAlignment: wrapData.runAlignment,
                    children: model.children,
                  ),
                  stack: (stackData) => Stack(
                    alignment: stackData.contentAlignment ??
                        AlignmentDirectional.topStart,
                    children: model.children,
                  ),
                ),
              );

              final aspect = model.aspectRatio;
              if (aspect != null) {
                return AspectRatio(
                  aspectRatio: aspect,
                  child: mainWidget,
                );
              }

              return mainWidget;
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
