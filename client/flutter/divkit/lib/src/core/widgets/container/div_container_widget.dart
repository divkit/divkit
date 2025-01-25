import 'package:divkit/divkit.dart';
import 'package:divkit/src/core/widgets/container/div_container_model.dart';
import 'package:divkit/src/utils/mapping_widget.dart';
import 'package:divkit/src/utils/provider.dart';
import 'package:flutter/widgets.dart';

class DivContainerWidget
    extends DivMappingWidget<DivContainer, DivContainerModel> {
  const DivContainerWidget(
    super.data, {
    super.key,
  });

  @override
  DivContainerModel value(BuildContext context) => data.resolve(context);

  @override
  Stream<DivContainerModel> stream(BuildContext context) =>
      watch<DivContext>(context)!.variableManager.watch(
            (values) => data.resolve(context),
          );

  @override
  Widget build(BuildContext context, DivContainerModel model) {
    final mainWidget = model.contentAlignment.map(
      flex: (data) => provide(
        data.direction == Axis.vertical
            ? DivParentData.column
            : DivParentData.row,
        child: Flex(
          mainAxisSize: MainAxisSize.min,
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
          alignment: data.contentAlignment ?? AlignmentDirectional.topStart,
          children: model.children,
        ),
      ),
    );

    return DivBaseWidget(
      data: data,
      aspect: data.aspect?.ratio,
      tapActionData: DivTapActionData(
        action: data.action,
        actions: data.actions,
        longtapActions: data.longtapActions,
        actionAnimation: data.actionAnimation,
      ),
      child: mainWidget,
    );
  }
}
