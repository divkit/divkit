import 'package:divkit/divkit.dart';
import 'package:divkit/src/core/widgets/base/div_base_model.dart';
import 'package:divkit/src/core/widgets/base/div_decoration.dart';
import 'package:divkit/src/core/widgets/div_visibility_emitter.dart';
import 'package:divkit/src/utils/div_focus_node.dart';
import 'package:divkit/src/utils/provider.dart';
import 'package:flutter/material.dart';

class DivBaseWidget extends StatefulWidget {
  final DivBase data;

  final DivTapActionData? tapActionData;

  final Expression<double>? aspect;

  final Widget child;

  const DivBaseWidget({
    super.key,
    required this.data,
    required this.child,
    this.tapActionData,
    this.aspect,
  });

  @override
  State<DivBaseWidget> createState() => _DivBaseWidgetState();
}

class _DivBaseWidgetState extends State<DivBaseWidget> {
  final key = GlobalKey();

  DivBaseModel? value;
  Stream<DivBaseModel>? stream;

  @override
  void initState() {
    super.initState();
    value = widget.data.resolve(context, aspect: widget.aspect);
  }

  @override
  void didChangeDependencies() {
    super.didChangeDependencies();

    stream ??= watch<DivContext>(context)!.variableManager.watch(
          (values) => widget.data.resolve(context, aspect: widget.aspect),
        );
  }

  @override
  void didUpdateWidget(covariant DivBaseWidget oldWidget) {
    super.didUpdateWidget(oldWidget);

    if (widget.data != oldWidget.data) {
      value = widget.data.resolve(context, aspect: widget.aspect);
      stream = watch<DivContext>(context)!.variableManager.watch(
            (values) => widget.data.resolve(context, aspect: widget.aspect),
          );
    }
  }

  @override
  Widget build(BuildContext context) => StreamBuilder<DivBaseModel>(
        initialData: value,
        stream: stream,
        builder: (context, snapshot) {
          if (snapshot.hasData) {
            final model = snapshot.requireData;
            final focusNode = FocusScope.of(context).getById(widget.data.id);

            if (model.isGone) {
              return const SizedBox.shrink();
            }

            return DivLayout(
              height: model.height,
              width: model.width,
              margin: model.margin,
              aspect: model.aspect,
              alignment: model.alignment,
              child: DivVisibilityEmitter(
                visibilityActions: model.visibilityActions,
                divVisibility: model.divVisibility,
                id: model.divId.toString(),
                child: Opacity(
                  opacity: model.opacity,
                  child: DivTapActionEmitter(
                    data: widget.tapActionData,
                    child: focusNode != null
                        ? AnimatedBuilder(
                            animation: focusNode,
                            builder: (_, __) => DivDecoratedBox(
                              key: key,
                              padding: model.padding,
                              decoration: focusNode.hasFocus
                                  ? model.focusDecoration
                                  : model.decoration,
                              child: widget.child,
                            ),
                          )
                        : DivDecoratedBox(
                            key: key,
                            padding: model.padding,
                            decoration: model.decoration,
                            child: widget.child,
                          ),
                  ),
                ),
              ),
            );
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
