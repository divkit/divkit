import 'package:divkit/src/core/widgets/base/div_base_model.dart';
import 'package:divkit/src/core/widgets/div_tap_action_emitter.dart';
import 'package:divkit/src/generated_sources/div_action.dart';
import 'package:divkit/src/generated_sources/div_base.dart';
import 'package:divkit/src/utils/div_focus_node.dart';
import 'package:divkit/src/utils/size_converters.dart';
import 'package:flutter/widgets.dart';

class DivBaseWidget extends StatefulWidget {
  final DivBase data;

  final List<DivAction> actions;

  final Widget child;

  DivBaseWidget({
    required this.data,
    super.key,
    DivAction? action,
    List<DivAction>? actions,
    required this.child,
  }) : actions = action != null
            ? ((actions ?? const []) + [action])
            : (actions ?? const []);

  @override
  State<DivBaseWidget> createState() => _DivBaseWidgetState();
}

class _DivBaseWidgetState extends State<DivBaseWidget> {
  final key = GlobalKey();

  // ToDo: Optimize repeated calculations on the same context.
  // The model itself is not long-lived, so you need to keep the stream in the state?
  Stream<DivBaseModel>? stream;

  @override
  void didChangeDependencies() {
    super.didChangeDependencies();

    stream ??= DivBaseModel.from(context, widget.data);
  }

  @override
  void didUpdateWidget(covariant DivBaseWidget oldWidget) {
    super.didUpdateWidget(oldWidget);

    if (widget.data != oldWidget.data) {
      stream = DivBaseModel.from(context, widget.data);
    }
  }

  @override
  Widget build(BuildContext context) => StreamBuilder<DivBaseModel>(
        stream: stream,
        builder: (context, snapshot) {
          if (snapshot.hasData) {
            final model = snapshot.requireData;
            final renderObject = context.findRenderObject();
            final focusNode = FocusScope.of(context).getById(model.divId);

            return DivSizeWrapper(
              parentData: renderObject?.parentData,
              height: model.height,
              width: model.width,
              alignment: model.alignment,
              child: IgnorePointer(
                ignoring: !model.isVisible,
                child: Opacity(
                  opacity: model.opacity,
                  child: DivTapActionEmitter(
                    actions: widget.actions,
                    child: focusNode != null
                        ? AnimatedBuilder(
                            animation: focusNode,
                            builder: (_, __) => _DecoratedBox(
                              key: key,
                              padding: model.padding,
                              margin: model.margin,
                              decoration: focusNode.hasFocus
                                  ? model.focusDecoration
                                  : model.boxDecoration,
                              child: RepaintBoundary(child: widget.child),
                            ),
                          )
                        : _DecoratedBox(
                            key: key,
                            padding: model.padding,
                            margin: model.margin,
                            decoration: model.boxDecoration,
                            child: RepaintBoundary(child: widget.child),
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
    stream = null;
    super.dispose();
  }
}

class _DecoratedBox extends StatelessWidget {
  final BoxDecoration? decoration;
  final Widget child;
  final EdgeInsetsGeometry? padding;
  final EdgeInsetsGeometry? margin;

  const _DecoratedBox({
    required this.child,
    this.margin,
    this.padding,
    this.decoration,
    super.key,
  });

  @override
  Widget build(BuildContext context) {
    final image = decoration?.image;
    final gradient = decoration?.gradient;
    final color = decoration?.color;
    return Container(
      margin: margin,
      child: Container(
        decoration: image != null
            ? BoxDecoration(
                border: decoration?.border,
                borderRadius: decoration?.borderRadius,
                boxShadow: decoration?.boxShadow,
                image: image,
              )
            : null,
        child: ClipRRect(
          borderRadius: decoration?.borderRadius ?? BorderRadius.zero,
          child: Container(
            padding: padding,
            decoration: BoxDecoration(
              border: decoration?.border,
              borderRadius: decoration?.borderRadius,
              boxShadow: decoration?.boxShadow,
              gradient: gradient,
              color: color,
            ),
            child: child,
          ),
        ),
      ),
    );
  }
}
