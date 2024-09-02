import 'package:divkit/divkit.dart';
import 'package:divkit/src/core/state/state_id.dart';
import 'package:divkit/src/core/widgets/state/div_state_model.dart';
import 'package:divkit/src/utils/provider.dart';
import 'package:flutter/widgets.dart';

class DivStateWidget extends StatefulWidget {
  final DivState data;

  const DivStateWidget(
    this.data, {
    super.key,
  });

  @override
  State<DivStateWidget> createState() => _DivStateWidgetState();
}

class _DivStateWidgetState extends State<DivStateWidget> {
  DivStateModel? value;

  Stream<DivStateModel>? stream;

  @override
  void initState() {
    super.initState();
    value = DivStateModel.value(context, widget.data);
  }

  @override
  void didChangeDependencies() {
    super.didChangeDependencies();

    stream ??= DivStateModel.from(context, widget.data);
  }

  @override
  void didUpdateWidget(covariant DivStateWidget oldWidget) {
    super.didUpdateWidget(oldWidget);

    if (widget.data != oldWidget.data) {
      value = DivStateModel.value(context, widget.data);
      stream = DivStateModel.from(context, widget.data);
    }
  }

  @override
  Widget build(BuildContext context) => DivBaseWidget(
        data: widget.data,
        child: StreamBuilder<DivStateModel>(
          initialData: value,
          stream: stream,
          builder: (context, snapshot) {
            if (snapshot.hasData) {
              final model = snapshot.requireData;
              return provide(
                DivParentData.none,
                child: provide(
                  DivStateId(model.divId),
                  child: DivWidget(
                    // The unique identifier of the state subtree
                    key: ValueKey(model.path), model.state?.div,
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
