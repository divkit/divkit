import 'package:divkit/divkit.dart';
import 'package:divkit/src/core/widgets/container/div_container_model.dart';
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
  DivContainerModel? value;
  Stream<DivContainerModel>? stream;

  @override
  void initState() {
    super.initState();
    value = DivContainerModel.value(context, widget.data);
  }

  @override
  void didChangeDependencies() {
    super.didChangeDependencies();

    stream ??= DivContainerModel.from(context, widget.data);
  }

  @override
  void didUpdateWidget(covariant DivContainerWidget oldWidget) {
    super.didUpdateWidget(oldWidget);

    if (widget.data != oldWidget.data) {
      value = DivContainerModel.value(context, widget.data);
      stream = DivContainerModel.from(context, widget.data);
    }
  }

  @override
  Widget build(BuildContext context) => DivBaseWidget(
        data: widget.data,
        tapActionData: DivTapActionData(
          action: widget.data.action,
          actions: widget.data.actions,
          longtapActions: widget.data.longtapActions,
          actionAnimation: widget.data.actionAnimation,
        ),
        child: StreamBuilder<DivContainerModel>(
          initialData: value,
          stream: stream,
          builder: (context, snapshot) {
            if (snapshot.hasData) {
              final model = snapshot.requireData;
              final mainWidget = provide(
                model.contentAlignment,
                child: model.contentAlignment.map(
                  flex: (data) => provide(
                    DivParentData.flex,
                    child: Flex(
                      direction: data.direction,
                      mainAxisAlignment: data.mainAxisAlignment,
                      crossAxisAlignment: data.crossAxisAlignment,
                      children: model.children,
                    ),
                  ),
                  wrap: (data) => provide(
                    DivParentData.wrap,
                    child: Wrap(
                      direction: data.direction,
                      alignment: data.wrapAlignment,
                      runAlignment: data.runAlignment,
                      children: model.children,
                    ),
                  ),
                  stack: (data) => provide(
                    DivParentData.stack,
                    child: Stack(
                      alignment: data.contentAlignment ??
                          AlignmentDirectional.topStart,
                      children: model.children,
                    ),
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
    value = null;
    stream = null;
    super.dispose();
  }
}
