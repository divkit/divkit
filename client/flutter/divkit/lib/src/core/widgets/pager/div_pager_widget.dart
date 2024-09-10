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
    value = DivPagerModel.value(context, widget.data);
    currentPage = widget.data.defaultItem.value ?? 0;
    controller = PageController(
      initialPage: currentPage,
    );
  }

  @override
  void didChangeDependencies() {
    super.didChangeDependencies();

    stream ??= DivPagerModel.from(
      context,
      widget.data,
      controller,
      () => currentPage,
    );
  }

  @override
  void didUpdateWidget(covariant DivPagerWidget oldWidget) {
    super.didUpdateWidget(oldWidget);

    if (widget.data != oldWidget.data) {
      value = DivPagerModel.value(context, widget.data);
      stream = DivPagerModel.from(
        context,
        widget.data,
        controller,
        () => currentPage,
      );
    }
  }

  @override
  Widget build(BuildContext context) => DivBaseWidget(
        data: widget.data,
        child: StreamBuilder<DivPagerModel>(
          initialData: value,
          stream: stream,
          builder: (context, snapshot) {
            if (snapshot.hasData) {
              final model = snapshot.requireData;

              return provide(
                DivParentData.pager,
                child: PageView(
                  scrollDirection: model.orientation,
                  controller: controller,
                  onPageChanged: (value) => onPageChanged(value),
                  children: model.children,
                ),
              );
            }

            return const SizedBox.shrink();
          },
        ),
      );

  void onPageChanged(int value) {
    currentPage = value;
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
