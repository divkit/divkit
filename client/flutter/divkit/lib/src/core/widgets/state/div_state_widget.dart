import 'package:divkit/src/core/state/div_id_provider.dart';
import 'package:divkit/src/core/widgets/base/div_base_widget.dart';
import 'package:divkit/src/core/widgets/div_widget.dart';
import 'package:divkit/src/core/widgets/state/div_state_model.dart';
import 'package:divkit/src/generated_sources/div_state.dart';
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
  // ToDo: Optimize repeated calculations on the same context.
  // The model itself is not long-lived, so you need to keep the stream in the state?
  Stream<DivStateModel>? stream;

  @override
  void didChangeDependencies() {
    super.didChangeDependencies();

    stream ??= DivStateModel.from(context, widget.data);
  }

  @override
  void didUpdateWidget(covariant DivStateWidget oldWidget) {
    super.didUpdateWidget(oldWidget);

    if (widget.data != oldWidget.data) {
      stream = DivStateModel.from(context, widget.data);
    }
  }

  @override
  Widget build(BuildContext context) => DivBaseWidget(
        data: widget.data,
        child: StreamBuilder<DivStateModel>(
          stream: stream,
          builder: (context, snapshot) {
            if (snapshot.hasData) {
              final model = snapshot.requireData;

              return InheritedDivId(
                divId: model.divId,
                child: DivWidget(
                  model.state?.div,
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
