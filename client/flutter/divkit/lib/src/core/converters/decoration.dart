import 'package:flutter/widgets.dart';

class DivDecoration {
  final BoxDecoration boxDecoration;
  final List<Widget> backgroundWidgets;
  final CustomBorderRadius customBorderRadius;
  final BoxShadow? outerShadow;

  DivDecoration({
    required this.boxDecoration,
    required this.customBorderRadius,
    this.outerShadow,
    this.backgroundWidgets = const <Widget>[],
  });
}

class CustomBorderRadius {
  final Radius? topLeft;
  final Radius? topRight;
  final Radius? bottomLeft;
  final Radius? bottomRight;

  CustomBorderRadius({
    this.topLeft = Radius.zero,
    this.topRight = Radius.zero,
    this.bottomLeft = Radius.zero,
    this.bottomRight = Radius.zero,
  });

  BorderRadius toBorderRadius() => BorderRadius.only(
        topLeft: topLeft ?? Radius.zero,
        topRight: topRight ?? Radius.zero,
        bottomLeft: bottomLeft ?? Radius.zero,
        bottomRight: bottomRight ?? Radius.zero,
      );
}
