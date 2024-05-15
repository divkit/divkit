import 'package:divkit/src/core/state/div_id_provider.dart';
import 'package:divkit/src/core/widgets/card_state/div_card_state_model.dart';
import 'package:divkit/src/core/widgets/div_widget.dart';
import 'package:divkit/src/generated_sources/div_data.dart';
import 'package:flutter/widgets.dart';

class DivCardStateWidget extends StatefulWidget {
  final DivData data;

  const DivCardStateWidget(
    this.data, {
    super.key,
  });

  @override
  State<DivCardStateWidget> createState() => _DivCardStateWidgetState();
}

class _DivCardStateWidgetState extends State<DivCardStateWidget> {
  // ToDo:Optimize repeated calculations on the same context.
  // The model itself is not long-lived, so you need to keep the stream in the state?
  Stream<DivCardStateModel>? stream;

  @override
  void didChangeDependencies() {
    super.didChangeDependencies();

    stream ??= DivCardStateModel.from(context, widget.data);
  }

  @override
  void didUpdateWidget(covariant DivCardStateWidget oldWidget) {
    super.didUpdateWidget(oldWidget);

    if (widget.data != oldWidget.data) {
      stream = DivCardStateModel.from(context, widget.data);
    }
  }

  @override
  Widget build(BuildContext context) => StreamBuilder<DivCardStateModel>(
        stream: stream?.asBroadcastStream(),
        builder: (context, snapshot) {
          if (snapshot.hasData) {
            final model = snapshot.requireData;

            return InheritedDivId(
              divId: model.path,
              child: DivWidget(
                model.state.div,
              ),
            );
          }

          return const SizedBox.shrink();
        },
      );

  @override
  void dispose() {
    stream = null;
    super.dispose();
  }
}
