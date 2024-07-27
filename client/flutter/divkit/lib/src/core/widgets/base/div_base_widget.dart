import 'package:divkit/src/core/widgets/base/div_base_model.dart';
import 'package:divkit/src/core/widgets/div_tap_action_emitter.dart';
import 'package:divkit/src/generated_sources/div_action.dart';
import 'package:divkit/src/generated_sources/div_animation.dart';
import 'package:divkit/src/generated_sources/div_base.dart';
import 'package:divkit/src/utils/div_focus_node.dart';
import 'package:divkit/src/utils/size_converters.dart';
import 'package:flutter/material.dart';
import 'package:divkit/src/core/widgets/div_visibility_emitter.dart';

class DivBaseWidget extends StatefulWidget {
  final DivBase data;

  final List<DivAction> actions;
  final List<DivAction> longtapActions;
  final DivAnimation? actionAnimation;

  final Widget child;

  DivBaseWidget({
    required this.data,
    this.actionAnimation,
    super.key,
    DivAction? action,
    List<DivAction>? actions,
    List<DivAction>? longtapActions,
    required this.child,
  })  : actions = action != null
            ? ((actions ?? const []) + [action])
            : (actions ?? const []),
        longtapActions = longtapActions ?? [];

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
              child: DivVisibilityEmitter(
                visibilityActions: model.visibilityActions,
                divVisibility: model.divVisibility,
                id: model.divId.toString(),
                child: Opacity(
                  opacity: model.opacity,
                  child: DivTapActionEmitter(
                    actions: widget.actions,
                    actionAnimation: widget.actionAnimation,
                    longtapActions: widget.longtapActions,
                    child: focusNode != null
                        ? AnimatedBuilder(
                            animation: focusNode,
                            builder: (_, __) => _DecoratedBox(
                              key: key,
                              padding: model.padding,
                              margin: model.margin,
                              decoration: focusNode.hasFocus
                                  ? model.focusDecoration
                                  : model.decoration,
                              child: RepaintBoundary(
                                child: widget.child,
                              ),
                            ),
                          )
                        : _DecoratedBox(
                            key: key,
                            padding: model.padding,
                            margin: model.margin,
                            decoration: model.decoration,
                            child: RepaintBoundary(
                              child: widget.child,
                            ),
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
  final DivDecoration? decoration;
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
    final backgroundWidgets = decoration?.backgroundWidgets ?? <Widget>[];
    return Container(
      margin: margin,
      child: CustomPaint(
        painter: _OuterShadowPainter(
          outerShadow: decoration?.outerShadow,
          borderRadiusCustom: decoration?.customBorderRadius,
        ),
        child: ClipRRect(
          borderRadius: decoration?.customBorderRadius.toBorderRadius() ??
              BorderRadius.zero,
          child: Stack(
            fit: StackFit.passthrough,
            children: [
              ...backgroundWidgets
                  .map(
                    (widget) => Positioned.fill(
                      child: widget,
                    ),
                  )
                  .toList(),
              Container(
                decoration: BoxDecoration(
                  borderRadius: decoration?.customBorderRadius.toBorderRadius(),
                  border: decoration?.boxDecoration.border,
                ),
                padding: padding,
                child: child,
              ),
            ],
          ),
        ),
      ),
    );
  }
}

class _OuterShadowPainter extends CustomPainter {
  final CustomBorderRadius? borderRadiusCustom;
  final BoxShadow? outerShadow;

  _OuterShadowPainter({
    this.borderRadiusCustom,
    this.outerShadow,
  });

  @override
  void paint(Canvas canvas, Size size) {
    final blurRadius = outerShadow?.blurRadius ?? 0.0;
    final shadowColor = outerShadow?.color ?? Colors.transparent;
    final offset = outerShadow?.offset ?? Offset.zero;
    Path path = Path()
      ..addRRect(
        RRect.fromRectAndCorners(
          Rect.fromLTWH(
            0,
            0,
            size.width,
            size.height,
          ),
          bottomLeft: borderRadiusCustom?.bottomLeft ?? Radius.zero,
          topLeft: borderRadiusCustom?.topLeft ?? Radius.zero,
          bottomRight: borderRadiusCustom?.bottomRight ?? Radius.zero,
          topRight: borderRadiusCustom?.topRight ?? Radius.zero,
        ),
      );

    Path outerPath = Path()
      ..addRRect(
        RRect.fromRectAndCorners(
          Rect.fromLTWH(
            offset.dx,
            offset.dy,
            size.width,
            size.height,
          ),
          bottomLeft: borderRadiusCustom?.bottomLeft ?? Radius.zero,
          topLeft: borderRadiusCustom?.topLeft ?? Radius.zero,
          bottomRight: borderRadiusCustom?.bottomRight ?? Radius.zero,
          topRight: borderRadiusCustom?.topRight ?? Radius.zero,
        ),
      );

    final shadowPaint = Paint()
      ..color = shadowColor
      ..maskFilter = MaskFilter.blur(BlurStyle.normal, blurRadius);

    canvas.saveLayer(Rect.largest, Paint());
    canvas.drawPath(outerPath, shadowPaint);
    canvas.drawPath(
      path,
      Paint()
        ..blendMode = BlendMode.clear
        ..color = Colors.transparent,
    );

    canvas.restore();
  }

  @override
  bool shouldRepaint(_OuterShadowPainter oldDelegate) =>
      outerShadow != oldDelegate.outerShadow;
}
