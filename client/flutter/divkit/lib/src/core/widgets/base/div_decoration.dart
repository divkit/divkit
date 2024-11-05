import 'package:divkit/src/core/converters/decoration.dart';
import 'package:flutter/widgets.dart';

class DivDecoratedBox extends StatelessWidget {
  final DivDecoration? decoration;
  final Widget child;
  final EdgeInsetsGeometry? padding;

  const DivDecoratedBox({
    required this.child,
    this.padding,
    this.decoration,
    super.key,
  });

  @override
  Widget build(BuildContext context) {
    final backgroundWidgets = decoration?.backgroundWidgets ?? <Widget>[];
    return CustomPaint(
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
    final shadowColor = outerShadow?.color ?? const Color(0x00000000);
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
        ..color = const Color(0x00000000),
    );

    canvas.restore();
  }

  @override
  bool shouldRepaint(_OuterShadowPainter oldDelegate) =>
      outerShadow != oldDelegate.outerShadow;
}
