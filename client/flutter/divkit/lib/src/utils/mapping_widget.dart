import 'package:flutter/widgets.dart';

abstract class DivMappingWidget<D, M> extends StatefulWidget {
  final D data;

  const DivMappingWidget(
    this.data, {
    super.key,
  });

  M value(BuildContext context);

  Stream<M> stream(BuildContext context);

  Widget build(BuildContext context, M model);

  @override
  State<DivMappingWidget> createState() => _DivMappingWidgetState<D, M>();
}

class _DivMappingWidgetState<D, M> extends State<DivMappingWidget<D, M>> {
  M? value;
  Stream<M>? stream;

  @override
  void initState() {
    super.initState();
    value = widget.value(context);
  }

  @override
  void didChangeDependencies() {
    super.didChangeDependencies();

    stream ??= widget.stream(context);
  }

  @override
  void didUpdateWidget(covariant DivMappingWidget<D, M> oldWidget) {
    super.didUpdateWidget(oldWidget);

    if (widget.data != oldWidget.data) {
      value = widget.value(context);
      stream = widget.stream(context);
    }
  }

  @override
  Widget build(BuildContext context) => StreamBuilder<M>(
        initialData: value,
        stream: stream,
        builder: (context, snapshot) {
          if (snapshot.hasData) {
            final model = snapshot.requireData;
            return widget.build(context, model);
          }
          return const SizedBox.shrink();
        },
      );

  @override
  void dispose() {
    value = null;
    stream = null;
    super.dispose();
  }
}
