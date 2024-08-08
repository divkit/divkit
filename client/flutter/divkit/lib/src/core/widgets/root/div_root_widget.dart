import 'package:divkit/divkit.dart';
import 'package:divkit/src/core/protocol/div_data_provider.dart';
import 'package:divkit/src/core/state/state_id.dart';
import 'package:divkit/src/core/widgets/root/div_root_model.dart';
import 'package:divkit/src/utils/provider.dart';
import 'package:flutter/widgets.dart';

class DivRootWidget extends StatefulWidget {
  final DivRootContext divRootContext;

  const DivRootWidget(
    this.divRootContext, {
    super.key,
  });

  @override
  State<DivRootWidget> createState() => _DivRootWidgetState();
}

class _DivRootWidgetState extends State<DivRootWidget> {
  late DivDataProvider provider;

  @override
  void initState() {
    super.initState();

    provider = widget.divRootContext.dataProvider!;
  }

  @override
  void didUpdateWidget(covariant DivRootWidget oldWidget) {
    super.didUpdateWidget(oldWidget);

    if (widget.divRootContext != oldWidget.divRootContext) {
      provider = widget.divRootContext.dataProvider!;
    }
  }

  @override
  Widget build(BuildContext context) => StreamBuilder<DivData>(
        initialData: provider.value,
        stream: provider.stream,
        builder: (context, snapshot) => DivRenderer(snapshot.requireData),
      );
}

class DivRenderer extends StatefulWidget {
  final DivData data;

  const DivRenderer(this.data, {super.key});

  @override
  State<DivRenderer> createState() => _DivRendererState();
}

class _DivRendererState extends State<DivRenderer> {
  DivRootModel? value;
  Stream<DivRootModel>? stream;

  @override
  void initState() {
    super.initState();

    value = DivRootModel.value(context, widget.data);
  }

  @override
  void didChangeDependencies() {
    super.didChangeDependencies();

    stream ??= DivRootModel.from(context, widget.data);
  }

  @override
  void didUpdateWidget(covariant DivRenderer oldWidget) {
    super.didUpdateWidget(oldWidget);

    if (widget.data != oldWidget.data) {
      value = DivRootModel.value(context, widget.data);
      stream = DivRootModel.from(context, widget.data);
    }
  }

  @override
  Widget build(BuildContext context) => StreamBuilder<DivRootModel>(
        initialData: value,
        stream: stream,
        builder: (context, snapshot) {
          final model = snapshot.requireData;
          return provide(
            DivStateId(model.path),
            child: DivWidget(
              model.state.div,
            ),
          );
        },
      );
}
