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
    final divContext = read<DivContext>(context)!;
    final initialState = widget.data.states.first.stateId.toString();
    // Register first state
    divContext.stateManager.registerState('root', initialState);
    widget.data.resolve(divContext.variables);
    value = widget.data.bind(initialState);
  }

  @override
  void didChangeDependencies() {
    super.didChangeDependencies();

    if (stream == null) {
      final divContext = watch<DivContext>(context)!;
      stream = divContext.stateManager.watch((state) {
        return widget.data.bind(state['root']!);
      });
    }
  }

  @override
  void didUpdateWidget(covariant DivRenderer oldWidget) {
    super.didUpdateWidget(oldWidget);

    if (widget.data != oldWidget.data) {
      final divContext = watch<DivContext>(context)!;
      final initialState = widget.data.states.first.stateId.toString();
      widget.data.resolve(divContext.variables);
      divContext.stateManager.updateState('root', initialState);
      value = widget.data.bind(initialState);
      stream = divContext.stateManager.watch((state) {
        return widget.data.bind(state['root']!);
      });
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
              // The unique identifier of the state subtree
              key: ValueKey(model.path),
              model.state.div,
            ),
          );
        },
      );

  @override
  void dispose() {
    value = null;
    stream = null;
    super.dispose();
  }
}
