import 'package:divkit/src/core/widgets/base/div_base_widget.dart';
import 'package:divkit/src/core/widgets/custom/div_custom_model.dart';
import 'package:divkit/src/generated_sources/div_custom.dart';
import 'package:flutter/material.dart';

class DivCustomWidget extends StatefulWidget {
  final DivCustom data;

  const DivCustomWidget(
    this.data, {
    super.key,
  });

  @override
  State<StatefulWidget> createState() => _DivCustomWidget();
}

class _DivCustomWidget extends State<DivCustomWidget> {
  // ToDo: Optimize repeated calculations on the same context
  Stream<DivCustomModel>? stream;

  @override
  void didChangeDependencies() {
    super.didChangeDependencies();

    stream ??= DivCustomModel.from(context, widget.data);
  }

  @override
  void didUpdateWidget(covariant DivCustomWidget oldWidget) {
    super.didUpdateWidget(oldWidget);

    if (widget.data != oldWidget.data) {
      stream = DivCustomModel.from(context, widget.data);
    }
  }

  @override
  Widget build(BuildContext context) => DivBaseWidget(
        data: widget.data,
        child: StreamBuilder<DivCustomModel>(
          stream: stream,
          builder: (context, snapshot) {
            if (snapshot.hasData) {
              final model = snapshot.requireData;

              return model.child;
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
