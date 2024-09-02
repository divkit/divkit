import 'package:divkit/divkit.dart';
import 'package:divkit/src/core/widgets/base/div_base_model.dart';
import 'package:divkit/src/core/widgets/div_visibility_emitter.dart';
import 'package:divkit/src/utils/content_alignment_converters.dart';
import 'package:divkit/src/utils/div_focus_node.dart';
import 'package:divkit/src/utils/provider.dart';
import 'package:flutter/material.dart';

class DivBaseWidget extends StatefulWidget {
  final DivBase data;

  final DivTapActionData? tapActionData;

  final Widget child;

  const DivBaseWidget({
    required this.data,
    super.key,
    required this.child,
    this.tapActionData,
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
    value = DivBaseModel.value(context, widget.data);
  }

  @override
  void didChangeDependencies() {
    super.didChangeDependencies();

    stream ??= DivBaseModel.from(context, widget.data);
  }

  @override
  void didUpdateWidget(covariant DivBaseWidget oldWidget) {
    super.didUpdateWidget(oldWidget);

    if (widget.data != oldWidget.data) {
      value = DivBaseModel.value(context, widget.data);
      stream = DivBaseModel.from(context, widget.data);
    }
  }

  @override
  Widget build(BuildContext context) => StreamBuilder<DivBaseModel>(
        initialData: value,
        stream: stream,
        builder: (context, snapshot) {
          if (snapshot.hasData) {
            final model = snapshot.requireData;
            final focusNode = FocusScope.of(context).getById(model.divId);
            final parent = watch<DivParentData>(context);
            final contentAlignment = watch<ContentAlignment>(context);

            return DivSizeWrapper(
              parent: parent,
              contentAlignment: contentAlignment,
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
                    data: widget.tapActionData,
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
    value = null;
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
