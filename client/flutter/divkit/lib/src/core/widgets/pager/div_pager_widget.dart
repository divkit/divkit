import 'package:divkit/divkit.dart';
import 'package:divkit/src/core/widgets/pager/div_pager_model.dart';
import 'package:divkit/src/utils/provider.dart';
import 'package:flutter/material.dart';

class DivPagerWidget extends StatefulWidget {
  final DivPager data;

  const DivPagerWidget(
    this.data, {
    super.key,
  });

  @override
  State<DivPagerWidget> createState() => _DivPagerWidgetState();
}

class _DivPagerWidgetState extends State<DivPagerWidget> {
  DivPagerModel? value;
  Stream<DivPagerModel>? stream;

  late int currentPage;

  late PageController controller;

  @override
  void initState() {
    super.initState();
    final divContext = read<DivContext>(context)!;
    final variables = divContext.variables;
    value = widget.data.init(context);

    final length = widget.data.items?.length ?? 0;
    currentPage = widget.data.defaultItem.resolve(variables).clamp(0, length);

    final id = widget.data.id;
    if (id != null && !variables.current.containsKey(id)) {
      divContext.variableManager.putVariable(id, currentPage);
    }

    controller = PageController(initialPage: currentPage);
  }

  @override
  void didChangeDependencies() {
    super.didChangeDependencies();

    stream ??= watch<DivContext>(context)!.variableManager.watch((values) {
      return widget.data.resolve(
        context,
        controller,
        () => currentPage,
      );
    });
  }

  @override
  void didUpdateWidget(covariant DivPagerWidget oldWidget) {
    super.didUpdateWidget(oldWidget);

    if (widget.data != oldWidget.data) {
      value = widget.data.init(context);
      stream = watch<DivContext>(context)!.variableManager.watch(
            (values) => widget.data.resolve(
              context,
              controller,
              () => currentPage,
            ),
          );
    }
  }

  @override
  Widget build(BuildContext context) => StreamBuilder<DivPagerModel>(
        initialData: value,
        stream: stream,
        builder: (context, snapshot) {
          if (snapshot.hasData) {
            final model = snapshot.requireData;

            return DivBaseWidget(
              data: widget.data,
              child: provide(
                DivParentData.pager,
                child: PageView(
                  scrollDirection: model.orientation,
                  controller: controller,
                  onPageChanged: (value) => onPageChanged(value),
                  children: model.children,
                ),
              ),
            );
          }

          return const SizedBox.shrink();
        },
      );

  void onPageChanged(int value) {
    final length = widget.data.items?.length ?? 0;
    currentPage = value.clamp(0, length);
    final id = widget.data.id;
    if (id != null) {
      final divContext = watch<DivContext>(context)!;
      divContext.variableManager.updateVariable(id, currentPage);
    }
  }

  @override
  void dispose() {
    controller.dispose();
    value = null;
    stream = null;
    super.dispose();
  }
}
